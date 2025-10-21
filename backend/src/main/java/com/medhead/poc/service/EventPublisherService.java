package com.medhead.poc.service;

import com.medhead.poc.event.BedReservedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Service for publishing events in the system.
 * Uses Spring's Observer pattern for event publishing.
 */
@Service
public class EventPublisherService {
    
    private static final Logger logger = LoggerFactory.getLogger(EventPublisherService.class);
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    /**
     * Publishes a bed reserved event
     */
    public void publishBedReservedEvent(BedReservedEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("L'événement ne peut pas être null");
        }
        
        try {
            // Validate event before publishing
            validateBedReservedEvent(event);
            
            // Publish the event
            eventPublisher.publishEvent(event);
            
            logger.info("BED_RESERVED event published: {}", event.getEventId());
            
        } catch (Exception e) {
            logger.error("Error publishing BED_RESERVED event: {}", e.getMessage(), e);
            throw new RuntimeException("Impossible de publier l'événement", e);
        }
    }
    
    /**
     * Creates and publishes a bed reserved event with the provided parameters
     */
    public void publishBedReservedEvent(String patientUuid,
                                      String anonymizedPatientId,
                                      String requiredSpecialty,
                                      String severityLevel,
                                      String ageGroup,
                                      Long hospitalId,
                                      String hospitalName,
                                      String hospitalCity,
                                      Double distanceKm,
                                      Integer availableBedsAfter,
                                      Integer estimatedTimeMinutes) {
        
        BedReservedEvent event = new BedReservedEvent(
            patientUuid,
            anonymizedPatientId,
            requiredSpecialty,
            severityLevel,
            ageGroup,
            hospitalId,
            hospitalName,
            hospitalCity,
            distanceKm,
            availableBedsAfter,
            estimatedTimeMinutes
        );
        
        publishBedReservedEvent(event);
    }
    
    /**
     * Validates an event before publishing
     */
    private void validateBedReservedEvent(BedReservedEvent event) {
        if (event.getPatientUuid() == null || event.getPatientUuid().trim().isEmpty()) {
            throw new IllegalArgumentException("Patient UUID is required");
        }
        
        if (event.getAnonymizedPatientId() == null || event.getAnonymizedPatientId().trim().isEmpty()) {
            throw new IllegalArgumentException("Anonymized patient ID is required");
        }
        
        if (event.getHospitalId() == null) {
            throw new IllegalArgumentException("Hospital ID is required");
        }
        
        if (event.getHospitalName() == null || event.getHospitalName().trim().isEmpty()) {
            throw new IllegalArgumentException("Hospital name is required");
        }
        
        if (event.getRequiredSpecialty() == null || event.getRequiredSpecialty().trim().isEmpty()) {
            throw new IllegalArgumentException("Required specialty is required");
        }
        
        if (event.getEventType() == null || !event.getEventType().equals("BED_RESERVED")) {
            throw new IllegalArgumentException("Event type must be BED_RESERVED");
        }
    }
    
    /**
     * Publishes a generic event
     */
    public void publishEvent(Object event) {
        if (event == null) {
            throw new IllegalArgumentException("L'événement ne peut pas être null");
        }
        
        try {
            eventPublisher.publishEvent(event);
            logger.info("Event published: {}", event.getClass().getSimpleName());
        } catch (Exception e) {
            logger.error("Error publishing event: {}", e.getMessage(), e);
            throw new RuntimeException("Impossible de publier l'événement", e);
        }
    }
}
