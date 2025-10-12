package com.medhead.poc.bdd.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Runner spécialisé pour les tests BDD de calcul de distance
 * Approche BDD : Behavior-Driven Development
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/calcul-distance.feature",
        glue = "com.medhead.poc.bdd.steps",
        plugin = {
                "pretty",
                "html:target/cucumber-reports/distance.html",
                "json:target/cucumber-reports/distance.json"
        },
        monochrome = true,
        tags = "@distance or @route"
)
public class DistanceBddTest {
    // Cette classe est vide car elle sert uniquement de point d'entrée pour Cucumber
}
