package com.medhead.poc.controller;

import com.medhead.poc.model.AllocationRequest;
import com.medhead.poc.model.AllocationResponse;
import com.medhead.poc.service.AllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for hospital bed allocation API.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // To allow calls from frontend
public class AllocationController {
    
    @Autowired
    private AllocationService allocationService;
    
    /**
     * Endpoint to get a hospital recommendation.
     * 
     * @param request The allocation request containing specialty and geolocation
     * @return The response with the recommended hospital
     */
    @PostMapping("/allocate")
    public ResponseEntity<AllocationResponse> allocateHospital(@RequestBody AllocationRequest request) {
        try {
            AllocationResponse response = allocationService.findBestHospital(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Parameter validation error
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            // No hospital found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            // Server error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET endpoint to test the API with query string parameters.
     * Useful for testing and demonstration.
     * 
     * @param specialty The medical specialty
     * @param latitude Patient latitude
     * @param longitude Patient longitude
     * @return The response with the recommended hospital
     */
    @GetMapping("/allocate")
    public ResponseEntity<AllocationResponse> allocateHospitalGet(
            @RequestParam String specialty,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        
        AllocationRequest request = new AllocationRequest(specialty, latitude, longitude);
        return allocateHospital(request);
    }
    
    /**
     * Health endpoint to verify that the API is operational.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Allocation API operational");
    }
    
    /**
     * Test endpoint to diagnose allocation issues.
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        try {
            AllocationRequest request = new AllocationRequest("Cardiology", 53.3976314, -2.1829641);
            AllocationResponse response = allocationService.findBestHospital(request);
            return ResponseEntity.ok("Test successful: " + response.getHospitalName());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Test failed: " + e.getMessage());
        }
    }
}
