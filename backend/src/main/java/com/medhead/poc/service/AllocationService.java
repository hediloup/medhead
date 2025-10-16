package com.medhead.poc.service;

import com.medhead.poc.dto.RouteResult;
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
        
        // Ultra-optimized strategy: Haversine selection only (no Google Maps calls in backend)
        Hospital selectedHospital = eligibleHospitals.stream()
            .min(Comparator.comparingDouble(hospital -> 
                distanceService.calculateDistanceToHospital(
                    request.getLatitude(), 
                    request.getLongitude(), 
                    hospital
                )))
            .orElseThrow(() -> new RuntimeException("No accessible hospital found with specialty '" + 
                                    request.getSpecialty() + "'"));
        
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
        
        // Create response with hospital coordinates (frontend will handle Google Maps)
        AllocationResponse response = new AllocationResponse(
            selectedHospital.getName(),
            selectedHospital.getId(),
            selectedHospital.getLatitude(),  // Hospital latitude for frontend
            selectedHospital.getLongitude(), // Hospital longitude for frontend
            request.getSpecialty(),
            availableBedsAfter
        );
        
        // Publish BED_RESERVED event (without distance/time - handled by frontend)
        eventPublisherService.publishBedReservedEvent(
            patient.getPatientUuid(),
            patient.getAnonymizedName(),
            request.getSpecialty(),
            patient.getSeverityLevel(),
            patient.getAgeGroup(),
            selectedHospital.getId(),
            selectedHospital.getName(),
            selectedHospital.getCity(),
            availableBedsAfter
        );
        
        // Increment success counter
        allocationCounter.increment();
        
        return response;
    }
    
}
