Feature: Continuous validation in CI/CD pipeline
  As a DevOps team
  I want all BDD tests to be executed automatically on each push
  In order to ensure quality and traceability of deliveries

  Scenario: Automated BDD test execution
    Given a commit is pushed to branch "main"
    When the CI/CD pipeline is triggered
    Then the "build", "test", "deploy" steps must execute successfully
    And a test report is generated in /reports/cucumber.json
    And the pipeline status must be "passed"