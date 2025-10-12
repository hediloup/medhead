# language: en
Feature: Hospital allocation for emergency patients
  As an emergency medical system
  I want to automatically allocate an appropriate hospital to a patient
  So that I can optimize care and reduce waiting times

  Scenario: Successful allocation of hospital with available specialty
    Given there is a hospital "Central Hospital" with specialty "Cardiology" and 5 available beds
    And the patient is located at coordinates 53.3976314, -2.1829641
    When I request an allocation for specialty "Cardiology"
    Then hospital "Central Hospital" should be recommended
    And the distance should be calculated correctly
    And the estimated arrival time should be provided
    And the number of available beds after allocation should be 4

  Scenario: Selection of closest hospital among multiple options
    Given there are multiple hospitals with specialty "Cardiology":
      | Name             | Latitude   | Longitude  | Beds |
      | Central Hospital | 53.3976314 | -2.1829641 | 5    |
      | North Hospital   | 53.4808    | -2.2426    | 3    |
      | South Hospital   | 53.3500    | -2.1000    | 4    |
    And the patient is located at coordinates 53.3976314, -2.1829641
    When I request an allocation for specialty "Cardiology"
    Then the closest hospital should be selected
    And the response should contain route information

  Scenario: Allocation failure - no specialty available
    Given there is no hospital with specialty "Pediatrics"
    And the patient is located at coordinates 53.3976314, -2.1829641
    When I request an allocation for specialty "Pediatrics"
    Then I should receive an error "No hospital available"
    And the HTTP status code should be 404

  Scenario: Allocation failure - no beds available
    Given there is a hospital "Full Hospital" with specialty "Cardiology" and 0 available beds
    And the patient is located at coordinates 53.3976314, -2.1829641
    When I request an allocation for specialty "Cardiology"
    Then I should receive an error "No hospital available"
    And the HTTP status code should be 404

  Scenario: Input parameter validation - missing specialty
    Given the patient is located at coordinates 53.3976314, -2.1829641
    When I request an allocation with empty specialty ""
    Then I should receive a validation error
    And the HTTP status code should be 400

  Scenario: Input parameter validation - missing coordinates
    Given there is a hospital with specialty "Cardiology"
    When I request an allocation for specialty "Cardiology" with null coordinates
    Then I should receive a validation error
    And the HTTP status code should be 400

  Scenario: Test GET endpoint
    Given there is a hospital "Central Hospital" with specialty "Cardiology"
    When I call the GET /api/allocate endpoint with parameters:
      | Parameter | Value        |
      | specialty | Cardiology   |
      | latitude  | 53.3976314   |
      | longitude | -2.1829641   |
    Then the response should be identical to the POST endpoint
    And hospital "Central Hospital" should be recommended

  Scenario: API health status verification
    When I call the /api/health endpoint
    Then I should receive the message "Allocation API operational"
    And the HTTP status code should be 200