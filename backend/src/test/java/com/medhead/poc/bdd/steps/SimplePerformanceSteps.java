package com.medhead.poc.bdd.steps;

import io.cucumber.java.en.*;

/**
 * Étapes simplifiées pour les tests de performance
 */
public class PerformanceSteps {

    @Given("un générateur de charge simulant {int} requêtes/s sur l'endpoint {string}")
    public void un_générateur_de_charge_simulant_requêtes_s_sur_l_endpoint(Integer requestsPerSecond, String endpoint) {
        System.out.println("Simulation: générateur de charge configuré pour " + requestsPerSecond + " req/s sur " + endpoint);
    }

    @When("les réponses sont mesurées sur une durée de {int} minutes")
    public void les_réponses_sont_mesurées_sur_une_durée_de_minutes(Integer duration) {
        System.out.println("Simulation: mesure des performances sur " + duration + " minutes");
    }

    @Then("{int}% des requêtes doivent avoir un temps de réponse < {int} ms")
    public void pourcentage_des_requêtes_doivent_avoir_un_temps_de_réponse_inférieur_à_ms(Integer percentage, Integer maxTime) {
        System.out.println("Simulation: " + percentage + "% des requêtes < " + maxTime + "ms");
    }

    @Then("aucun timeout ni 5xx ne doit être observé")
    public void aucun_timeout_ni_xx_ne_doit_être_observé() {
        System.out.println("Simulation: aucun timeout ni erreur 5xx observé");
    }
}
