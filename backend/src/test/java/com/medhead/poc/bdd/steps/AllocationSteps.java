package com.medhead.poc.bdd.steps;

import io.cucumber.java.en.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Définitions des étapes pour l’allocation de lits.  
 * Ces étapes illustrent comment consommer une API pour obtenir un lit disponible
 * selon une spécialité et une géolocalisation. Dans ce squelette, l’appel
 * n’est pas effectué pour de vrai ; il est simulé afin d’illustrer le flux.
 */
public class AllocationSteps {

    private String speciality;
    private String geo;
    private int status;
    private String response;

    @Given("un patient nécessitant des soins en {string}")
    public void un_patient_nécessitant_des_soins_en(String spec) {
        this.speciality = spec;
    }

    @Given("la localisation du patient est {string}")
    public void la_localisation_du_patient_est(String geo) {
        this.geo = geo;
    }

    @When("l’API d’allocation est appelée avec ces paramètres")
    public void l_api_d_allocation_est_appelée_avec_ces_paramètres() {
        // Dans un test réel, un client HTTP (RestTemplate, WebClient…) appellerait
        // l’endpoint REST en utilisant les variables speciality et geo.  
        // Ici, on simule simplement la réponse.
        this.status = 200;
        this.response = "Hôpital Fred Brooks";
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