package com.medhead.poc.bdd.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Runner JUnit pour les tests BDD Cucumber optimisés.
 * 
 * Ce runner exécute uniquement les scénarios non-API pour éviter les dépendances externes.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "com.medhead.poc.bdd.steps",
    plugin = {
        "pretty", 
        "json:target/cucumber-reports/cucumber.json", 
        "html:target/cucumber-reports/cucumber.html",
        "junit:target/cucumber-reports/cucumber.xml"
    },
    tags = "not @api and not @e2e",
    monochrome = true,
    dryRun = false
)
public class CucumberBddTest {
    // Configuration via @CucumberOptions
}
