package com.medhead.poc.listener;

import com.medhead.poc.event.BedReservedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Listener for bed reserved events.
 * Processes BED_RESERVED events published in the system.
 */
@Component
public class BedReservedEventListener {
    
    /**
     * Listens and processes BED_RESERVED events
     */
    @EventListener
    public void handleBedReservedEvent(BedReservedEvent event) {
        try {
            System.out.println("=== BED_RESERVED EVENT RECEIVED ===");
            System.out.println("Event ID: " + event.getEventId());
            System.out.println("Timestamp: " + event.getTimestamp());
            System.out.println("Anonymized Patient: " + event.getAnonymizedPatientId());
            System.out.println("Specialty: " + event.getRequiredSpecialty());
            System.out.println("Severity Level: " + event.getSeverityLevel());
            System.out.println("Age Group: " + event.getAgeGroup());
            System.out.println("Hospital: " + event.getHospitalName() + " (" + event.getHospitalCity() + ")");
            System.out.println("Distance: " + event.getDistanceKm() + " km");
            System.out.println("Available beds after: " + event.getAvailableBedsAfter());
            System.out.println("Estimated time: " + event.getEstimatedTimeMinutes() + " minutes");
            System.out.println("Status: " + event.getAllocationStatus());
            System.out.println("=====================================");
            
            // Here you can add other processing:
            // - Sending notifications
            // - Updating dashboards
            // - Integration with other systems
            // - Logging to audit database
            // - Sending emails/SMS to medical teams
            
            processBedReservation(event);
            
        } catch (Exception e) {
            System.err.println("Error processing BED_RESERVED event: " + e.getMessage());
            e.printStackTrace();
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
        System.out.println("📧 Notification sent to teams at " + event.getHospitalName());
        System.out.println("   Patient: " + event.getAnonymizedPatientId());
        System.out.println("   Specialty: " + event.getRequiredSpecialty());
        System.out.println("   Level: " + event.getSeverityLevel());
        System.out.println("   Estimated arrival: " + event.getEstimatedTimeMinutes() + " minutes");
    }
    
    /**
     * Updates statistics
     */
    private void updateStatistics(BedReservedEvent event) {
        // Statistics update simulation
        System.out.println("📊 Statistics updated:");
        System.out.println("   - " + event.getRequiredSpecialty() + " allocation to " + event.getHospitalName());
        System.out.println("   - Response time: " + event.getEstimatedTimeMinutes() + " minutes");
        System.out.println("   - Average distance for " + event.getRequiredSpecialty() + ": " + event.getDistanceKm() + " km");
    }
    
    /**
     * Logs to audit trail
     */
    private void logAuditTrail(BedReservedEvent event) {
        // Audit trail simulation
        System.out.println("🔍 Audit Trail - Bed reservation:");
        System.out.println("   Patient UUID: " + event.getPatientUuid());
        System.out.println("   Hospital: " + event.getHospitalId() + " - " + event.getHospitalName());
        System.out.println("   Timestamp: " + event.getTimestamp());
        System.out.println("   Sensitive data: ANONYMIZED ✅");
    }
    
    /**
     * Integrates with external systems
     */
    private void integrateWithExternalSystems(BedReservedEvent event) {
        // External systems integration simulation
        System.out.println("🔗 External systems integration:");
        System.out.println("   - Hospital management system: Reservation confirmed");
        System.out.println("   - Transport system: Ambulance notified");
        System.out.println("   - Real-time dashboard: Update completed");
        System.out.println("   - Billing system: File preparation");
    }
}
