package com.medhead.poc.bdd.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Runner spécialisé pour les tests BDD d'anonymisation des patients
 * Approche BDD : Behavior-Driven Development
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/anonymisation-patient.feature",
        glue = "com.medhead.poc.bdd.steps",
        plugin = {
                "pretty",
                "html:target/cucumber-reports/anonymisation.html",
                "json:target/cucumber-reports/anonymisation.json"
        },
        monochrome = true,
        tags = "@anonymisation or @patient"
)
public class AnonymisationBddTest {
    // Cette classe est vide car elle sert uniquement de point d'entrée pour Cucumber
}
