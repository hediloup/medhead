package com.medhead.poc.service;

import com.medhead.poc.event.BedReservedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Service pour publier des événements dans le système.
 * Utilise le pattern Observer de Spring pour la publication d'événements.
 */
@Service
public class EventPublisherService {
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    /**
     * Publie un événement de lit réservé
     */
    public void publishBedReservedEvent(BedReservedEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("L'événement ne peut pas être null");
        }
        
        try {
            // Validation de l'événement avant publication
            validateBedReservedEvent(event);
            
            // Publication de l'événement
            eventPublisher.publishEvent(event);
            
            System.out.println("Événement BED_RESERVED publié : " + event.getEventId());
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la publication de l'événement BED_RESERVED : " + e.getMessage());
            throw new RuntimeException("Impossible de publier l'événement", e);
        }
    }
    
    /**
     * Crée et publie un événement de lit réservé avec les paramètres fournis
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
     * Valide un événement avant publication
     */
    private void validateBedReservedEvent(BedReservedEvent event) {
        if (event.getPatientUuid() == null || event.getPatientUuid().trim().isEmpty()) {
            throw new IllegalArgumentException("L'UUID du patient est requis");
        }
        
        if (event.getAnonymizedPatientId() == null || event.getAnonymizedPatientId().trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID anonymisé du patient est requis");
        }
        
        if (event.getHospitalId() == null) {
            throw new IllegalArgumentException("L'ID de l'hôpital est requis");
        }
        
        if (event.getHospitalName() == null || event.getHospitalName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'hôpital est requis");
        }
        
        if (event.getRequiredSpecialty() == null || event.getRequiredSpecialty().trim().isEmpty()) {
            throw new IllegalArgumentException("La spécialité requise est requise");
        }
        
        if (event.getEventType() == null || !event.getEventType().equals("BED_RESERVED")) {
            throw new IllegalArgumentException("Le type d'événement doit être BED_RESERVED");
        }
    }
    
    /**
     * Publie un événement générique
     */
    public void publishEvent(Object event) {
        if (event == null) {
            throw new IllegalArgumentException("L'événement ne peut pas être null");
        }
        
        try {
            eventPublisher.publishEvent(event);
            System.out.println("Événement publié : " + event.getClass().getSimpleName());
        } catch (Exception e) {
            System.err.println("Erreur lors de la publication de l'événement : " + e.getMessage());
            throw new RuntimeException("Impossible de publier l'événement", e);
        }
    }
}
