package com.medhead.poc.bdd.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Runner principal pour les tests BDD avec Cucumber
 * Approche BDD : Behavior-Driven Development
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.medhead.poc.bdd.steps", "com.medhead.poc.bdd.hooks", "com.medhead.poc.bdd.config"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/cucumber.html",
                "json:target/cucumber-reports/cucumber.json",
                "junit:target/cucumber-reports/cucumber.xml"
        },
        monochrome = true,
        tags = "not @ignore",
        objectFactory = io.cucumber.spring.SpringFactory.class
)
public class CucumberBddTest {
    // Cette classe est vide car elle sert uniquement de point d'entrée pour Cucumber
}
