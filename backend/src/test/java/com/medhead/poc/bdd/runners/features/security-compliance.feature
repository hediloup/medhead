Feature: Security and GDPR compliance
  As a software architect
  I want to ensure that patient data is anonymized and secure
  In order to comply with GDPR and architecture principles

  Scenario: Data anonymization before sending
    Given a Patient object containing "name", "birth_date", "pathology"
    When the allocation request is sent
    Then the "name" field must be replaced by an anonymous identifier
    And no personally identifiable data is transmitted to the API