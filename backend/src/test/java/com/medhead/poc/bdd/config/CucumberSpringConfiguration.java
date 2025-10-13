package com.medhead.poc.bdd.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring configuration for Cucumber BDD tests
 */
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
public class CucumberSpringConfiguration {
    // This class serves as entry point for Spring configuration in Cucumber
    // WebEnvironment.RANDOM_PORT to enable TestRestTemplate for API testing
}
