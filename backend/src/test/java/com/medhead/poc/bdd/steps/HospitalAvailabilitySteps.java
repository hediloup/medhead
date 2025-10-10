package com.medhead.poc.bdd.steps;

import io.cucumber.java.en.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Définitions des étapes pour vérifier la disponibilité des lits selon les
 * spécialités. Ce scénario est basé sur un tableau d’exemples et illustre
 * comment calculer un statut en fonction du nombre de lits disponibles.
 */
public class HospitalAvailabilitySteps {
    private int lits;
    private String specialite;
    private String etat;

    @Given("un hôpital nommé {string} ayant {string} lits disponibles en {string}")
    public void un_hôpital_nommé_ayant_lits_disponibles_en(String hopital, String lits, String specialite) {
        // On ignore le nom de l’hôpital pour le calcul, on stocke seulement le nombre de lits et la spécialité.
        this.lits = Integer.parseInt(lits);
        this.specialite = specialite;
    }

    @When("le système vérifie la disponibilité pour {string}")
    public void le_système_vérifie_la_disponibilité_pour(String specialiteDemandée) {
        if (this.lits > 0 && this.specialite.equals(specialiteDemandée)) {
            this.etat = "disponible";
        } else {
            this.etat = "indisponible";
        }
    }

    @Then("le statut doit être {string}")
    public void le_statut_doit_être(String expected) {
        assertThat(this.etat).isEqualTo(expected);
    }
}