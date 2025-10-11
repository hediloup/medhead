package com.medhead.poc.service;

import com.medhead.poc.model.Patient;
import com.medhead.poc.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for patient data anonymization and protection in accordance with GDPR.
 */
@Service
@Transactional
public class PatientAnonymizationService {
    
    @Autowired
    private PatientRepository patientRepository;
    
    /**
     * Anonymizes a patient by replacing sensitive data with anonymous identifiers
     */
    public Patient anonymizePatient(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null");
        }
        
        // Generate anonymized name if not already done
        if (patient.getAnonymizedName() == null || patient.getAnonymizedName().isEmpty()) {
            patient.generateAnonymizedName();
        }
        
        // Mark as anonymized
        patient.setIsAnonymized(true);
        
        // Clean additional sensitive data if necessary
        // (the Patient model already stores only anonymized data)
        
        return patientRepository.save(patient);
    }
    
    /**
     * Creates an anonymized patient directly (recommended for API)
     */
    public Patient createAnonymizedPatient(String requiredSpecialty, 
                                         Double latitude, 
                                         Double longitude,
                                         String severityLevel) {
        
        Patient patient = new Patient(requiredSpecialty, latitude, longitude);
        
        // Generate anonymized name immediately
        patient.generateAnonymizedName();
        
        // Set severity level
        if (severityLevel != null) {
            patient.setSeverityLevel(severityLevel);
        } else {
            patient.setSeverityLevel("MEDIUM"); // Default value
        }
        
        // Mark as anonymized from creation
        patient.setIsAnonymized(true);
        
        return patientRepository.save(patient);
    }
    
    /**
     * Anonymizes all non-anonymized patients
     */
    public int anonymizeAllNonAnonymizedPatients() {
        List<Patient> nonAnonymizedPatients = patientRepository.findNonAnonymizedPatients();
        int count = 0;
        
        for (Patient patient : nonAnonymizedPatients) {
            anonymizePatient(patient);
            count++;
        }
        
        return count;
    }
    
    /**
     * Permanently deletes expired data (GDPR compliance)
     */
    public int deleteExpiredPatients() {
        LocalDateTime currentDate = LocalDateTime.now();
        return patientRepository.deleteExpiredPatients(currentDate);
    }
    
    /**
     * Finds and anonymizes patients with missing sensitive data
     */
    public int anonymizePatientsWithMissingData() {
        List<Patient> patientsToAnonymize = patientRepository.findPatientsWithMissingAnonymization();
        int count = 0;
        
        for (Patient patient : patientsToAnonymize) {
            anonymizePatient(patient);
            count++;
        }
        
        return count;
    }
    
    /**
     * Scheduled task: Automatic cleanup of expired data (executed daily at 2 AM)
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduledDataCleanup() {
        try {
            int deletedCount = deleteExpiredPatients();
            System.out.println("Automatic cleanup: " + deletedCount + " expired patients deleted");
            
            int anonymizedCount = anonymizePatientsWithMissingData();
            System.out.println("Automatic anonymization: " + anonymizedCount + " patients anonymized");
            
        } catch (Exception e) {
            System.err.println("Error during automatic data cleanup: " + e.getMessage());
        }
    }
    
    /**
     * Generates a unique anonymous identifier for a patient
     */
    public String generateAnonymousId() {
        return "PATIENT_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * Checks if a patient can be accessed (not expired and anonymized)
     */
    public boolean canAccessPatient(Patient patient) {
        if (patient == null) {
            return false;
        }
        
        // Check if data is expired
        if (patient.isDataExpired()) {
            return false;
        }
        
        // Check if patient is anonymized
        if (!patient.getIsAnonymized()) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Gets anonymized statistics about patients
     */
    public PatientStatistics getAnonymizedStatistics() {
        List<Object[]> specialtyCounts = patientRepository.countPatientsBySpecialty();
        List<Object[]> ageGroupCounts = patientRepository.countPatientsByAgeGroup();
        
        PatientStatistics stats = new PatientStatistics();
        stats.setSpecialtyCounts(specialtyCounts);
        stats.setAgeGroupCounts(ageGroupCounts);
        stats.setGeneratedAt(LocalDateTime.now());
        
        return stats;
    }
    
    /**
     * Class for anonymized statistics
     */
    public static class PatientStatistics {
        private List<Object[]> specialtyCounts;
        private List<Object[]> ageGroupCounts;
        private LocalDateTime generatedAt;
        
        // Getters and Setters
        public List<Object[]> getSpecialtyCounts() {
            return specialtyCounts;
        }
        
        public void setSpecialtyCounts(List<Object[]> specialtyCounts) {
            this.specialtyCounts = specialtyCounts;
        }
        
        public List<Object[]> getAgeGroupCounts() {
            return ageGroupCounts;
        }
        
        public void setAgeGroupCounts(List<Object[]> ageGroupCounts) {
            this.ageGroupCounts = ageGroupCounts;
        }
        
        public LocalDateTime getGeneratedAt() {
            return generatedAt;
        }
        
        public void setGeneratedAt(LocalDateTime generatedAt) {
            this.generatedAt = generatedAt;
        }
    }
}
