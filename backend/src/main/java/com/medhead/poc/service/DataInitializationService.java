package com.medhead.poc.service;

import com.medhead.poc.model.Hospital;
import com.medhead.poc.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

/**
 * Service pour initialiser les données de test au démarrage de l'application.
 */
@Service
public class DataInitializationService implements CommandLineRunner {
    
    @Autowired
    private HospitalRepository hospitalRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Vérifier si des données existent déjà
        if (hospitalRepository.count() == 0) {
            initializeTestData();
        }
    }
    
    private void initializeTestData() {
        // Hôpitaux de test basés sur les données des scénarios BDD
        
        // Fred Brooks - Cardiologie (51.5074, -0.1278 - Londres)
        Hospital fredBrooks = new Hospital(
            "Hôpital Fred Brooks",
            51.5074,
            -0.1278,
            "Cardiologie,Neurologie,Chirurgie",
            2
        );
        hospitalRepository.save(fredBrooks);
        
        // Julia Crusher - Cardiologie (51.5118, -0.1313 - Londres)
        Hospital juliaCrusher = new Hospital(
            "Hôpital Julia Crusher",
            51.5118,
            -0.1313,
            "Cardiologie,Urgences",
            0 // Pas de lits disponibles
        );
        hospitalRepository.save(juliaCrusher);
        
        // Beverly Bashir - Immunologie (51.5155, -0.0922 - Londres)
        Hospital beverlyBashir = new Hospital(
            "Hôpital Beverly Bashir",
            51.5155,
            -0.0922,
            "Immunologie,Oncologie,Hématologie",
            5
        );
        hospitalRepository.save(beverlyBashir);
        
        // Hôpital supplémentaire pour plus de diversité
        Hospital saintMary = new Hospital(
            "Hôpital Saint Mary",
            51.5154,
            -0.1754,
            "Cardiologie,Urgences,Chirurgie",
            3
        );
        hospitalRepository.save(saintMary);
        
        System.out.println("Données de test initialisées avec " + hospitalRepository.count() + " hôpitaux");
    }
}
