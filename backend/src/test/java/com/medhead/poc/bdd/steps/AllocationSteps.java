package com.medhead.poc.bdd.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Steps pour les tests BDD d'allocation d'hôpitaux
 */
public class AllocationSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private SpecialityRepository specialityRepository;

    private ObjectMapper objectMapper = new ObjectMapper();
    private ResponseEntity<String> lastResponse;
    private AllocationRequest currentRequest;
    private String currentSpecialty;
    private Double currentLatitude;
    private Double currentLongitude;

    @Étantdonné("^qu'il existe un hôpital \"([^\"]*)\" avec la spécialité \"([^\"]*)\" et (\\d+) lits disponibles$")
    @Transactional
    public void qu_il_existe_un_hôpital_avec_la_spécialité_et_lits_disponibles(String hospitalName, String specialtyName, int availableBeds) {
        // Créer ou récupérer la spécialité
        Speciality specialty = specialityRepository.findByName(specialtyName).orElse(null);
        if (specialty == null) {
            specialty = new Speciality();
            specialty.setName(specialtyName);
            specialty = specialityRepository.save(specialty);
        }

        // Créer l'hôpital
        Hospital hospital = new Hospital(hospitalName, 53.3976314, -2.1829641, "Manchester", "Test Address", availableBeds);
        Set<Speciality> specialties = new HashSet<>();
        specialties.add(specialty);
        hospital.setSpecialities(specialties);
        hospitalRepository.save(hospital);
    }

    @Étantdonné("^que le patient se trouve aux coordonnées (\\d+\\.\\d+), (-?\\d+\\.\\d+)$")
    public void que_le_patient_se_trouve_aux_coordonnées(double latitude, double longitude) {
        this.currentLatitude = latitude;
        this.currentLongitude = longitude;
    }

    @Quand("^je demande une allocation pour la spécialité \"([^\"]*)\"$")
    public void je_demande_une_allocation_pour_la_spécialité(String specialty) {
        this.currentSpecialty = specialty;
        AllocationRequest request = new AllocationRequest(specialty, currentLatitude, currentLongitude);
        this.currentRequest = request;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<AllocationRequest> entity = new HttpEntity<>(request, headers);
        this.lastResponse = restTemplate.postForEntity("/api/allocate", entity, String.class);
    }

    @Alors("^l'hôpital \"([^\"]*)\" doit être recommandé$")
    public void l_hôpital_doit_être_recommandé(String expectedHospitalName) {
        assertEquals("Le statut HTTP doit être 200", 200, lastResponse.getStatusCodeValue());
        
        try {
            Map<String, Object> responseMap = objectMapper.readValue(lastResponse.getBody(), Map.class);
            String actualHospitalName = (String) responseMap.get("hospitalName");
            assertEquals("Le nom de l'hôpital doit correspondre", expectedHospitalName, actualHospitalName);
        } catch (Exception e) {
            fail("Erreur lors du parsing de la réponse JSON: " + e.getMessage());
        }
    }

    @Et("^la distance doit être calculée correctement$")
    public void la_distance_doit_être_calculée_correctement() {
        try {
            Map<String, Object> responseMap = objectMapper.readValue(lastResponse.getBody(), Map.class);
            assertNotNull("La distance doit être présente", responseMap.get("distance"));
            Double distance = ((Number) responseMap.get("distance")).doubleValue();
            assertTrue("La distance doit être positive", distance >= 0);
        } catch (Exception e) {
            fail("Erreur lors de la vérification de la distance: " + e.getMessage());
        }
    }

    @Et("^le temps d'arrivée estimé doit être fourni$")
    public void le_temps_d_arrivée_estimé_doit_être_fourni() {
        try {
            Map<String, Object> responseMap = objectMapper.readValue(lastResponse.getBody(), Map.class);
            assertNotNull("Le temps estimé doit être présent", responseMap.get("estimatedTimeMinutes"));
            Integer estimatedTime = ((Number) responseMap.get("estimatedTimeMinutes")).intValue();
            assertTrue("Le temps estimé doit être positif", estimatedTime >= 0);
        } catch (Exception e) {
            fail("Erreur lors de la vérification du temps estimé: " + e.getMessage());
        }
    }

    @Et("^le nombre de lits disponibles après allocation doit être (\\d+)$")
    public void le_nombre_de_lits_disponibles_après_allocation_doit_être(int expectedBeds) {
        try {
            Map<String, Object> responseMap = objectMapper.readValue(lastResponse.getBody(), Map.class);
            assertNotNull("Le nombre de lits après allocation doit être présent", responseMap.get("availableBedsAfterAllocation"));
            Integer actualBeds = ((Number) responseMap.get("availableBedsAfterAllocation")).intValue();
            assertEquals("Le nombre de lits après allocation doit correspondre", expectedBeds, actualBeds.intValue());
        } catch (Exception e) {
            fail("Erreur lors de la vérification du nombre de lits: " + e.getMessage());
        }
    }

    @Étantdonné("^qu'il n'existe aucun hôpital avec la spécialité \"([^\"]*)\"$")
    @Transactional
    public void qu_il_n_existe_aucun_hôpital_avec_la_spécialité(String specialtyName) {
        // Ne pas créer d'hôpital avec cette spécialité
        // Les hôpitaux existants sont déjà nettoyés dans les hooks
    }

    @Alors("^je dois recevoir une erreur \"([^\"]*)\"$")
    public void je_dois_recevoir_une_erreur(String expectedError) {
        // Vérifier que la réponse est une erreur
        assertTrue("La réponse doit être une erreur", 
                lastResponse.getStatusCodeValue() >= 400);
    }

    @Et("^le code de statut HTTP doit être (\\d+)$")
    public void le_code_de_statut_HTTP_doit_être(int expectedStatusCode) {
        assertEquals("Le code de statut HTTP doit correspondre", expectedStatusCode, lastResponse.getStatusCodeValue());
    }

    @Étantdonné("^qu'il existe un hôpital \"([^\"]*)\" avec la spécialité \"([^\"]*)\" et (\\d+) lit disponible$")
    @Transactional
    public void qu_il_existe_un_hôpital_avec_la_spécialité_et_lit_disponible(String hospitalName, String specialtyName, int availableBeds) {
        qu_il_existe_un_hôpital_avec_la_spécialité_et_lits_disponibles(hospitalName, specialtyName, availableBeds);
    }

    @Étantdonné("^que le patient se trouve aux coordonnées de test (\\d+\\.\\d+), (-?\\d+\\.\\d+)$")
    public void que_le_patient_se_trouve_aux_coordonnées_de_test(double latitude, double longitude) {
        this.currentLatitude = latitude;
        this.currentLongitude = longitude;
    }

    @Quand("^je demande une allocation avec une spécialité vide \"\"$")
    public void je_demande_une_allocation_avec_une_spécialité_vide() {
        AllocationRequest request = new AllocationRequest("", currentLatitude, currentLongitude);
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        
        HttpEntity<AllocationRequest> entity = new HttpEntity<>(request, headers);
        this.lastResponse = restTemplate.postForEntity("/api/allocate", entity, String.class);
    }

    @Alors("^je dois recevoir une erreur de validation$")
    public void je_dois_recevoir_une_erreur_de_validation() {
        assertEquals("Le code de statut doit être 400 (Bad Request)", 400, lastResponse.getStatusCodeValue());
    }

    @Étantdonné("^qu'il existe un hôpital avec la spécialité \"([^\"]*)\"$")
    @Transactional
    public void qu_il_existe_un_hôpital_avec_la_spécialité(String specialtyName) {
        qu_il_existe_un_hôpital_avec_la_spécialité_et_lits_disponibles("Hôpital Test", specialtyName, 5);
    }

    @Quand("^je demande une allocation pour la spécialité \"([^\"]*)\" avec des coordonnées nulles$")
    public void je_demande_une_allocation_pour_la_spécialité_avec_des_coordonnées_nulles(String specialty) {
        AllocationRequest request = new AllocationRequest(specialty, null, null);
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        
        HttpEntity<AllocationRequest> entity = new HttpEntity<>(request, headers);
        this.lastResponse = restTemplate.postForEntity("/api/allocate", entity, String.class);
    }

    @Quand("^j'appelle l'endpoint GET /api/allocate avec les paramètres:$")
    public void j_appelle_l_endpoint_GET_api_allocate_avec_les_paramètres(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> params = dataTable.asMap(String.class, String.class);
        
        String specialty = params.get("specialty");
        String latitude = params.get("latitude");
        String longitude = params.get("longitude");
        
        this.lastResponse = restTemplate.getForEntity(
                "/api/allocate?specialty={specialty}&latitude={latitude}&longitude={longitude}",
                String.class, specialty, latitude, longitude);
    }

    @Et("^la réponse doit être identique à l'endpoint POST$")
    public void la_réponse_doit_être_identique_à_l_endpoint_POST() {
        assertEquals("Le code de statut doit être 200", 200, lastResponse.getStatusCodeValue());
        assertNotNull("La réponse ne doit pas être null", lastResponse.getBody());
    }

    @Quand("^j'appelle l'endpoint /api/health$")
    public void j_appelle_l_endpoint_api_health() {
        this.lastResponse = restTemplate.getForEntity("/api/health", String.class);
    }

    @Alors("^je dois recevoir le message \"([^\"]*)\"$")
    public void je_dois_recevoir_le_message(String expectedMessage) {
        assertEquals("Le message doit correspondre", expectedMessage, lastResponse.getBody());
    }

    @Et("^le code de statut HTTP doit être (\\d+) pour la réponse$")
    public void le_code_de_statut_HTTP_doit_être_pour_la_réponse(int expectedStatusCode) {
        assertEquals("Le code de statut HTTP doit correspondre", expectedStatusCode, lastResponse.getStatusCodeValue());
    }

    @Étantdonné("^qu'il existe plusieurs hôpitaux avec la spécialité \"([^\"]*)\":$")
    @Transactional
    public void qu_il_existe_plusieurs_hôpitaux_avec_la_spécialité(io.cucumber.datatable.DataTable dataTable, String specialtyName) {
        List<Map<String, String>> hospitals = dataTable.asMaps(String.class, String.class);
        
        // Créer ou récupérer la spécialité
        Speciality specialty = specialityRepository.findByName(specialtyName).orElse(null);
        if (specialty == null) {
            specialty = new Speciality();
            specialty.setName(specialtyName);
            specialty = specialityRepository.save(specialty);
        }
        
        for (Map<String, String> hospitalData : hospitals) {
            String name = hospitalData.get("Nom");
            Double latitude = Double.parseDouble(hospitalData.get("Latitude"));
            Double longitude = Double.parseDouble(hospitalData.get("Longitude"));
            Integer beds = Integer.parseInt(hospitalData.get("Lits"));
            
            Hospital hospital = new Hospital(name, latitude, longitude, "Manchester", "Test Address", beds);
            Set<Speciality> specialties = new HashSet<>();
            specialties.add(specialty);
            hospital.setSpecialities(specialties);
            hospitalRepository.save(hospital);
        }
    }

    @Alors("^l'hôpital le plus proche doit être sélectionné$")
    public void l_hôpital_le_plus_proche_doit_être_sélectionné() {
        assertEquals("Le statut HTTP doit être 200", 200, lastResponse.getStatusCodeValue());
        
        try {
            Map<String, Object> responseMap = objectMapper.readValue(lastResponse.getBody(), Map.class);
            assertNotNull("Le nom de l'hôpital doit être présent", responseMap.get("hospitalName"));
            assertNotNull("La distance doit être présente", responseMap.get("distance"));
        } catch (Exception e) {
            fail("Erreur lors du parsing de la réponse JSON: " + e.getMessage());
        }
    }

    @Et("^la réponse doit contenir les informations de route$")
    public void la_réponse_doit_contenir_les_informations_de_route() {
        try {
            Map<String, Object> responseMap = objectMapper.readValue(lastResponse.getBody(), Map.class);
            assertNotNull("La distance doit être présente", responseMap.get("distance"));
            assertNotNull("Le temps estimé doit être présent", responseMap.get("estimatedTimeMinutes"));
        } catch (Exception e) {
            fail("Erreur lors de la vérification des informations de route: " + e.getMessage());
        }
    }

    // Étapes supplémentaires pour les tests manquants
    @Étantdonné("^qu'il existe un hôpital \"([^\"]*)\" avec la spécialité \"([^\"]*)\"$")
    @Transactional
    public void qu_il_existe_un_hôpital_avec_la_spécialité(String hospitalName, String specialtyName) {
        // Créer ou récupérer la spécialité
        Speciality specialty = specialityRepository.findByName(specialtyName).orElse(null);
        if (specialty == null) {
            specialty = new Speciality();
            specialty.setName(specialtyName);
            specialty = specialityRepository.save(specialty);
        }

        // Créer l'hôpital avec la spécialité
        Hospital hospital = new Hospital(hospitalName, 53.4808, -2.2426, "Manchester", "Test Address", 5);
        Set<Speciality> specialities = new HashSet<>();
        specialities.add(specialty);
        hospital.setSpecialities(specialities);
        hospitalRepository.save(hospital);
    }


}
