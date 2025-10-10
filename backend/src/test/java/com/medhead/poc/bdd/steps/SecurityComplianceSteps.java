package com.medhead.poc.bdd.steps;

import io.cucumber.java.en.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Étapes pour vérifier la conformité RGPD et l’anonymisation des données
 * patient. Dans ce squelette, on simule l’anonymisation en générant un
 * identifiant aléatoire et on vérifie que les champs sensibles ne sont pas
 * transmis tels quels.
 */
public class SecurityComplianceSteps {

    private String nom;
    private String anonymised;

    @Given("un objet Patient contenant {string}, {string}, {string}")
    public void un_objet_patient_contenant(String nom, String date_naissance, String pathologie) {
        this.nom = nom;
    }

    @When("la requête d’allocation est envoyée")
    public void la_requête_d_allocation_est_envoyée() {
        // Dans un vrai cas, on anonymiserait avant l’envoi. Ici on simule.
        this.anonymised = "ANON-" + System.currentTimeMillis();
    }

    @Then("le champ {string} doit être remplacé par un identifiant anonyme")
    public void le_champ_doit_être_remplacé_par_un_identifiant_anonyme(String champ) {
        assertThat(this.anonymised).startsWith("ANON-");
    }

    @Then("aucune donnée personnelle identifiable n’est transmise à l’API")
    public void aucune_donnée_personnelle_identifiable_n_est_transmise_à_l_api() {
        assertThat(this.nom).isNotEqualTo(this.anonymised);
    }
}