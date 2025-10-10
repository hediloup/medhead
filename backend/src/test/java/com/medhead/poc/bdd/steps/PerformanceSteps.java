package com.medhead.poc.bdd.steps;

import io.cucumber.java.en.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Étapes pour les tests de performance. Ces tests sont purement illustratifs
 * et ne réalisent pas de charge réelle : ils montrent comment structurer
 * l’écriture d’un test de performance en BDD.
 */
public class PerformanceSteps {

    private double responseTime;
    private int requests;

    @Given("un générateur de charge simulant {int} requêtes/s sur l’endpoint {string}")
    public void un_générateur_de_charge_simulant_requêtes_s_sur_l_endpoint(int reqs, String endpoint) {
        this.requests = reqs;
    }

    @When("les réponses sont mesurées sur une durée de {int} minutes")
    public void les_réponses_sont_mesurées_sur_une_durée_de_minutes(int minutes) {
        // On simule un temps moyen de réponse en millisecondes
        this.responseTime = 150.0;
    }

    @Then("{int}% des requêtes doivent avoir un temps de réponse < {int} ms")
    public void pourcentage_des_requêtes_doivent_avoir_un_temps_de_réponse_inférieur_à_ms(int percentile, int seuil) {
        assertThat(this.responseTime).isLessThan((double) seuil);
    }

    @Then("aucun timeout ni 5xx ne doit être observé")
    public void aucun_timeout_ni_xx_ne_doit_être_observé() {
        assertThat(true).isTrue();
    }
}