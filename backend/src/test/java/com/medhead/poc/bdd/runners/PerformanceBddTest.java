package com.medhead.poc.bdd.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Runner spécialisé pour les tests BDD de performance
 * Approche BDD : Behavior-Driven Development
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/performance-api.feature",
        glue = "com.medhead.poc.bdd.steps",
        plugin = {
                "pretty",
                "html:target/cucumber-reports/performance.html",
                "json:target/cucumber-reports/performance.json"
        },
        monochrome = true,
        tags = "@performance or @load or @stress"
)
public class PerformanceBddTest {
    // Cette classe est vide car elle sert uniquement de point d'entrée pour Cucumber
}
