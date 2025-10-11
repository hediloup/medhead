Feature: Continuous validation in CI/CD pipeline
  As a DevOps team
  I want all BDD tests to be executed automatically on every push
  In order to guarantee quality and traceability of deliveries

  Scenario: Automated execution of BDD tests
    Given a commit is pushed on branch "main"
    When the CI/CD pipeline is triggered
    Then the steps "build", "test", "deploy" must execute successfully
    And a test report is generated in /reports/cucumber.json
    And the pipeline status must be "passed"