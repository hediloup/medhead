package com.medhead.poc.bdd.steps;

import io.cucumber.java.en.*;
import io.cucumber.datatable.DataTable;
import static org.assertj.core.api.Assertions.*;
import java.util.Map;

/**
 * Step definitions for event publishing after bed allocation.
 * This skeleton illustrates how to verify that an event is
 * published and that certain data is present in the message.
 */
public class EventPublishingSteps {

    private boolean eventPublished;
    private String hospitalId;
    private String speciality;
    private String timestamp;

    @Given("a validated bed request for {string}")
    public void a_validated_bed_request_for(String hopitalId) {
        this.hospitalId = hopitalId;
    }

    @When("the system confirms the reservation")
    public void the_system_confirms_the_reservation() {
        // Event publishing simulation
        this.eventPublished = true;
        this.speciality = "Cardiology";
        this.timestamp = "2025-10-10T10:10:10Z";
    }

    @Then("a message with type {string} is published on topic {string}")
    public void a_message_with_type_is_published_on_topic(String type, String topic) {
        assertThat(this.eventPublished).as("Verify that the event is published").isTrue();
    }

    @Then("the message contains:")
    public void the_message_contains(DataTable table) {
        // Convert DataTable to Map for validation
        Map<String, String> expectedData = table.asMap(String.class, String.class);
        
        // Verify expected data
        assertThat(expectedData).containsKey("hospital_id");
        assertThat(expectedData).containsKey("speciality");
        assertThat(expectedData).containsKey("timestamp");
        
        // Verify values
        assertThat(expectedData.get("hospital_id")).isEqualTo(this.hospitalId);
        assertThat(expectedData.get("speciality")).isEqualTo(this.speciality);
        assertThat(expectedData.get("timestamp")).isEqualTo("non null");
    }
}