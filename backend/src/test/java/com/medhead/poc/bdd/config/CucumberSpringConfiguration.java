package com.medhead.poc.bdd.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring configuration for Cucumber BDD tests
 */
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
public class CucumberSpringConfiguration {
    // This class serves as entry point for Spring configuration in Cucumber
    // WebEnvironment.NONE to avoid starting a complete web server
}
