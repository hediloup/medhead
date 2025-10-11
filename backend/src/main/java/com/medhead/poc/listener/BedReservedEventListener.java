package com.medhead.poc.listener;

import com.medhead.poc.event.BedReservedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Écouteur pour les événements de lit réservé.
 * Traite les événements BED_RESERVED publiés dans le système.
 */
@Component
public class BedReservedEventListener {
    
    /**
     * Écoute et traite les événements BED_RESERVED
     */
    @EventListener
    public void handleBedReservedEvent(BedReservedEvent event) {
        try {
            System.out.println("=== ÉVÉNEMENT BED_RESERVED REÇU ===");
            System.out.println("ID Événement: " + event.getEventId());
            System.out.println("Timestamp: " + event.getTimestamp());
            System.out.println("Patient anonymisé: " + event.getAnonymizedPatientId());
            System.out.println("Spécialité: " + event.getRequiredSpecialty());
            System.out.println("Niveau de gravité: " + event.getSeverityLevel());
            System.out.println("Groupe d'âge: " + event.getAgeGroup());
            System.out.println("Hôpital: " + event.getHospitalName() + " (" + event.getHospitalCity() + ")");
            System.out.println("Distance: " + event.getDistanceKm() + " km");
            System.out.println("Lits disponibles après: " + event.getAvailableBedsAfter());
            System.out.println("Temps estimé: " + event.getEstimatedTimeMinutes() + " minutes");
            System.out.println("Statut: " + event.getAllocationStatus());
            System.out.println("=====================================");
            
            // Ici vous pouvez ajouter d'autres traitements :
            // - Envoi de notifications
            // - Mise à jour de dashboards
            // - Intégration avec d'autres systèmes
            // - Logging dans une base de données d'audit
            // - Envoi d'emails/SMS aux équipes médicales
            
            processBedReservation(event);
            
        } catch (Exception e) {
            System.err.println("Erreur lors du traitement de l'événement BED_RESERVED : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Traite la réservation de lit
     */
    private void processBedReservation(BedReservedEvent event) {
        // Exemples de traitements possibles :
        
        // 1. Notification aux équipes médicales
        notifyMedicalTeams(event);
        
        // 2. Mise à jour des statistiques
        updateStatistics(event);
        
        // 3. Audit trail
        logAuditTrail(event);
        
        // 4. Intégration avec systèmes externes
        integrateWithExternalSystems(event);
    }
    
    /**
     * Notifie les équipes médicales
     */
    private void notifyMedicalTeams(BedReservedEvent event) {
        // Simulation d'une notification
        System.out.println("📧 Notification envoyée aux équipes de " + event.getHospitalName());
        System.out.println("   Patient: " + event.getAnonymizedPatientId());
        System.out.println("   Spécialité: " + event.getRequiredSpecialty());
        System.out.println("   Niveau: " + event.getSeverityLevel());
        System.out.println("   Arrivée estimée: " + event.getEstimatedTimeMinutes() + " minutes");
    }
    
    /**
     * Met à jour les statistiques
     */
    private void updateStatistics(BedReservedEvent event) {
        // Simulation de mise à jour de statistiques
        System.out.println("📊 Statistiques mises à jour :");
        System.out.println("   - Allocation " + event.getRequiredSpecialty() + " à " + event.getHospitalName());
        System.out.println("   - Temps de réponse: " + event.getEstimatedTimeMinutes() + " minutes");
        System.out.println("   - Distance moyenne pour " + event.getRequiredSpecialty() + ": " + event.getDistanceKm() + " km");
    }
    
    /**
     * Enregistre dans l'audit trail
     */
    private void logAuditTrail(BedReservedEvent event) {
        // Simulation d'un audit trail
        System.out.println("🔍 Audit Trail - Réservation de lit :");
        System.out.println("   UUID Patient: " + event.getPatientUuid());
        System.out.println("   Hôpital: " + event.getHospitalId() + " - " + event.getHospitalName());
        System.out.println("   Timestamp: " + event.getTimestamp());
        System.out.println("   Données sensibles: ANONYMISÉES ✅");
    }
    
    /**
     * Intègre avec des systèmes externes
     */
    private void integrateWithExternalSystems(BedReservedEvent event) {
        // Simulation d'intégration avec des systèmes externes
        System.out.println("🔗 Intégration systèmes externes :");
        System.out.println("   - Système de gestion hôpital: Réservation confirmée");
        System.out.println("   - Système de transport: Ambulance notifiée");
        System.out.println("   - Dashboard temps réel: Mise à jour effectuée");
        System.out.println("   - Système de facturation: Préparation dossier");
    }
}
