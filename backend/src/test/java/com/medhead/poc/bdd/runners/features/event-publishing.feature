Feature: Event publishing after bed allocation
  As an orchestration service
  I want to publish an event after a successful allocation
  In order to ensure consistency between microservices

  Scenario: Publishing a "BED_RESERVED" event
    Given a validated bed request for "fred_brooks_001"
    When the system confirms the reservation
    Then a message with type "BED_RESERVED" is published on topic "hospital.events"
    And the message contains:
      | field       | value            |
      | hospital_id | fred_brooks_001  |
      | speciality  | Cardiology       |
      | timestamp   | non null         |