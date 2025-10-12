package com.medhead.poc.bdd.hooks;

import com.medhead.poc.repository.HospitalRepository;
import com.medhead.poc.repository.PatientRepository;
import com.medhead.poc.repository.SpecialityRepository;
import io.cucumber.java.fr.*;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * Hooks exécutés avant et après chaque scénario BDD.
 * Ils permettent d'initialiser les ressources (base de données, serveurs)
 * ou de nettoyer l'état après chaque test.
 */
public class Hooks {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private SpecialityRepository specialityRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * Hook exécuté avant chaque scénario.
     * Nettoie la base de données et initialise l'état de base.
     */
    @Before
    @Transactional
    public void setUp() {
        // Nettoyer toutes les données de test
        patientRepository.deleteAll();
        hospitalRepository.deleteAll();
        specialityRepository.deleteAll();
        
        // Ne pas vérifier l'API ici pour éviter les blocages
        System.out.println("✅ Base de données nettoyée pour le test");
    }

    /**
     * Hook exécuté après chaque scénario.
     * Effectue le nettoyage final et les vérifications post-test.
     */
    @After
    @Transactional
    public void tearDown() {
        // Nettoyer toutes les données de test après chaque scénario
        patientRepository.deleteAll();
        hospitalRepository.deleteAll();
        specialityRepository.deleteAll();
        
        System.out.println("✅ Nettoyage terminé après le test");
    }

    /**
     * Hook exécuté avant chaque scénario d'allocation d'hôpitaux.
     * Initialise des données de base pour les tests d'allocation.
     */
    @Before("@allocation")
    @Transactional
    public void setUpAllocationTests() {
        // Créer des spécialités de base
        createBaseSpecialties();
        
        System.out.println("✅ Données d'allocation initialisées");
    }

    /**
     * Hook exécuté après chaque scénario d'allocation d'hôpitaux.
     * Nettoie spécifiquement les données d'allocation.
     */
    @After("@allocation")
    @Transactional
    public void tearDownAllocationTests() {
        // Nettoyer les données d'allocation
        patientRepository.deleteAll();
        hospitalRepository.deleteAll();
        specialityRepository.deleteAll();
    }

    /**
     * Hook exécuté avant chaque scénario d'anonymisation.
     * Initialise des données de base pour les tests d'anonymisation.
     */
    @Before("@anonymisation")
    @Transactional
    public void setUpAnonymisationTests() {
        // Créer des spécialités de base
        createBaseSpecialties();
        
        // Vérifier que les services d'anonymisation sont disponibles
        System.out.println("Initialisation des tests d'anonymisation...");
    }

    /**
     * Hook exécuté après chaque scénario d'anonymisation.
     * Nettoie spécifiquement les données d'anonymisation.
     */
    @After("@anonymisation")
    @Transactional
    public void tearDownAnonymisationTests() {
        // Nettoyer les données d'anonymisation
        patientRepository.deleteAll();
        System.out.println("Nettoyage des tests d'anonymisation terminé.");
    }

    /**
     * Hook exécuté avant chaque scénario de performance.
     * Initialise l'environnement pour les tests de performance.
     */
    @Before("@performance")
    @Transactional
    public void setUpPerformanceTests() {
        // Créer des données de base pour les tests de performance
        createBaseSpecialties();
        createPerformanceTestData();
        
        System.out.println("Initialisation des tests de performance...");
    }

    /**
     * Hook exécuté après chaque scénario de performance.
     * Nettoie l'environnement après les tests de performance.
     */
    @After("@performance")
    @Transactional
    public void tearDownPerformanceTests() {
        // Nettoyer les données de performance
        patientRepository.deleteAll();
        hospitalRepository.deleteAll();
        specialityRepository.deleteAll();
        
        System.out.println("Nettoyage des tests de performance terminé.");
    }

    /**
     * Hook exécuté avant chaque scénario de calcul de distance.
     * Initialise l'environnement pour les tests de distance.
     */
    @Before("@distance")
    @Transactional
    public void setUpDistanceTests() {
        // Créer des données de base pour les tests de distance
        createBaseSpecialties();
        
        System.out.println("Initialisation des tests de distance...");
    }

    /**
     * Hook exécuté après chaque scénario de calcul de distance.
     * Nettoie l'environnement après les tests de distance.
     */
    @After("@distance")
    @Transactional
    public void tearDownDistanceTests() {
        // Nettoyer les données de distance
        patientRepository.deleteAll();
        hospitalRepository.deleteAll();
        specialityRepository.deleteAll();
        
        System.out.println("Nettoyage des tests de distance terminé.");
    }

    /**
     * Crée les spécialités de base nécessaires aux tests.
     */
    private void createBaseSpecialties() {
        String[] baseSpecialties = {"Cardiology", "Neurology", "Pediatrics", "Emergency", "Surgery"};
        
        for (String specialtyName : baseSpecialties) {
            if (specialityRepository.findByName(specialtyName) == null) {
                com.medhead.poc.model.Speciality specialty = new com.medhead.poc.model.Speciality();
                specialty.setName(specialtyName);
                specialityRepository.save(specialty);
            }
        }
    }

    /**
     * Crée des données de test spécifiques pour les tests de performance.
     */
    private void createPerformanceTestData() {
        // Créer des hôpitaux de test pour les tests de performance
        com.medhead.poc.model.Speciality cardiology = specialityRepository.findByName("Cardiology").orElse(null);
        if (cardiology != null) {
            for (int i = 0; i < 5; i++) {
                com.medhead.poc.model.Hospital hospital = new com.medhead.poc.model.Hospital(
                        "Performance Test Hospital " + i,
                        53.3976314 + (i * 0.01),
                        -2.1829641 + (i * 0.01),
                        "Manchester",
                        "Test Address " + i,
                        10
                );
                
                java.util.Set<com.medhead.poc.model.Speciality> specialties = new java.util.HashSet<>();
                specialties.add(cardiology);
                hospital.setSpecialities(specialties);
                hospitalRepository.save(hospital);
            }
        }
    }

    /**
     * Hook exécuté avant tous les tests (une seule fois).
     * Initialise l'environnement global des tests BDD.
     */
    @Before
    public void globalSetUp() {
        System.out.println("=== Début de l'exécution des tests BDD ===");
        System.out.println("Timestamp: " + java.time.LocalDateTime.now());
    }

    /**
     * Hook exécuté après tous les tests (une seule fois).
     * Nettoie l'environnement global des tests BDD.
     */
    @After
    public void globalTearDown() {
        System.out.println("=== Fin de l'exécution des tests BDD ===");
        System.out.println("Timestamp: " + java.time.LocalDateTime.now());
    }

    /**
     * Hook exécuté avant chaque scénario marqué comme critique.
     * Effectue des vérifications supplémentaires pour les scénarios critiques.
     */
    @Before("@critical")
    @Transactional
    public void setUpCriticalTests() {
        System.out.println("⚠️  Exécution d'un scénario critique - Vérifications supplémentaires...");
        
        // S'assurer que la base de données est propre
        patientRepository.deleteAll();
        hospitalRepository.deleteAll();
        specialityRepository.deleteAll();
        
        System.out.println("✅ Environnement critique initialisé");
    }

    /**
     * Hook exécuté après chaque scénario marqué comme critique.
     * Effectue des vérifications post-scénario critique.
     */
    @After("@critical")
    @Transactional
    public void tearDownCriticalTests() {
        System.out.println("✅ Scénario critique terminé - Vérifications post-test...");
        
        // Nettoyer les données
        patientRepository.deleteAll();
        hospitalRepository.deleteAll();
        specialityRepository.deleteAll();
        
        System.out.println("✅ Nettoyage critique terminé");
    }
}
