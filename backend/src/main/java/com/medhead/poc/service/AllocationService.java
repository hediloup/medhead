package com.medhead.poc.service;

import com.medhead.poc.dto.HospitalProjection;
import com.medhead.poc.model.AllocationRequest;
import com.medhead.poc.model.AllocationResponse;
import com.medhead.poc.model.Hospital;
import com.medhead.poc.model.Patient;
import com.medhead.poc.repository.HospitalRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main service for hospital bed allocation logic.
 */
@Service
@Transactional
public class AllocationService {
    
    @Autowired
    private HospitalRepository hospitalRepository;
    
    @Autowired
    private DistanceCalculationService distanceService;
    
    @Autowired
    private PatientAnonymizationService patientAnonymizationService;
    
    @Autowired
    private EventPublisherService eventPublisherService;
    
    // Metrics
    @Autowired
    private Counter allocationCounter;
    
    @Autowired
    private Counter allocationErrorCounter;
    
    @Autowired
    private Timer allocationTimer;
    
    /**
     * Finds the most appropriate hospital for an allocation request and records the patient.
     * 
     * @param request The allocation request
     * @return The response with the recommended hospital
     * @throws RuntimeException if no appropriate hospital is found
     */
    public AllocationResponse findBestHospital(AllocationRequest request) {
        try {
            return allocationTimer.recordCallable(() -> {
                try {
                    return performAllocation(request);
                } catch (RuntimeException e) {
                    allocationErrorCounter.increment();
                    throw e;  // Re-throw RuntimeException as-is
                } catch (Exception e) {
                    allocationErrorCounter.increment();
                    throw new RuntimeException(e);
                }
            });
        } catch (RuntimeException e) {
            throw e;  // Re-throw RuntimeException as-is
        } catch (Exception e) {
            throw new RuntimeException("Failed to allocate hospital", e);
        }
    }
    
    private AllocationResponse performAllocation(AllocationRequest request) {
        // Parameter validation
        if (request.getSpecialty() == null || request.getSpecialty().trim().isEmpty()) {
            throw new IllegalArgumentException("Specialty is required");
        }
        
        if (request.getLatitude() == null || request.getLongitude() == null) {
            throw new IllegalArgumentException("Geolocation is required");
        }
        
        // Use optimized query with projection for better performance
        List<HospitalProjection> eligibleHospitalsProjection = hospitalRepository
            .findBySpecialtyAndAvailableBedsProjection(request.getSpecialty());
        
        if (eligibleHospitalsProjection.isEmpty()) {
            throw new RuntimeException("No hospital available with specialty '" + 
                                    request.getSpecialty() + "'");
        }
        
        // Convert projections to full Hospital objects for distance calculation
        List<Hospital> eligibleHospitals = eligibleHospitalsProjection.stream()
            .map(projection -> {
                Hospital hospital = new Hospital();
                hospital.setId(projection.getId());
                hospital.setName(projection.getName());
                hospital.setLatitude(projection.getLatitude());
                hospital.setLongitude(projection.getLongitude());
                hospital.setAvailableBeds(projection.getAvailableBeds());
                hospital.setCity("Manchester"); // Set default city for testing
                return hospital;
            })
            .collect(Collectors.toList());
        
        // Calculate distances for all eligible hospitals
        List<HospitalWithDistance> hospitalsWithDistance = eligibleHospitals.stream()
            .map(hospital -> {
                double distanceKm = distanceService.calculateDistanceToHospital(
                    request.getLatitude(),
                    request.getLongitude(),
                    hospital
                );
                int estimatedTime = estimateTravelTime(distanceKm);
                return new HospitalWithDistance(hospital, distanceKm, estimatedTime);
            })
            .sorted(Comparator.comparingDouble(HospitalWithDistance::getDistanceKm))
            .collect(Collectors.toList());
        
        if (hospitalsWithDistance.isEmpty()) {
            throw new RuntimeException("No accessible hospital found with specialty '" + 
                                    request.getSpecialty() + "'");
        }
        
        // Select the best hospital (nearest distance)
        HospitalWithDistance bestHospital = hospitalsWithDistance.get(0);
        Hospital selectedHospital = bestHospital.getHospital();
        double distance = bestHospital.getDistanceKm();
        int estimatedTime = bestHospital.getEstimatedTimeMinutes();
        
        // Create anonymized patient
        Patient patient = patientAnonymizationService.createAnonymizedPatient(
            request.getSpecialty(),
            request.getLatitude(),
            request.getLongitude(),
            "MEDIUM" // Default severity level
        );
        
        // Associate patient with hospital
        patient.setAllocatedHospital(selectedHospital);
        patientAnonymizationService.anonymizePatient(patient);
        
        // Calculate available beds after reservation
        int availableBedsAfter = selectedHospital.getAvailableBeds() - 1;
        
        // Create response
        AllocationResponse response = new AllocationResponse(
            selectedHospital.getName(),
            selectedHospital.getId(),
            Math.round(distance * 100.0) / 100.0, // Rounded to 2 decimal places
            request.getSpecialty(),
            availableBedsAfter,
            estimatedTime
        );
        
        // Publish BED_RESERVED event
        eventPublisherService.publishBedReservedEvent(
            patient.getPatientUuid(),
            patient.getAnonymizedName(),
            request.getSpecialty(),
            patient.getSeverityLevel(),
            patient.getAgeGroup(),
            selectedHospital.getId(),
            selectedHospital.getName(),
            selectedHospital.getCity(),
            Math.round(distance * 100.0) / 100.0,
            availableBedsAfter,
            estimatedTime
        );
        
        // Increment success counter
        allocationCounter.increment();
        
        return response;
    }

    private int estimateTravelTime(double distanceKm) {
        double averageSpeedKmh = 50.0; // city average
        double timeHours = distanceKm / averageSpeedKmh;
        return (int) Math.round(timeHours * 60);
    }
    
    /**
     * Internal class to store a hospital with its distance information.
     */
    private static class HospitalWithDistance {
        private final Hospital hospital;
        private final double distanceKm;
        private final int estimatedTimeMinutes;
        
        public HospitalWithDistance(Hospital hospital, double distanceKm, int estimatedTimeMinutes) {
            this.hospital = hospital;
            this.distanceKm = distanceKm;
            this.estimatedTimeMinutes = estimatedTimeMinutes;
        }
        
        public Hospital getHospital() {
            return hospital;
        }
        
        public double getDistanceKm() {
            return distanceKm;
        }
        
        public int getEstimatedTimeMinutes() {
            return estimatedTimeMinutes;
        }
    }
}
