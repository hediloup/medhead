package com.medhead.poc.bdd.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Configuration Spring pour les tests BDD Cucumber
 */
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
public class CucumberSpringConfiguration {
    // Cette classe sert de point d'entrée pour la configuration Spring dans Cucumber
    // WebEnvironment.NONE pour éviter le démarrage d'un serveur web complet
}
