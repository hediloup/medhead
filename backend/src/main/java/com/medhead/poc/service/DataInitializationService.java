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
 * Service pour initialiser les données de test au démarrage de l'application.
 * Ce service ne s'exécute que pour le profil "dev" (H2).
 * Pour le profil "prod" (PostgreSQL), les données sont initialisées via les scripts SQL.
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
        // Vérifier si des données existent déjà
        if (hospitalRepository.count() == 0) {
            initializeTestData();
        }
    }
    
    private void initializeTestData() {
        // Créer les spécialités de test
        Speciality cardiology = new Speciality("Cardiology", "Cardiologie et maladies cardiovasculaires");
        Speciality neurology = new Speciality("Neurology", "Neurologie");
        Speciality surgery = new Speciality("General Surgery", "Chirurgie générale");
        Speciality emergency = new Speciality("Emergency Medicine", "Médecine d'urgence");
        Speciality immunology = new Speciality("Immunology", "Immunologie");
        Speciality oncology = new Speciality("Oncology", "Oncologie");
        Speciality haematology = new Speciality("Haematology", "Hématologie");
        
        specialityRepository.save(cardiology);
        specialityRepository.save(neurology);
        specialityRepository.save(surgery);
        specialityRepository.save(emergency);
        specialityRepository.save(immunology);
        specialityRepository.save(oncology);
        specialityRepository.save(haematology);
        
        // Hôpitaux de test basés sur les données des scénarios BDD
        
        // Fred Brooks - Cardiologie (51.5074, -0.1278 - Londres)
        Hospital fredBrooks = new Hospital(
            "Hôpital Fred Brooks",
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
        
        // Julia Crusher - Cardiologie (51.5118, -0.1313 - Londres)
        Hospital juliaCrusher = new Hospital(
            "Hôpital Julia Crusher",
            51.5118,
            -0.1313,
            "Londres",
            "456 Test Avenue, London",
            0 // Pas de lits disponibles
        );
        juliaCrusher.addSpeciality(cardiology);
        juliaCrusher.addSpeciality(emergency);
        hospitalRepository.save(juliaCrusher);
        
        // Beverly Bashir - Immunologie (51.5155, -0.0922 - Londres)
        Hospital beverlyBashir = new Hospital(
            "Hôpital Beverly Bashir",
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
        
        // Hôpital supplémentaire pour plus de diversité
        Hospital saintMary = new Hospital(
            "Hôpital Saint Mary",
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
        
        System.out.println("Données de test initialisées avec " + hospitalRepository.count() + " hôpitaux et " + specialityRepository.count() + " spécialités");
    }
}
