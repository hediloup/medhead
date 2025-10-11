Feature: Performance and resilience under load
  As a QA engineer
  I want to validate that the service responds in less than 200 ms
  Even under 800 requests per second

  Scenario: Allocation API performance test
    Given a load generator simulating 800 requests/s on endpoint "/api/allocate"
    When responses are measured over a duration of 2 minutes
    Then 95% of requests must have a response time < 200 ms
    And no timeout or 5xx should be observed