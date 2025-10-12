package com.medhead.poc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Suite de tests complète pour le projet MedHead
 * Inclut tous les types de tests : TDD unitaires, TDD d'intégration, et BDD
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        // Tests TDD unitaires
        com.medhead.poc.unit.service.AllocationServiceTest.class,
        com.medhead.poc.unit.service.DistanceCalculationServiceTest.class,
        com.medhead.poc.unit.service.PatientAnonymizationServiceTest.class,
        com.medhead.poc.unit.controller.AllocationControllerTest.class,
        
        // Tests TDD d'intégration
        com.medhead.poc.integration.AllocationIntegrationTest.class,
        com.medhead.poc.integration.RepositoryIntegrationTest.class,
        
        // Tests BDD avec Cucumber
        com.medhead.poc.bdd.runners.CucumberBddTest.class
})
public class TestSuite {
    // Cette classe est vide car elle sert uniquement de conteneur pour la suite de tests
    // Tous les tests sont définis dans l'annotation @Suite.SuiteClasses
}
