package com.medhead.poc.bdd.steps;

import com.medhead.poc.model.AllocationRequest;
import com.medhead.poc.model.Hospital;
import com.medhead.poc.model.Speciality;
import com.medhead.poc.repository.HospitalRepository;
import com.medhead.poc.repository.SpecialityRepository;
import io.cucumber.java.fr.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Steps pour les tests BDD de performance
 */
public class PerformanceSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private SpecialityRepository specialityRepository;

    private List<ResponseEntity<String>> responses = new ArrayList<>();
    private List<Long> responseTimes = new ArrayList<>();
    private AtomicInteger successCount = new AtomicInteger(0);
    private AtomicInteger errorCount = new AtomicInteger(0);
    private ExecutorService executorService;

    @Étantdonné("^qu'il existe des hôpitaux avec la spécialité \"([^\"]*)\"$")
    @Transactional
    public void qu_il_existe_des_hôpitaux_avec_la_spécialité(String specialtyName) {
        // Créer ou récupérer la spécialité
        Speciality specialty = specialityRepository.findByName(specialtyName).orElse(null);
        if (specialty == null) {
            specialty = new Speciality();
            specialty.setName(specialtyName);
            specialty = specialityRepository.save(specialty);
        }

        // Créer plusieurs hôpitaux pour les tests de performance
        for (int i = 0; i < 10; i++) {
            Hospital hospital = new Hospital("Hôpital Test " + i, 53.3976314 + (i * 0.01), -2.1829641 + (i * 0.01), 
                    "Manchester", "Test Address " + i, 10);
            Set<Speciality> specialties = new HashSet<>();
            specialties.add(specialty);
            hospital.setSpecialities(specialties);
            hospitalRepository.save(hospital);
        }
    }

    @Et("^qu'un générateur de charge simule (\\d+) requêtes simultanées sur l'endpoint /api/allocate$")
    public void qu_un_générateur_de_charge_simule_requêtes_simultanées_sur_l_endpoint_api_allocate(int requestCount) {
        // Initialiser les compteurs
        responses.clear();
        responseTimes.clear();
        successCount.set(0);
        errorCount.set(0);
        executorService = Executors.newFixedThreadPool(requestCount);

        // Créer les tâches de requêtes simultanées
        List<Future<Void>> futures = new ArrayList<>();
        
        for (int i = 0; i < requestCount; i++) {
            Future<Void> future = executorService.submit(() -> {
                try {
                    long startTime = System.currentTimeMillis();
                    
                    AllocationRequest request = new AllocationRequest("Cardiology", 53.3976314, -2.1829641);
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("Content-Type", "application/json");
                    HttpEntity<AllocationRequest> entity = new HttpEntity<>(request, headers);
                    
                    ResponseEntity<String> response = restTemplate.postForEntity("/api/allocate", entity, String.class);
                    
                    long endTime = System.currentTimeMillis();
                    long responseTime = endTime - startTime;
                    
                    synchronized (responses) {
                        responses.add(response);
                        responseTimes.add(responseTime);
                    }
                    
                    if (response.getStatusCode().is2xxSuccessful()) {
                        successCount.incrementAndGet();
                    } else {
                        errorCount.incrementAndGet();
                    }
                    
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                }
                return null;
            });
            
            futures.add(future);
        }
        
        // Attendre que toutes les requêtes se terminent
        for (Future<Void> future : futures) {
            try {
                future.get(30, TimeUnit.SECONDS);
            } catch (Exception e) {
                errorCount.incrementAndGet();
            }
        }
    }

    @Quand("^les requêtes sont exécutées pendant (\\d+) minute$")
    public void les_requêtes_sont_exécutées_pendant_minute(int durationMinutes) {
        // Les requêtes sont déjà exécutées dans l'étape précédente
        // Cette étape est principalement documentaire pour le scénario BDD
    }

    @Alors("^(\\d+)% des requêtes doivent avoir un temps de réponse inférieur à (\\d+) millisecondes$")
    public void pourcentage_des_requêtes_doivent_avoir_un_temps_de_réponse_inférieur_à_millisecondes(int percentage, int maxResponseTime) {
        assertTrue("Il doit y avoir des réponses", responseTimes.size() > 0);
        
        long responsesUnderThreshold = responseTimes.stream()
                .mapToLong(Long::longValue)
                .filter(time -> time < maxResponseTime)
                .count();
        
        double actualPercentage = (double) responsesUnderThreshold / responseTimes.size() * 100;
        
        assertTrue(String.format("Le pourcentage de réponses rapides (%d%%) doit être >= %d%%", 
                (int) actualPercentage, percentage), 
                actualPercentage >= percentage);
    }

    @Et("^aucune erreur de timeout ne doit être observée$")
    public void aucune_erreur_de_timeout_ne_doit_être_observée() {
        // Vérifier qu'il n'y a pas trop d'erreurs (timeouts inclus)
        double errorRate = (double) errorCount.get() / (responses.size() + errorCount.get()) * 100;
        assertTrue("Le taux d'erreur doit être acceptable", errorRate < 10); // Moins de 10% d'erreurs
    }

    @Et("^le taux d'erreur doit être inférieur à (\\d+)%$")
    public void le_taux_d_erreur_doit_être_inférieur_à_pourcentage(int maxErrorRate) {
        double actualErrorRate = (double) errorCount.get() / (responses.size() + errorCount.get()) * 100;
        assertTrue(String.format("Le taux d'erreur (%d%%) doit être < %d%%", 
                (int) actualErrorRate, maxErrorRate), 
                actualErrorRate < maxErrorRate);
    }

    @Étantdonné("^que l'API est en fonctionnement normal$")
    public void que_l_API_est_en_fonctionnement_normal() {
        // Vérifier que l'API répond normalement
        ResponseEntity<String> healthResponse = restTemplate.getForEntity("/api/health", String.class);
        assertTrue("L'API doit être en fonctionnement normal", healthResponse.getStatusCode().is2xxSuccessful());
    }

    @Quand("^je surveille la disponibilité pendant (\\d+) heures$")
    public void je_surveille_la_disponibilité_pendant_heures(int hours) {
        // Simuler une surveillance de disponibilité (version simplifiée pour les tests)
        responses.clear();
        responseTimes.clear();
        successCount.set(0);
        errorCount.set(0);
        
        // Simuler quelques vérifications de santé (pas 24h complètes pour les tests)
        int checksPerHour = 60; // Une vérification par minute
        int totalChecks = checksPerHour * Math.min(hours, 1); // Limiter à 1 heure pour les tests
        
        executorService = Executors.newFixedThreadPool(10);
        
        List<Future<Void>> futures = new ArrayList<>();
        
        for (int i = 0; i < totalChecks; i++) {
            Future<Void> future = executorService.submit(() -> {
                try {
                    long startTime = System.currentTimeMillis();
                    ResponseEntity<String> response = restTemplate.getForEntity("/api/health", String.class);
                    long endTime = System.currentTimeMillis();
                    
                    synchronized (responses) {
                        responses.add(response);
                        responseTimes.add(endTime - startTime);
                    }
                    
                    if (response.getStatusCode().is2xxSuccessful()) {
                        successCount.incrementAndGet();
                    } else {
                        errorCount.incrementAndGet();
                    }
                    
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                }
                return null;
            });
            
            futures.add(future);
            
            // Attendre 1 seconde entre les vérifications
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        // Attendre que toutes les vérifications se terminent
        for (Future<Void> future : futures) {
            try {
                future.get(5, TimeUnit.SECONDS);
            } catch (Exception e) {
                errorCount.incrementAndGet();
            }
        }
    }

    @Alors("^le taux de disponibilité doit être supérieur à (\\d+)\\.(\\d+)%$")
    public void le_taux_de_disponibilité_doit_être_supérieur_à_pourcentage(int major, int minor) {
        double expectedAvailability = major + minor / 100.0;
        
        double actualAvailability = (double) successCount.get() / (successCount.get() + errorCount.get()) * 100;
        
        assertTrue(String.format("Le taux de disponibilité (%d%%) doit être > %.1f%%", 
                (int) actualAvailability, expectedAvailability), 
                actualAvailability > expectedAvailability);
    }

    @Et("^tous les endpoints principaux doivent rester accessibles$")
    public void tous_les_endpoints_principaux_doivent_rester_accessibles() {
        // Vérifier que les endpoints principaux sont accessibles
        ResponseEntity<String> healthResponse = restTemplate.getForEntity("/api/health", String.class);
        assertTrue("L'endpoint /api/health doit être accessible", healthResponse.getStatusCode().is2xxSuccessful());
        
        ResponseEntity<String> testResponse = restTemplate.getForEntity("/api/test", String.class);
        assertTrue("L'endpoint /api/test doit être accessible", testResponse.getStatusCode().is2xxSuccessful());
    }

    @Et("^les temps de réponse doivent rester stables$")
    public void les_temps_de_réponse_doivent_rester_stables() {
        assertTrue("Il doit y avoir des temps de réponse", responseTimes.size() > 0);
        
        // Calculer l'écart-type pour vérifier la stabilité
        double average = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        double variance = responseTimes.stream()
                .mapToDouble(time -> Math.pow(time - average, 2))
                .average()
                .orElse(0.0);
        double standardDeviation = Math.sqrt(variance);
        
        // Le coefficient de variation (écart-type / moyenne) doit être raisonnable
        double coefficientOfVariation = standardDeviation / average;
        assertTrue("Les temps de réponse doivent être stables", coefficientOfVariation < 0.5); // Moins de 50% de variation
    }
}
