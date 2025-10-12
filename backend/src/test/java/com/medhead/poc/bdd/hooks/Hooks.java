package com.medhead.poc.bdd.hooks;

import com.medhead.poc.repository.HospitalRepository;
import com.medhead.poc.repository.PatientRepository;
import com.medhead.poc.repository.SpecialityRepository;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Hooks executed before and after each BDD scenario.
 * They allow initialization of resources (database, servers)
 * or cleaning up state after each test.
 */
public class Hooks {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private SpecialityRepository specialityRepository;

    /**
     * Hook executed before each scenario.
     * Cleans the database and initializes base state.
     */
    @Before
    @Transactional
    public void setUp() {
        // Clean all test data
        patientRepository.deleteAll();
        hospitalRepository.deleteAll();
        specialityRepository.deleteAll();
        
        System.out.println("✅ Database cleaned for test");
    }

    /**
     * Hook executed after each scenario.
     * Performs final cleanup and post-test checks.
     */
    @After
    @Transactional
    public void tearDown() {
        // Clean all test data after each scenario
        patientRepository.deleteAll();
        hospitalRepository.deleteAll();
        specialityRepository.deleteAll();
        
        System.out.println("✅ Cleanup completed after test");
    }

    /**
     * Hook executed before each hospital allocation scenario.
     * Initializes base data for allocation tests.
     */
    @Before("@allocation")
    @Transactional
    public void setUpAllocationTests() {
        // Create base specialties
        createBaseSpecialties();
        
        System.out.println("✅ Allocation data initialized");
    }

    /**
     * Hook executed after each hospital allocation scenario.
     * Specifically cleans allocation data.
     */
    @After("@allocation")
    @Transactional
    public void tearDownAllocationTests() {
        // Clean allocation data
        patientRepository.deleteAll();
        hospitalRepository.deleteAll();
        specialityRepository.deleteAll();
    }

    /**
     * Creates base specialties needed for tests.
     */
    private void createBaseSpecialties() {
        String[] baseSpecialties = {"Cardiology", "Neurology", "Pediatrics", "Emergency", "Surgery"};
        
        for (String specialtyName : baseSpecialties) {
            if (specialityRepository.findByName(specialtyName).isEmpty()) {
                com.medhead.poc.model.Speciality specialty = new com.medhead.poc.model.Speciality();
                specialty.setName(specialtyName);
                specialityRepository.save(specialty);
            }
        }
    }

    /**
     * Hook executed before all tests (once only).
     * Initializes global BDD test environment.
     */
    @Before
    public void globalSetUp() {
        System.out.println("=== Starting BDD test execution ===");
        System.out.println("Timestamp: " + java.time.LocalDateTime.now());
    }

    /**
     * Hook executed after all tests (once only).
     * Cleans up global BDD test environment.
     */
    @After
    public void globalTearDown() {
        System.out.println("=== BDD test execution completed ===");
        System.out.println("Timestamp: " + java.time.LocalDateTime.now());
    }
}