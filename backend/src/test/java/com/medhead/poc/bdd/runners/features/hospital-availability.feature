Feature: Bed availability verification
  As a hospital service
  I want to manage bed availability based on specialties
  So that the allocation engine can make reliable decisions

  Scenario Outline: Availability validation by specialty
    Given a hospital named "<hospital>" having "<beds>" available beds in "<specialty>"
    When the system checks availability for "<specialty>"
    Then the status must be "<state>"

    Examples:
      | hospital       | beds | specialty     | state       |
      | Fred Brooks    | 2    | Cardiology    | available   |
      | Julia Crusher  | 0    | Cardiology    | unavailable |
      | Beverly Bashir | 5    | Immunology    | available   |