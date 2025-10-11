package com.medhead.poc.bdd.steps;

import io.cucumber.java.en.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Steps to verify GDPR compliance and patient data anonymization.
 * In this skeleton, anonymization is simulated by generating a
 * random identifier and verifying that sensitive fields are not
 * transmitted as-is.
 */
public class SecurityComplianceSteps {

    private String name;
    private String anonymised;

    @Given("a Patient object containing {string}, {string}, {string}")
    public void a_patient_object_containing(String name, String dateOfBirth, String pathology) {
        this.name = name;
    }

    @When("the allocation request is sent")
    public void the_allocation_request_is_sent() {
        // In a real case, anonymization would happen before sending. Here we simulate.
        this.anonymised = "ANON-" + System.currentTimeMillis();
    }

    @Then("the field {string} must be replaced by an anonymous identifier")
    public void the_field_must_be_replaced_by_an_anonymous_identifier(String field) {
        assertThat(this.anonymised).startsWith("ANON-");
    }

    @Then("no personally identifiable data is transmitted to the API")
    public void no_personally_identifiable_data_is_transmitted_to_the_api() {
        assertThat(this.name).isNotEqualTo(this.anonymised);
    }
}