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
 * Repository pour la gestion sécurisée des données patients.
 * Implémente les bonnes pratiques RGPD et de protection des données.
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    /**
     * Trouve un patient par son UUID (identifiant anonyme)
     */
    Optional<Patient> findByPatientUuid(String patientUuid);
    
    /**
     * Trouve tous les patients non anonymisés
     */
    @Query("SELECT p FROM Patient p WHERE p.isAnonymized = false")
    List<Patient> findNonAnonymizedPatients();
    
    /**
     * Trouve les patients dont les données ont expiré selon la politique de rétention
     */
    @Query("SELECT p FROM Patient p WHERE p.dataRetentionUntil < :currentDate")
    List<Patient> findExpiredPatients(@Param("currentDate") LocalDateTime currentDate);
    
    /**
     * Trouve les patients par spécialité requise
     */
    @Query("SELECT p FROM Patient p WHERE p.requiredSpecialty = :specialty AND p.isAnonymized = false")
    List<Patient> findByRequiredSpecialty(@Param("specialty") String specialty);
    
    /**
     * Trouve les patients par groupe d'âge (pour statistiques anonymisées)
     */
    @Query("SELECT p FROM Patient p WHERE p.ageGroup = :ageGroup")
    List<Patient> findByAgeGroup(@Param("ageGroup") String ageGroup);
    
    /**
     * Trouve les patients par niveau de gravité
     */
    @Query("SELECT p FROM Patient p WHERE p.severityLevel = :severity AND p.isAnonymized = false")
    List<Patient> findBySeverityLevel(@Param("severity") String severity);
    
    /**
     * Trouve les patients par hôpital alloué
     */
    @Query("SELECT p FROM Patient p WHERE p.allocatedHospital.id = :hospitalId")
    List<Patient> findByAllocatedHospital(@Param("hospitalId") Long hospitalId);
    
    /**
     * Trouve les patients créés dans une période donnée (pour rapports anonymisés)
     */
    @Query("SELECT p FROM Patient p WHERE p.createdAt BETWEEN :startDate AND :endDate")
    List<Patient> findPatientsCreatedBetween(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);
    
    /**
     * Compte le nombre de patients par spécialité (pour statistiques)
     */
    @Query("SELECT p.requiredSpecialty, COUNT(p) FROM Patient p WHERE p.isAnonymized = false GROUP BY p.requiredSpecialty")
    List<Object[]> countPatientsBySpecialty();
    
    /**
     * Compte le nombre de patients par groupe d'âge (pour statistiques)
     */
    @Query("SELECT p.ageGroup, COUNT(p) FROM Patient p GROUP BY p.ageGroup")
    List<Object[]> countPatientsByAgeGroup();
    
    /**
     * Trouve les patients par code postal (pour analyses géographiques anonymisées)
     */
    @Query("SELECT p FROM Patient p WHERE p.postalCode = :postalCode")
    List<Patient> findByPostalCode(@Param("postalCode") String postalCode);
    
    /**
     * Marque un patient comme anonymisé (suppression logique)
     */
    @Modifying
    @Query("UPDATE Patient p SET p.isAnonymized = true, p.anonymizedName = :anonymizedName WHERE p.id = :patientId")
    int anonymizePatient(@Param("patientId") Long patientId, @Param("anonymizedName") String anonymizedName);
    
    /**
     * Supprime définitivement les données expirées (conformité RGPD)
     */
    @Modifying
    @Query("DELETE FROM Patient p WHERE p.dataRetentionUntil < :currentDate")
    int deleteExpiredPatients(@Param("currentDate") LocalDateTime currentDate);
    
    /**
     * Met à jour la date de rétention d'un patient
     */
    @Modifying
    @Query("UPDATE Patient p SET p.dataRetentionUntil = :newRetentionDate WHERE p.id = :patientId")
    int updateDataRetention(@Param("patientId") Long patientId, @Param("newRetentionDate") LocalDateTime newRetentionDate);
    
    /**
     * Trouve les patients avec des données sensibles non anonymisées
     */
    @Query("SELECT p FROM Patient p WHERE p.isAnonymized = false AND (p.anonymizedName IS NULL OR p.anonymizedName = '')")
    List<Patient> findPatientsWithMissingAnonymization();
    
    /**
     * Vérifie l'existence d'un patient par UUID sans exposer ses données
     */
    boolean existsByPatientUuid(String patientUuid);
    
    /**
     * Trouve les patients par proximité géographique (pour analyses anonymisées)
     */
    @Query("SELECT p FROM Patient p WHERE " +
           "6371 * acos(cos(radians(:latitude)) * cos(radians(p.latitude)) * " +
           "cos(radians(p.longitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(p.latitude))) <= :radiusKm")
    List<Patient> findPatientsWithinRadius(@Param("latitude") Double latitude, 
                                          @Param("longitude") Double longitude, 
                                          @Param("radiusKm") Double radiusKm);
}
