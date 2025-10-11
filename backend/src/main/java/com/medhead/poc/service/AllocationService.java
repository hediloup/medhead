package com.medhead.poc.service;

import com.medhead.poc.model.AllocationRequest;
import com.medhead.poc.model.AllocationResponse;
import com.medhead.poc.model.Hospital;
import com.medhead.poc.model.Patient;
import com.medhead.poc.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service principal pour la logique d'allocation de lits d'hôpital.
 */
@Service
@Transactional
public class AllocationService {
    
    @Autowired
    private HospitalRepository hospitalRepository;
    
    @Autowired
    private DistanceCalculationService distanceService;
    
    @Autowired
    private PatientAnonymizationService patientAnonymizationService;
    
    @Autowired
    private EventPublisherService eventPublisherService;
    
    /**
     * Trouve l'hôpital le plus approprié pour une demande d'allocation et enregistre le patient.
     * 
     * @param request La demande d'allocation
     * @return La réponse avec l'hôpital recommandé
     * @throws RuntimeException si aucun hôpital approprié n'est trouvé
     */
    public AllocationResponse findBestHospital(AllocationRequest request) {
        // Validation des paramètres
        if (request.getSpecialty() == null || request.getSpecialty().trim().isEmpty()) {
            throw new IllegalArgumentException("La spécialité est obligatoire");
        }
        
        if (request.getLatitude() == null || request.getLongitude() == null) {
            throw new IllegalArgumentException("La géolocalisation est obligatoire");
        }
        
        // Recherche des hôpitaux avec la spécialité demandée et des lits disponibles
        List<Hospital> eligibleHospitals = hospitalRepository
            .findBySpecialtyAndAvailableBeds(request.getSpecialty());
        
        if (eligibleHospitals.isEmpty()) {
            throw new RuntimeException("Aucun hôpital disponible avec la spécialité '" + 
                                    request.getSpecialty() + "'");
        }
        
        // Calcul des distances et tri par distance
        List<HospitalWithDistance> hospitalsWithDistance = eligibleHospitals.stream()
            .map(hospital -> {
                double distance = distanceService.calculateDistanceToHospital(
                    request.getLatitude(), 
                    request.getLongitude(), 
                    hospital
                );
                return new HospitalWithDistance(hospital, distance);
            })
            .sorted(Comparator.comparingDouble(HospitalWithDistance::getDistance))
            .collect(Collectors.toList());
        
        // Sélection du meilleur hôpital (le plus proche)
        HospitalWithDistance bestHospital = hospitalsWithDistance.get(0);
        Hospital selectedHospital = bestHospital.getHospital();
        double distance = bestHospital.getDistance();
        
        // Calcul du temps de trajet estimé
        int estimatedTime = distanceService.estimateTravelTime(distance);
        
        // Création du patient anonymisé
        Patient patient = patientAnonymizationService.createAnonymizedPatient(
            request.getSpecialty(),
            request.getLatitude(),
            request.getLongitude(),
            "MEDIUM" // Niveau de gravité par défaut
        );
        
        // Association du patient à l'hôpital
        patient.setAllocatedHospital(selectedHospital);
        patientAnonymizationService.anonymizePatient(patient);
        
        // Calcul des lits disponibles après réservation
        int availableBedsAfter = selectedHospital.getAvailableBeds() - 1;
        
        // Création de la réponse
        AllocationResponse response = new AllocationResponse(
            selectedHospital.getName(),
            selectedHospital.getId(),
            Math.round(distance * 100.0) / 100.0, // Arrondi à 2 décimales
            request.getSpecialty(),
            availableBedsAfter,
            estimatedTime
        );
        
        // Publication de l'événement BED_RESERVED
        eventPublisherService.publishBedReservedEvent(
            patient.getPatientUuid(),
            patient.getAnonymizedName(),
            request.getSpecialty(),
            patient.getSeverityLevel(),
            patient.getAgeGroup(),
            selectedHospital.getId(),
            selectedHospital.getName(),
            selectedHospital.getCity(),
            Math.round(distance * 100.0) / 100.0,
            availableBedsAfter,
            estimatedTime
        );
        
        return response;
    }
    
    /**
     * Classe interne pour stocker un hôpital avec sa distance.
     */
    private static class HospitalWithDistance {
        private final Hospital hospital;
        private final double distance;
        
        public HospitalWithDistance(Hospital hospital, double distance) {
            this.hospital = hospital;
            this.distance = distance;
        }
        
        public Hospital getHospital() {
            return hospital;
        }
        
        public double getDistance() {
            return distance;
        }
    }
}
