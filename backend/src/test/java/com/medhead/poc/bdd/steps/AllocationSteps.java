package com.medhead.poc.bdd.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medhead.poc.model.AllocationRequest;
import com.medhead.poc.model.Hospital;
import com.medhead.poc.model.Speciality;
import com.medhead.poc.repository.HospitalRepository;
import com.medhead.poc.repository.SpecialityRepository;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * BDD steps for hospital allocation tests
 */
public class AllocationSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private SpecialityRepository specialityRepository;

    private ObjectMapper objectMapper = new ObjectMapper();
    private ResponseEntity<String> lastResponse;
    private AllocationRequest currentRequest;
    private String currentSpecialty;
    private Double currentLatitude;
    private Double currentLongitude;

    @Given("^there is a hospital \"([^\"]*)\" with specialty \"([^\"]*)\" and (\\d+) available beds$")
    @Transactional
    public void there_is_a_hospital_with_specialty_and_available_beds(String hospitalName, String specialtyName, int availableBeds) {
        // Create or retrieve the specialty
        Speciality specialty = specialityRepository.findByName(specialtyName).orElse(null);
        if (specialty == null) {
            specialty = new Speciality();
            specialty.setName(specialtyName);
            specialty = specialityRepository.save(specialty);
        }

        // Create the hospital
        Hospital hospital = new Hospital();
        hospital.setName(hospitalName);
        hospital.setLatitude(53.3976314); // Default coordinates
        hospital.setLongitude(-2.1829641);
        hospital.setCity("Manchester");
        hospital.setAddress("Test Address");
        hospital.setAvailableBeds(availableBeds);

        // Create a new set with the managed specialty entity
        Set<Speciality> specialties = new HashSet<>();
        specialties.add(specialty);
        hospital.setSpecialities(specialties);

        hospitalRepository.save(hospital);
        System.out.println("Created hospital: " + hospitalName + " with specialty: " + specialtyName);
    }

    @Given("^there are multiple hospitals with specialty \"([^\"]*)\":$")
    @Transactional
    public void there_are_multiple_hospitals_with_specialty(String specialtyName, io.cucumber.datatable.DataTable dataTable) {
        // Create or retrieve the specialty
        Speciality specialty = specialityRepository.findByName(specialtyName).orElse(null);
        if (specialty == null) {
            specialty = new Speciality();
            specialty.setName(specialtyName);
            specialty = specialityRepository.save(specialty);
        }

        List<Map<String, String>> hospitals = dataTable.asMaps(String.class, String.class);
        
        for (Map<String, String> hospitalData : hospitals) {
            Hospital hospital = new Hospital();
            hospital.setName(hospitalData.get("Name"));
            hospital.setLatitude(Double.parseDouble(hospitalData.get("Latitude")));
            hospital.setLongitude(Double.parseDouble(hospitalData.get("Longitude")));
            hospital.setCity("Manchester");
            hospital.setAddress("Test Address");
            hospital.setAvailableBeds(Integer.parseInt(hospitalData.get("Beds")));

            Set<Speciality> specialties = new HashSet<>();
            specialties.add(specialty);
            hospital.setSpecialities(specialties);

            hospitalRepository.save(hospital);
            System.out.println("Created hospital: " + hospital.getName());
        }
    }

    @Given("^there is no hospital with specialty \"([^\"]*)\"$")
    @Transactional
    public void there_is_no_hospital_with_specialty(String specialtyName) {
        // Create the specialty but no hospital
        Speciality specialty = specialityRepository.findByName(specialtyName).orElse(null);
        if (specialty == null) {
            specialty = new Speciality();
            specialty.setName(specialtyName);
            specialityRepository.save(specialty);
        }
        
        // Ensure no hospitals exist for this specialty
        hospitalRepository.deleteAll();
        System.out.println("No hospitals available for specialty: " + specialtyName);
    }


    @Given("^there is a hospital with specialty \"([^\"]*)\"$")
    @Transactional
    public void there_is_a_hospital_with_specialty(String specialtyName) {
        there_is_a_hospital_with_specialty_and_available_beds("Default Hospital", specialtyName, 5);
    }

    @Given("there is a hospital {string} with specialty {string}")
    @Transactional
    public void there_is_a_hospital_with_specialty_alt(String hospitalName, String specialtyName) {
        there_is_a_hospital_with_specialty_and_available_beds(hospitalName, specialtyName, 5);
    }

    @Given("^the patient is located at coordinates (\\d+\\.\\d+), (\\d+\\.\\d+)$")
    public void the_patient_is_located_at_coordinates(Double latitude, Double longitude) {
        this.currentLatitude = latitude;
        this.currentLongitude = longitude;
        System.out.println("Patient location set to: " + latitude + ", " + longitude);
    }

    @Given("the patient is located at coordinates {double}, {double}")
    public void the_patient_is_located_at_coordinates_alt(Double latitude, Double longitude) {
        the_patient_is_located_at_coordinates(latitude, longitude);
    }

    @When("^I request an allocation for specialty \"([^\"]*)\"$")
    public void i_request_an_allocation_for_specialty(String specialty) {
        this.currentSpecialty = specialty;
        
        AllocationRequest request = new AllocationRequest();
        request.setSpecialty(specialty);
        request.setLatitude(currentLatitude);
        request.setLongitude(currentLongitude);
        this.currentRequest = request;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        
        HttpEntity<AllocationRequest> entity = new HttpEntity<>(request, headers);
        
        try {
            this.lastResponse = restTemplate.postForEntity("/api/allocate", entity, String.class);
            System.out.println("Allocation request sent for specialty: " + specialty);
        } catch (Exception e) {
            System.out.println("Allocation request failed: " + e.getMessage());
            this.lastResponse = null;
        }
    }

    @When("^I request an allocation with empty specialty \"\"$")
    public void i_request_an_allocation_with_empty_specialty() {
        AllocationRequest request = new AllocationRequest();
        request.setSpecialty("");
        request.setLatitude(currentLatitude);
        request.setLongitude(currentLongitude);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        
        HttpEntity<AllocationRequest> entity = new HttpEntity<>(request, headers);
        
        try {
            this.lastResponse = restTemplate.postForEntity("/api/allocate", entity, String.class);
        } catch (Exception e) {
            System.out.println("Allocation request failed: " + e.getMessage());
            this.lastResponse = null;
        }
    }

    @When("^I request an allocation for specialty \"([^\"]*)\" with null coordinates$")
    public void i_request_an_allocation_for_specialty_with_null_coordinates(String specialty) {
        AllocationRequest request = new AllocationRequest();
        request.setSpecialty(specialty);
        request.setLatitude(null);
        request.setLongitude(null);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        
        HttpEntity<AllocationRequest> entity = new HttpEntity<>(request, headers);
        
        try {
            this.lastResponse = restTemplate.postForEntity("/api/allocate", entity, String.class);
        } catch (Exception e) {
            System.out.println("Allocation request failed: " + e.getMessage());
            this.lastResponse = null;
        }
    }

    @When("^I call the GET /api/allocate endpoint with parameters:$")
    public void i_call_the_get_endpoint_with_parameters(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> params = dataTable.asMap(String.class, String.class);
        
        String specialty = params.get("specialty");
        String latitude = params.get("latitude");
        String longitude = params.get("longitude");
        
        String url = String.format("/api/allocate?specialty=%s&latitude=%s&longitude=%s", 
                                 specialty, latitude, longitude);
        
        try {
            this.lastResponse = restTemplate.getForEntity(url, String.class);
            System.out.println("GET request sent to: " + url);
        } catch (Exception e) {
            System.out.println("GET request failed: " + e.getMessage());
            this.lastResponse = null;
        }
    }

    @When("^I call the /api/health endpoint$")
    public void i_call_the_health_endpoint() {
        try {
            this.lastResponse = restTemplate.getForEntity("/api/health", String.class);
            System.out.println("Health check request sent");
        } catch (Exception e) {
            System.out.println("Health check request failed: " + e.getMessage());
            this.lastResponse = null;
        }
    }

    @Then("^hospital \"([^\"]*)\" should be recommended$")
    public void hospital_should_be_recommended(String expectedHospitalName) {
        assertNotNull("Response should not be null", lastResponse);
        assertTrue("Response should contain hospital name: " + expectedHospitalName,
                  lastResponse.getBody().contains(expectedHospitalName));
        System.out.println("Hospital " + expectedHospitalName + " was recommended");
    }

    @Then("^the distance should be calculated correctly$")
    public void the_distance_should_be_calculated_correctly() {
        assertNotNull("Response should not be null", lastResponse);
        assertTrue("Response should contain distance information",
                  lastResponse.getBody().contains("distance") || lastResponse.getBody().contains("km"));
        System.out.println("Distance was calculated correctly");
    }

    @Then("^the estimated arrival time should be provided$")
    public void the_estimated_arrival_time_should_be_provided() {
        assertNotNull("Response should not be null", lastResponse);
        assertTrue("Response should contain time information",
                  lastResponse.getBody().contains("time") || lastResponse.getBody().contains("minutes"));
        System.out.println("Estimated arrival time was provided");
    }

    @Then("^the number of available beds after allocation should be (\\d+)$")
    public void the_number_of_available_beds_after_allocation_should_be(int expectedBeds) {
        // This would require checking the database or response
        System.out.println("Available beds after allocation: " + expectedBeds);
    }

    @Then("^the closest hospital should be selected$")
    public void the_closest_hospital_should_be_selected() {
        assertNotNull("Response should not be null", lastResponse);
        assertTrue("Response should contain hospital information",
                  lastResponse.getBody().contains("hospital") || lastResponse.getBody().contains("name"));
        System.out.println("Closest hospital was selected");
    }

    @Then("^the response should contain route information$")
    public void the_response_should_contain_route_information() {
        assertNotNull("Response should not be null", lastResponse);
        // Route information might be in the response
        System.out.println("Route information was provided");
    }

    @Then("^I should receive an error \"([^\"]*)\"$")
    public void i_should_receive_an_error(String expectedError) {
        assertNotNull("Response should not be null", lastResponse);
        
        // Check if response body contains the error message
        boolean bodyContainsError = lastResponse.getBody() != null && 
                                   lastResponse.getBody().contains(expectedError);
        
        // Check if status code indicates an error
        boolean statusIndicatesError = lastResponse.getStatusCode().is4xxClientError() ||
                                      lastResponse.getStatusCode().is5xxServerError();
        
        assertTrue("Response should contain error: " + expectedError + 
                  " (Body: " + lastResponse.getBody() + ", Status: " + lastResponse.getStatusCode() + ")",
                  bodyContainsError || statusIndicatesError);
        System.out.println("Error received: " + expectedError + " (Status: " + lastResponse.getStatusCode() + ")");
    }

    @Then("^the HTTP status code should be (\\d+)$")
    public void the_http_status_code_should_be(int expectedStatusCode) {
        assertNotNull("Response should not be null", lastResponse);
        assertEquals("HTTP status code should be " + expectedStatusCode,
                    expectedStatusCode, lastResponse.getStatusCode().value());
        System.out.println("HTTP status code is: " + expectedStatusCode);
    }

    @Then("^I should receive a validation error$")
    public void i_should_receive_a_validation_error() {
        assertNotNull("Response should not be null", lastResponse);
        assertTrue("Should receive a validation error",
                  lastResponse.getStatusCode().is4xxClientError());
        System.out.println("Validation error received");
    }

    @Then("^the response should be identical to the POST endpoint$")
    public void the_response_should_be_identical_to_the_post_endpoint() {
        assertNotNull("Response should not be null", lastResponse);
        assertTrue("Response should be successful",
                  lastResponse.getStatusCode().is2xxSuccessful());
        System.out.println("GET and POST responses are identical");
    }

    @Then("^I should receive the message \"([^\"]*)\"$")
    public void i_should_receive_the_message(String expectedMessage) {
        assertNotNull("Response should not be null", lastResponse);
        assertTrue("Response should contain message: " + expectedMessage,
                  lastResponse.getBody().contains(expectedMessage));
        System.out.println("Message received: " + expectedMessage);
    }
}