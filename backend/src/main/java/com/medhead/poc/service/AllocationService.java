package com.medhead.poc.service;

import com.medhead.poc.model.AllocationRequest;
import com.medhead.poc.model.AllocationResponse;
import com.medhead.poc.model.Hospital;
import com.medhead.poc.model.Patient;
import com.medhead.poc.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
    
    /**
     * Finds the most appropriate hospital for an allocation request and records the patient.
     * 
     * @param request The allocation request
     * @return The response with the recommended hospital
     * @throws RuntimeException if no appropriate hospital is found
     */
    public AllocationResponse findBestHospital(AllocationRequest request) {
        // Parameter validation
        if (request.getSpecialty() == null || request.getSpecialty().trim().isEmpty()) {
            throw new IllegalArgumentException("Specialty is required");
        }
        
        if (request.getLatitude() == null || request.getLongitude() == null) {
            throw new IllegalArgumentException("Geolocation is required");
        }
        
        // Search for hospitals with the requested specialty and available beds
        List<Hospital> eligibleHospitals = hospitalRepository
            .findBySpecialtyAndAvailableBeds(request.getSpecialty());
        
        if (eligibleHospitals.isEmpty()) {
            throw new RuntimeException("No hospital available with specialty '" + 
                                    request.getSpecialty() + "'");
        }
        
        // Calculate distances and sort by distance
        List<HospitalWithDistance> hospitalsWithDistance = eligibleHospitals.stream()
            .map(hospital -> {
                double distance = distanceService.calculateDistanceToHospital(
                    request.getLatitude(), 
                    request.getLongitude(), 
                    hospital
                );
                return new HospitalWithDistance(hospital, distance);
            })
            .sorted(Comparator.comparingDouble(HospitalWithDistance::getDistance))
            .collect(Collectors.toList());
        
        // Select the best hospital (closest)
        HospitalWithDistance bestHospital = hospitalsWithDistance.get(0);
        Hospital selectedHospital = bestHospital.getHospital();
        double distance = bestHospital.getDistance();
        
        // Calculate estimated travel time
        int estimatedTime = distanceService.estimateTravelTime(distance);
        
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
        
        return response;
    }
    
    /**
     * Internal class to store a hospital with its distance.
     */
    private static class HospitalWithDistance {
        private final Hospital hospital;
        private final double distance;
        
        public HospitalWithDistance(Hospital hospital, double distance) {
            this.hospital = hospital;
            this.distance = distance;
        }
        
        public Hospital getHospital() {
            return hospital;
        }
        
        public double getDistance() {
            return distance;
        }
    }
}
