package com.medhead.poc.bdd.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * BDD test runner for hospital allocation scenarios
 * BDD approach: Behavior-Driven Development
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/allocation-hospital.feature",
        glue = "com.medhead.poc.bdd.steps",
        plugin = {
                "pretty",
                "html:target/cucumber-reports/allocation.html",
                "json:target/cucumber-reports/allocation.json"
        },
        monochrome = true,
        tags = "@allocation or @hospital"
)
public class AllocationBddTest {
    // This class serves as entry point for Cucumber
}
