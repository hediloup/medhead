package com.medhead.poc.bdd.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Runner JUnit pour lancer les scénarios Cucumber.  
 *
 * Cette classe configure Cucumber pour rechercher les fichiers `.feature` 
 * dans le répertoire `src/test/resources/features`.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "com.medhead.poc.bdd.steps",
    plugin = {"pretty", "json:target/cucumber-reports/cucumber.json", "html:target/cucumber-reports/cucumber.html"}
)
public class CucumberTest {
    // Configuration via @CucumberOptions
}