package com.medhead.poc.repository;

import com.medhead.poc.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for secure patient data management.
 * Implements GDPR and data protection best practices.
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    /**
     * Finds a patient by their UUID (anonymous identifier)
     */
    Optional<Patient> findByPatientUuid(String patientUuid);
    
    /**
     * Finds all non-anonymized patients
     */
    @Query("SELECT p FROM Patient p WHERE p.isAnonymized = false")
    List<Patient> findNonAnonymizedPatients();
    
    /**
     * Finds patients whose data has expired according to retention policy
     */
    @Query("SELECT p FROM Patient p WHERE p.dataRetentionUntil < :currentDate")
    List<Patient> findExpiredPatients(@Param("currentDate") LocalDateTime currentDate);
    
    /**
     * Finds patients by required specialty
     */
    @Query("SELECT p FROM Patient p WHERE p.requiredSpecialty = :specialty AND p.isAnonymized = false")
    List<Patient> findByRequiredSpecialty(@Param("specialty") String specialty);
    
    /**
     * Finds patients by age group (for anonymized statistics)
     */
    @Query("SELECT p FROM Patient p WHERE p.ageGroup = :ageGroup")
    List<Patient> findByAgeGroup(@Param("ageGroup") String ageGroup);
    
    /**
     * Finds patients by severity level
     */
    @Query("SELECT p FROM Patient p WHERE p.severityLevel = :severity AND p.isAnonymized = false")
    List<Patient> findBySeverityLevel(@Param("severity") String severity);
    
    /**
     * Finds patients by allocated hospital
     */
    @Query("SELECT p FROM Patient p WHERE p.allocatedHospital.id = :hospitalId")
    List<Patient> findByAllocatedHospital(@Param("hospitalId") Long hospitalId);
    
    /**
     * Finds patients created in a given period (for anonymized reports)
     */
    @Query("SELECT p FROM Patient p WHERE p.createdAt BETWEEN :startDate AND :endDate")
    List<Patient> findPatientsCreatedBetween(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);
    
    /**
     * Counts the number of patients by specialty (for statistics)
     */
    @Query("SELECT p.requiredSpecialty, COUNT(p) FROM Patient p WHERE p.isAnonymized = false GROUP BY p.requiredSpecialty")
    List<Object[]> countPatientsBySpecialty();
    
    /**
     * Counts the number of patients by age group (for statistics)
     */
    @Query("SELECT p.ageGroup, COUNT(p) FROM Patient p GROUP BY p.ageGroup")
    List<Object[]> countPatientsByAgeGroup();
    
    /**
     * Finds patients by postal code (for anonymized geographic analyses)
     */
    @Query("SELECT p FROM Patient p WHERE p.postalCode = :postalCode")
    List<Patient> findByPostalCode(@Param("postalCode") String postalCode);
    
    /**
     * Marks a patient as anonymized (logical deletion)
     */
    @Modifying
    @Query("UPDATE Patient p SET p.isAnonymized = true, p.anonymizedName = :anonymizedName WHERE p.id = :patientId")
    int anonymizePatient(@Param("patientId") Long patientId, @Param("anonymizedName") String anonymizedName);
    
    /**
     * Permanently deletes expired data (GDPR compliance)
     */
    @Modifying
    @Query("DELETE FROM Patient p WHERE p.dataRetentionUntil < :currentDate")
    int deleteExpiredPatients(@Param("currentDate") LocalDateTime currentDate);
    
    /**
     * Updates a patient's retention date
     */
    @Modifying
    @Query("UPDATE Patient p SET p.dataRetentionUntil = :newRetentionDate WHERE p.id = :patientId")
    int updateDataRetention(@Param("patientId") Long patientId, @Param("newRetentionDate") LocalDateTime newRetentionDate);
    
    /**
     * Finds patients with non-anonymized sensitive data
     */
    @Query("SELECT p FROM Patient p WHERE p.isAnonymized = false AND (p.anonymizedName IS NULL OR p.anonymizedName = '')")
    List<Patient> findPatientsWithMissingAnonymization();
    
    /**
     * Checks the existence of a patient by UUID without exposing their data
     */
    boolean existsByPatientUuid(String patientUuid);
    
    /**
     * Finds patients by geographic proximity (for anonymized analyses)
     */
    @Query("SELECT p FROM Patient p WHERE " +
           "6371 * acos(cos(radians(:latitude)) * cos(radians(p.latitude)) * " +
           "cos(radians(p.longitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(p.latitude))) <= :radiusKm")
    List<Patient> findPatientsWithinRadius(@Param("latitude") Double latitude, 
                                          @Param("longitude") Double longitude, 
                                          @Param("radiusKm") Double radiusKm);
}
