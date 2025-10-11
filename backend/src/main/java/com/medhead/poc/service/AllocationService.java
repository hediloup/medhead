package com.medhead.poc.service;

import com.medhead.poc.dto.RouteResult;
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
        
        // Calculate routes with traffic optimization and sort by travel time
        List<HospitalWithRoute> hospitalsWithRoute = eligibleHospitals.stream()
            .map(hospital -> {
                RouteResult routeResult = distanceService.calculateOptimalRouteToHospital(
                    request.getLatitude(), 
                    request.getLongitude(), 
                    hospital
                );
                return new HospitalWithRoute(hospital, routeResult);
            })
            .filter(hwr -> !hwr.getRouteResult().isError()) // Filter out hospitals with route errors
            .sorted(Comparator.comparingInt(hwr -> hwr.getRouteResult().getOptimalDurationMinutes()))
            .collect(Collectors.toList());
        
        if (hospitalsWithRoute.isEmpty()) {
            throw new RuntimeException("No accessible hospital found with specialty '" + 
                                    request.getSpecialty() + "'");
        }
        
        // Select the best hospital (fastest travel time)
        HospitalWithRoute bestHospital = hospitalsWithRoute.get(0);
        Hospital selectedHospital = bestHospital.getHospital();
        RouteResult routeResult = bestHospital.getRouteResult();
        
        double distance = routeResult.getDistanceKm();
        int estimatedTime = routeResult.getOptimalDurationMinutes();
        
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
     * Internal class to store a hospital with its route information.
     */
    private static class HospitalWithRoute {
        private final Hospital hospital;
        private final RouteResult routeResult;
        
        public HospitalWithRoute(Hospital hospital, RouteResult routeResult) {
            this.hospital = hospital;
            this.routeResult = routeResult;
        }
        
        public Hospital getHospital() {
            return hospital;
        }
        
        public RouteResult getRouteResult() {
            return routeResult;
        }
    }
}
