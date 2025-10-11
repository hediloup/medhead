package com.medhead.poc.service;

import com.medhead.poc.model.Hospital;
import com.medhead.poc.model.Speciality;
import com.medhead.poc.repository.HospitalRepository;
import com.medhead.poc.repository.SpecialityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Service to initialize test data at application startup.
 * This service only runs for the "dev" profile (H2).
 * For the "prod" profile (PostgreSQL), data is initialized via SQL scripts.
 */
@Service
@Profile("dev")
public class DataInitializationService implements CommandLineRunner {
    
    @Autowired
    private HospitalRepository hospitalRepository;
    
    @Autowired
    private SpecialityRepository specialityRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (hospitalRepository.count() == 0) {
            initializeTestData();
        }
    }
    
    private void initializeTestData() {
        // Create test specialties
        Speciality cardiology = new Speciality("Cardiology", "Cardiology and cardiovascular diseases");
        Speciality neurology = new Speciality("Neurology", "Neurology");
        Speciality surgery = new Speciality("General Surgery", "General surgery");
        Speciality emergency = new Speciality("Emergency Medicine", "Emergency medicine");
        Speciality immunology = new Speciality("Immunology", "Immunology");
        Speciality oncology = new Speciality("Oncology", "Oncology");
        Speciality haematology = new Speciality("Haematology", "Haematology");
        
        specialityRepository.save(cardiology);
        specialityRepository.save(neurology);
        specialityRepository.save(surgery);
        specialityRepository.save(emergency);
        specialityRepository.save(immunology);
        specialityRepository.save(oncology);
        specialityRepository.save(haematology);
        
        // Test hospitals based on BDD scenario data
        
        // Fred Brooks - Cardiology (51.5074, -0.1278 - London)
        Hospital fredBrooks = new Hospital(
            "Fred Brooks Hospital",
            51.5074,
            -0.1278,
            "Londres",
            "123 Test Street, London",
            2
        );
        fredBrooks.addSpeciality(cardiology);
        fredBrooks.addSpeciality(neurology);
        fredBrooks.addSpeciality(surgery);
        hospitalRepository.save(fredBrooks);
        
        // Julia Crusher - Cardiology (51.5118, -0.1313 - London)
        Hospital juliaCrusher = new Hospital(
            "Julia Crusher Hospital",
            51.5118,
            -0.1313,
            "Londres",
            "456 Test Avenue, London",
            0 // No available beds
        );
        juliaCrusher.addSpeciality(cardiology);
        juliaCrusher.addSpeciality(emergency);
        hospitalRepository.save(juliaCrusher);
        
        // Beverly Bashir - Immunology (51.5155, -0.0922 - London)
        Hospital beverlyBashir = new Hospital(
            "Beverly Bashir Hospital",
            51.5155,
            -0.0922,
            "Londres",
            "789 Test Road, London",
            5
        );
        beverlyBashir.addSpeciality(immunology);
        beverlyBashir.addSpeciality(oncology);
        beverlyBashir.addSpeciality(haematology);
        hospitalRepository.save(beverlyBashir);
        
        // Additional hospital for more diversity
        Hospital saintMary = new Hospital(
            "Saint Mary Hospital",
            51.5154,
            -0.1754,
            "Londres",
            "321 Test Lane, London",
            3
        );
        saintMary.addSpeciality(cardiology);
        saintMary.addSpeciality(emergency);
        saintMary.addSpeciality(surgery);
        hospitalRepository.save(saintMary);
        
        System.out.println("Test data initialized with " + hospitalRepository.count() + " hospitals and " + specialityRepository.count() + " specialties");
    }
}
