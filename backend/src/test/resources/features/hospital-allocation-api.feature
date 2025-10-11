@api @e2e
Feature: Hospital bed allocation by specialty and location
  As an emergency intervention system
  I want to recommend the most appropriate nearest hospital
  In order to assign an available bed in the right specialty

  Scenario: Assigning a cardiology bed for a patient near Fred Brooks
    Given a patient requiring care in "Cardiology"
    And the patient location is "51.5009, -0.1253"
    When the allocation API is called with these parameters
    Then the HTTP code must be 200
    And the response must contain "Fred Brooks Hospital"