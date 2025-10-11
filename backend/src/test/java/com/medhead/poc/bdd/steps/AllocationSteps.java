package com.medhead.poc.bdd.steps;

import io.cucumber.java.en.*;
import static org.assertj.core.api.Assertions.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Définitions des étapes pour l'allocation de lits.  
 * Ces étapes appellent l'API REST réelle pour obtenir une recommandation d'hôpital.
 */
public class AllocationSteps {

    private String speciality;
    private String geo;
    private int status;
    private String response;
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    public AllocationSteps() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Given("un patient nécessitant des soins en {string}")
    public void un_patient_nécessitant_des_soins_en(String spec) {
        this.speciality = spec;
    }

    @Given("la localisation du patient est {string}")
    public void la_localisation_du_patient_est(String geo) {
        this.geo = geo;
    }

    @When("l'API d'allocation est appelée avec ces paramètres")
    public void l_api_d_allocation_est_appelée_avec_ces_paramètres() {
        try {
            // Parse les coordonnées géographiques
            String[] coordinates = this.geo.split(",");
            double latitude = Double.parseDouble(coordinates[0].trim());
            double longitude = Double.parseDouble(coordinates[1].trim());
            
            // Appel de l'API REST réelle
            String url = String.format("http://localhost:8080/api/allocate?specialty=%s&latitude=%f&longitude=%f", 
                                     this.speciality, latitude, longitude);
            
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
            this.status = responseEntity.getStatusCode().value();
            
            // Parse la réponse JSON pour extraire le nom de l'hôpital
            if (responseEntity.getBody() != null) {
                JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
                this.response = jsonNode.get("hospital_name").asText();
            }
        } catch (Exception e) {
            this.status = 500;
            this.response = "Erreur lors de l'appel API: " + e.getMessage();
        }
    }

    @Then("le code HTTP doit être {int}")
    public void le_code_http_doit_être(Integer expectedStatus) {
        assertThat(this.status).as("vérification du code HTTP").isEqualTo(expectedStatus);
    }

    @Then("la réponse doit contenir {string}")
    public void la_réponse_doit_contenir(String expected) {
        assertThat(this.response).as("vérification du corps de réponse").contains(expected);
    }
}