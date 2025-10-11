package com.medhead.poc.bdd.steps;

import io.cucumber.java.en.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Step definitions for checking bed availability by specialty.
 * This scenario is based on an example table and illustrates
 * how to calculate a status based on the number of available beds.
 */
public class HospitalAvailabilitySteps {
    private int beds;
    private String specialty;
    private String status;

    @Given("a hospital named {string} having {string} available beds in {string}")
    public void a_hospital_named_having_available_beds_in(String hospital, String beds, String specialty) {
        // Ignore hospital name for calculation, only store bed count and specialty.
        this.beds = Integer.parseInt(beds);
        this.specialty = specialty;
    }

    @When("the system checks availability for {string}")
    public void the_system_checks_availability_for(String requestedSpecialty) {
        if (this.beds > 0 && this.specialty.equals(requestedSpecialty)) {
            this.status = "available";
        } else {
            this.status = "unavailable";
        }
    }

    @Then("the status must be {string}")
    public void the_status_must_be(String expected) {
        assertThat(this.status).isEqualTo(expected);
    }
}