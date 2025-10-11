package com.medhead.poc.controller;

import com.medhead.poc.model.AllocationRequest;
import com.medhead.poc.model.AllocationResponse;
import com.medhead.poc.service.AllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST pour l'API d'allocation de lits d'hôpital.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Pour permettre les appels depuis le frontend
public class AllocationController {
    
    @Autowired
    private AllocationService allocationService;
    
    /**
     * Endpoint pour obtenir une recommandation d'hôpital.
     * 
     * @param request La demande d'allocation contenant spécialité et géolocalisation
     * @return La réponse avec l'hôpital recommandé
     */
    @PostMapping("/allocate")
    public ResponseEntity<AllocationResponse> allocateHospital(@RequestBody AllocationRequest request) {
        try {
            AllocationResponse response = allocationService.findBestHospital(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Erreur de validation des paramètres
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            // Aucun hôpital trouvé
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            // Erreur serveur
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Endpoint GET pour tester l'API avec des paramètres en query string.
     * Utile pour les tests et la démonstration.
     * 
     * @param specialty La spécialité médicale
     * @param latitude Latitude du patient
     * @param longitude Longitude du patient
     * @return La réponse avec l'hôpital recommandé
     */
    @GetMapping("/allocate")
    public ResponseEntity<AllocationResponse> allocateHospitalGet(
            @RequestParam String specialty,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        
        AllocationRequest request = new AllocationRequest(specialty, latitude, longitude);
        return allocateHospital(request);
    }
    
    /**
     * Endpoint de santé pour vérifier que l'API est opérationnelle.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("API d'allocation opérationnelle");
    }
}
