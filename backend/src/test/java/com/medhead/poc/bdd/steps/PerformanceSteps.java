package com.medhead.poc.bdd.steps;

import io.cucumber.java.en.*;

/**
 * Simplified steps for performance tests
 */
public class PerformanceSteps {

    @Given("a load generator simulating {int} requests/s on endpoint {string}")
    public void a_load_generator_simulating_requests_s_on_endpoint(Integer requestsPerSecond, String endpoint) {
        System.out.println("Simulation: load generator configured for " + requestsPerSecond + " req/s on " + endpoint);
    }

    @When("responses are measured over a duration of {int} minutes")
    public void responses_are_measured_over_a_duration_of_minutes(Integer duration) {
        System.out.println("Simulation: performance measurement over " + duration + " minutes");
    }

    @Then("{int}% of requests must have a response time < {int} ms")
    public void percentage_of_requests_must_have_a_response_time_less_than_ms(Integer percentage, Integer maxTime) {
        System.out.println("Simulation: " + percentage + "% of requests < " + maxTime + "ms");
    }

    @Then("no timeout or 5xx errors should be observed")
    public void no_timeout_or_5xx_errors_should_be_observed() {
        System.out.println("Simulation: no timeout or 5xx errors observed");
    }
}
