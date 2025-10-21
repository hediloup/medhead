package com.medhead.poc.listener;

import com.medhead.poc.event.BedReservedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Listener for bed reserved events.
 * Processes BED_RESERVED events published in the system.
 */
@Component
public class BedReservedEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(BedReservedEventListener.class);
    
    /**
     * Listens and processes BED_RESERVED events
     */
    @EventListener
    public void handleBedReservedEvent(BedReservedEvent event) {
        try {
            logger.info("=== BED_RESERVED EVENT RECEIVED ===");
            logger.info("Event ID: {}", event.getEventId());
            logger.info("Timestamp: {}", event.getTimestamp());
            logger.info("Anonymized Patient: {}", event.getAnonymizedPatientId());
            logger.info("Specialty: {}", event.getRequiredSpecialty());
            logger.info("Severity Level: {}", event.getSeverityLevel());
            logger.info("Age Group: {}", event.getAgeGroup());
            logger.info("Hospital: {} ({})", event.getHospitalName(), event.getHospitalCity());
            logger.info("Distance: {} km", event.getDistanceKm());
            logger.info("Available beds after: {}", event.getAvailableBedsAfter());
            logger.info("Estimated time: {} minutes", event.getEstimatedTimeMinutes());
            logger.info("Status: {}", event.getAllocationStatus());
            logger.info("=====================================");
            
            // Here you can add other processing:
            // - Sending notifications
            // - Updating dashboards
            // - Integration with other systems
            // - Logging to audit database
            // - Sending emails/SMS to medical teams
            
            processBedReservation(event);
            
        } catch (Exception e) {
            logger.error("Error processing BED_RESERVED event: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Processes bed reservation
     */
    private void processBedReservation(BedReservedEvent event) {
        // Examples of possible processing:
        
        // 1. Notification to medical teams
        notifyMedicalTeams(event);
        
        // 2. Statistics update
        updateStatistics(event);
        
        // 3. Audit trail
        logAuditTrail(event);
        
        // 4. Integration with external systems
        integrateWithExternalSystems(event);
    }
    
    /**
     * Notifies medical teams
     */
    private void notifyMedicalTeams(BedReservedEvent event) {
        // Notification simulation
        logger.info("📧 Notification sent to teams at {}", event.getHospitalName());
        logger.info("   Patient: {}", event.getAnonymizedPatientId());
        logger.info("   Specialty: {}", event.getRequiredSpecialty());
        logger.info("   Level: {}", event.getSeverityLevel());
        logger.info("   Estimated arrival: {} minutes", event.getEstimatedTimeMinutes());
    }
    
    /**
     * Updates statistics
     */
    private void updateStatistics(BedReservedEvent event) {
        // Statistics update simulation
        logger.info("📊 Statistics updated:");
        logger.info("   - {} allocation to {}", event.getRequiredSpecialty(), event.getHospitalName());
        logger.info("   - Response time: {} minutes", event.getEstimatedTimeMinutes());
        logger.info("   - Average distance for {}: {} km", event.getRequiredSpecialty(), event.getDistanceKm());
    }
    
    /**
     * Logs to audit trail
     */
    private void logAuditTrail(BedReservedEvent event) {
        // Audit trail simulation
        logger.info("🔍 Audit Trail - Bed reservation:");
        logger.info("   Patient UUID: {}", event.getPatientUuid());
        logger.info("   Hospital: {} - {}", event.getHospitalId(), event.getHospitalName());
        logger.info("   Timestamp: {}", event.getTimestamp());
        logger.info("   Sensitive data: ANONYMIZED ✅");
    }
    
    /**
     * Integrates with external systems
     */
    private void integrateWithExternalSystems(BedReservedEvent event) {
        // External systems integration simulation
        logger.info("🔗 External systems integration:");
        logger.info("   - Hospital management system: Reservation confirmed");
        logger.info("   - Transport system: Ambulance notified");
        logger.info("   - Real-time dashboard: Update completed");
        logger.info("   - Billing system: File preparation");
    }
}
