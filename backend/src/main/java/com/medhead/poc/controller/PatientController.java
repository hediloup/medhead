package com.medhead.poc.controller;

import com.medhead.poc.model.Patient;
import com.medhead.poc.service.PatientAnonymizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller for secure patient data management.
 * Implements GDPR and data protection best practices.
 */
@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
public class PatientController {
    
    @Autowired
    private PatientAnonymizationService patientAnonymizationService;
    
    /**
     * Gets anonymized patient statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PatientAnonymizationService.PatientStatistics> getPatientStatistics() {
        try {
            PatientAnonymizationService.PatientStatistics stats = patientAnonymizationService.getAnonymizedStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Anonymizes all non-anonymized patients
     */
    @PostMapping("/anonymize-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> anonymizeAllPatients() {
        try {
            int count = patientAnonymizationService.anonymizeAllNonAnonymizedPatients();
            return ResponseEntity.ok("Anonymization successful: " + count + " patients processed");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error during anonymization: " + e.getMessage());
        }
    }
    
    /**
     * Deletes expired data (GDPR compliance)
     */
    @DeleteMapping("/cleanup-expired")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> cleanupExpiredPatients() {
        try {
            int count = patientAnonymizationService.deleteExpiredPatients();
            return ResponseEntity.ok("Cleanup successful: " + count + " expired patients deleted");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error during cleanup: " + e.getMessage());
        }
    }
    
    /**
     * Gets anonymized information of a patient by their UUID
     */
    @GetMapping("/{patientUuid}")
    @PreAuthorize("hasRole('MEDICAL_STAFF')")
    public ResponseEntity<Patient> getPatientByUuid(@PathVariable String patientUuid) {
        try {
            // Note: This method should be implemented in the service
            // For now, we return an error because the service does not implement it yet
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Checks if a patient exists by their UUID
     */
    @GetMapping("/exists/{patientUuid}")
    @PreAuthorize("hasRole('MEDICAL_STAFF')")
    public ResponseEntity<Boolean> patientExists(@PathVariable String patientUuid) {
        try {
            // Note: This method should be implemented in the service
            // For now, we return false
            return ResponseEntity.ok(false);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Health endpoint for patient controller
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Patient Controller operational - Data protected by GDPR");
    }
}
