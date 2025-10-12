package com.medhead.poc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Complete test suite for MedHead project
 * Includes all types of tests: TDD unit tests, TDD integration tests, and BDD
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        // TDD unit tests
        com.medhead.poc.unit.service.AllocationServiceTest.class,
        com.medhead.poc.unit.service.DistanceCalculationServiceTest.class,
        com.medhead.poc.unit.service.PatientAnonymizationServiceTest.class,
        com.medhead.poc.unit.controller.AllocationControllerTest.class,
        
        // TDD integration tests
        com.medhead.poc.integration.AllocationIntegrationTest.class,
        com.medhead.poc.integration.RepositoryIntegrationTest.class,
        
        // BDD tests with Cucumber
        com.medhead.poc.bdd.runners.AllocationBddTest.class
})
public class TestSuite {
    // This class is empty as it serves only as a container for the test suite
    // All tests are defined in the @Suite.SuiteClasses annotation
}
