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
 * Service pour l'anonymisation et la protection des données patients conformément au RGPD.
 */
@Service
@Transactional
public class PatientAnonymizationService {
    
    @Autowired
    private PatientRepository patientRepository;
    
    /**
     * Anonymise un patient en remplaçant les données sensibles par des identifiants anonymes
     */
    public Patient anonymizePatient(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Le patient ne peut pas être null");
        }
        
        // Générer un nom anonymisé si pas déjà fait
        if (patient.getAnonymizedName() == null || patient.getAnonymizedName().isEmpty()) {
            patient.generateAnonymizedName();
        }
        
        // Marquer comme anonymisé
        patient.setIsAnonymized(true);
        
        // Nettoyer les données sensibles supplémentaires si nécessaire
        // (le modèle Patient ne stocke déjà que des données anonymisées)
        
        return patientRepository.save(patient);
    }
    
    /**
     * Crée un patient anonymisé directement (recommandé pour l'API)
     */
    public Patient createAnonymizedPatient(String requiredSpecialty, 
                                         Double latitude, 
                                         Double longitude,
                                         String severityLevel) {
        
        Patient patient = new Patient(requiredSpecialty, latitude, longitude);
        
        // Générer immédiatement un nom anonymisé
        patient.generateAnonymizedName();
        
        // Définir le niveau de gravité
        if (severityLevel != null) {
            patient.setSeverityLevel(severityLevel);
        } else {
            patient.setSeverityLevel("MEDIUM"); // Valeur par défaut
        }
        
        // Marquer comme anonymisé dès la création
        patient.setIsAnonymized(true);
        
        return patientRepository.save(patient);
    }
    
    /**
     * Anonymise tous les patients non anonymisés
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
     * Supprime définitivement les données expirées (conformité RGPD)
     */
    public int deleteExpiredPatients() {
        LocalDateTime currentDate = LocalDateTime.now();
        return patientRepository.deleteExpiredPatients(currentDate);
    }
    
    /**
     * Trouve et anonymise les patients avec des données sensibles manquantes
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
     * Tâche planifiée : Nettoyage automatique des données expirées (exécutée quotidiennement à 2h du matin)
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduledDataCleanup() {
        try {
            int deletedCount = deleteExpiredPatients();
            System.out.println("Nettoyage automatique : " + deletedCount + " patients expirés supprimés");
            
            int anonymizedCount = anonymizePatientsWithMissingData();
            System.out.println("Anonymisation automatique : " + anonymizedCount + " patients anonymisés");
            
        } catch (Exception e) {
            System.err.println("Erreur lors du nettoyage automatique des données : " + e.getMessage());
        }
    }
    
    /**
     * Génère un identifiant anonyme unique pour un patient
     */
    public String generateAnonymousId() {
        return "PATIENT_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * Vérifie si un patient peut être consulté (pas expiré et anonymisé)
     */
    public boolean canAccessPatient(Patient patient) {
        if (patient == null) {
            return false;
        }
        
        // Vérifier si les données sont expirées
        if (patient.isDataExpired()) {
            return false;
        }
        
        // Vérifier si le patient est anonymisé
        if (!patient.getIsAnonymized()) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Obtient les statistiques anonymisées sur les patients
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
     * Classe pour les statistiques anonymisées
     */
    public static class PatientStatistics {
        private List<Object[]> specialtyCounts;
        private List<Object[]> ageGroupCounts;
        private LocalDateTime generatedAt;
        
        // Getters et Setters
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
