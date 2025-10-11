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
 * Contrôleur pour la gestion sécurisée des données patients.
 * Implémente les bonnes pratiques RGPD et de protection des données.
 */
@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
public class PatientController {
    
    @Autowired
    private PatientAnonymizationService patientAnonymizationService;
    
    /**
     * Obtient les statistiques anonymisées des patients
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
     * Anonymise tous les patients non anonymisés
     */
    @PostMapping("/anonymize-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> anonymizeAllPatients() {
        try {
            int count = patientAnonymizationService.anonymizeAllNonAnonymizedPatients();
            return ResponseEntity.ok("Anonymisation réussie : " + count + " patients traités");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur lors de l'anonymisation : " + e.getMessage());
        }
    }
    
    /**
     * Supprime les données expirées (conformité RGPD)
     */
    @DeleteMapping("/cleanup-expired")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> cleanupExpiredPatients() {
        try {
            int count = patientAnonymizationService.deleteExpiredPatients();
            return ResponseEntity.ok("Nettoyage réussi : " + count + " patients expirés supprimés");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur lors du nettoyage : " + e.getMessage());
        }
    }
    
    /**
     * Obtient les informations anonymisées d'un patient par son UUID
     */
    @GetMapping("/{patientUuid}")
    @PreAuthorize("hasRole('MEDICAL_STAFF')")
    public ResponseEntity<Patient> getPatientByUuid(@PathVariable String patientUuid) {
        try {
            // Note: Cette méthode devrait être implémentée dans le service
            // Pour l'instant, on retourne une erreur car le service ne l'implémente pas encore
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Vérifie si un patient existe par son UUID
     */
    @GetMapping("/exists/{patientUuid}")
    @PreAuthorize("hasRole('MEDICAL_STAFF')")
    public ResponseEntity<Boolean> patientExists(@PathVariable String patientUuid) {
        try {
            // Note: Cette méthode devrait être implémentée dans le service
            // Pour l'instant, on retourne false
            return ResponseEntity.ok(false);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Endpoint de santé pour le contrôleur patient
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Patient Controller opérationnel - Données protégées par RGPD");
    }
}
