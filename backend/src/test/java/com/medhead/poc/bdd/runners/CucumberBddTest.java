package com.medhead.poc.bdd.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * JUnit runner for optimized Cucumber BDD tests.
 * 
 * This runner executes only non-API scenarios to avoid external dependencies.
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
