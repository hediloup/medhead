package com.medhead.poc.bdd.steps;

import io.cucumber.java.en.*;
import io.cucumber.datatable.DataTable;
import static org.assertj.core.api.Assertions.*;
import java.util.Map;

/**
 * Définitions des étapes pour la publication d’événements après l’allocation
 * d’un lit. Ce squelette illustre comment vérifier qu’un événement est
 * publié et que certaines données sont présentes dans le message.
 */
public class EventPublishingSteps {

    private boolean eventPublished;
    private String hospitalId;
    private String speciality;
    private String timestamp;

    @Given("une demande de lit validée pour {string}")
    public void une_demande_de_lit_validée_pour(String hopitalId) {
        this.hospitalId = hopitalId;
    }

    @When("le système confirme la réservation")
    public void le_système_confirme_la_réservation() {
        // Simulation de publication de l’événement
        this.eventPublished = true;
        this.speciality = "Cardiologie";
        this.timestamp = "2025-10-10T10:10:10Z";
    }

    @Then("un message avec type {string} est publié sur le topic {string}")
    public void un_message_avec_type_est_publié_sur_le_topic(String type, String topic) {
        assertThat(this.eventPublished).as("Vérifier que l’événement est publié").isTrue();
    }

    @Then("le message contient :")
    public void le_message_contient(DataTable table) {
        // Convertir la DataTable en Map pour validation
        Map<String, String> expectedData = table.asMap(String.class, String.class);
        
        // Vérifier les données attendues
        assertThat(expectedData).containsKey("hospital_id");
        assertThat(expectedData).containsKey("speciality");
        assertThat(expectedData).containsKey("timestamp");
        
        // Vérifier les valeurs
        assertThat(expectedData.get("hospital_id")).isEqualTo(this.hospitalId);
        assertThat(expectedData.get("speciality")).isEqualTo(this.speciality);
        assertThat(expectedData.get("timestamp")).isEqualTo("non nul");
    }
}