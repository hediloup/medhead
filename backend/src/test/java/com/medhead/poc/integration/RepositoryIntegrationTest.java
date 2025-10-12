package com.medhead.poc.integration;

import com.medhead.poc.model.Hospital;
import com.medhead.poc.model.Patient;
import com.medhead.poc.model.Speciality;
import com.medhead.poc.repository.HospitalRepository;
import com.medhead.poc.repository.PatientRepository;
import com.medhead.poc.repository.SpecialityRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Tests d'intégration pour les repositories
 * Approche TDD : Test-Driven Development
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@org.springframework.test.context.ActiveProfiles("test")
public class RepositoryIntegrationTest {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private SpecialityRepository specialityRepository;

    private Speciality cardiology;
    private Speciality neurology;
    private Hospital hospital1;
    private Hospital hospital2;

    @Before
    public void setUp() {
        // Nettoyer les données de test
        patientRepository.deleteAll();
        hospitalRepository.deleteAll();
        specialityRepository.deleteAll();

        // Créer les spécialités
        cardiology = new Speciality();
        cardiology.setName("Cardiology");
        cardiology = specialityRepository.save(cardiology);

        neurology = new Speciality();
        neurology.setName("Neurology");
        neurology = specialityRepository.save(neurology);

        // Créer les hôpitaux
        hospital1 = new Hospital("Hôpital Central", 53.3976314, -2.1829641, "Manchester", "123 Main St", 5);
        Set<Speciality> specialities1 = Set.of(cardiology);
        hospital1.setSpecialities(specialities1);
        hospital1 = hospitalRepository.save(hospital1);

        hospital2 = new Hospital("Hôpital Nord", 53.4808, -2.2426, "Manchester", "456 Oak Ave", 3);
        Set<Speciality> specialities2 = Set.of(cardiology, neurology);
        hospital2.setSpecialities(specialities2);
        hospital2 = hospitalRepository.save(hospital2);
    }

    private Patient createValidPatient(String specialty, double latitude, double longitude, String anonymizedName, boolean isAnonymized) {
        Patient patient = new Patient(specialty, latitude, longitude);
        patient.setAnonymizedName(anonymizedName);
        patient.setIsAnonymized(isAnonymized);
        patient.setSeverityLevel("HIGH");
        patient.setAgeGroup("19-65");
        patient.setGender("M");
        return patient;
    }

    @Test
    public void testHospitalRepository_FindBySpecialtyAndAvailableBeds() {
        // When
        List<Hospital> cardiologyHospitals = hospitalRepository.findBySpecialtyAndAvailableBeds("Cardiology");

        // Then
        assertNotNull("La liste ne doit pas être null", cardiologyHospitals);
        assertEquals("Il doit y avoir 2 hôpitaux avec la spécialité Cardiology", 2, cardiologyHospitals.size());
        assertTrue("hospital1 doit être dans la liste", cardiologyHospitals.contains(hospital1));
        assertTrue("hospital2 doit être dans la liste", cardiologyHospitals.contains(hospital2));

        // Test avec une spécialité inexistante
        List<Hospital> neurologyHospitals = hospitalRepository.findBySpecialtyAndAvailableBeds("Pediatrics");
        assertEquals("Il ne doit y avoir aucun hôpital avec la spécialité Pediatrics", 0, neurologyHospitals.size());
    }

    @Test
    public void testHospitalRepository_FindBySpecialtyAndAvailableBeds_NoBeds() {
        // Given - Créer un hôpital sans lits disponibles
        Hospital noBedsHospital = new Hospital("Hôpital Complet", 53.3976314, -2.1829641, "Manchester", "999 Full St", 0);
        Set<Speciality> specialities = Set.of(cardiology);
        noBedsHospital.setSpecialities(specialities);
        hospitalRepository.save(noBedsHospital);

        // When
        List<Hospital> hospitalsWithBeds = hospitalRepository.findBySpecialtyAndAvailableBeds("Cardiology");

        // Then
        assertEquals("Il doit y avoir seulement 2 hôpitaux avec des lits disponibles", 2, hospitalsWithBeds.size());
        assertFalse("L'hôpital sans lits ne doit pas être dans la liste", hospitalsWithBeds.contains(noBedsHospital));
    }

    @Test
    public void testHospitalRepository_SaveAndFind() {
        // Given
        Hospital newHospital = new Hospital("Nouvel Hôpital", 53.5000, -2.3000, "Liverpool", "789 New St", 10);
        Set<Speciality> specialities = Set.of(neurology);
        newHospital.setSpecialities(specialities);

        // When
        Hospital savedHospital = hospitalRepository.save(newHospital);
        Hospital foundHospital = hospitalRepository.findById(savedHospital.getId()).orElse(null);

        // Then
        assertNotNull("L'hôpital sauvegardé ne doit pas être null", savedHospital);
        assertNotNull("L'hôpital trouvé ne doit pas être null", foundHospital);
        assertEquals("Le nom doit être correct", "Nouvel Hôpital", foundHospital.getName());
        assertEquals("La ville doit être correcte", "Liverpool", foundHospital.getCity());
        assertEquals("Le nombre de lits doit être correct", Integer.valueOf(10), foundHospital.getAvailableBeds());
        assertEquals("Il doit avoir 1 spécialité", 1, foundHospital.getSpecialities().size());
        assertTrue("Il doit avoir la spécialité Neurology", foundHospital.hasSpecialty("Neurology"));
    }

    @Test
    public void testPatientRepository_SaveAndFind() {
        // Given
        Patient patient = createValidPatient("Cardiology", 53.3976314, -2.1829641, "ANON123", true);
        patient.setAllocatedHospital(hospital1);

        // When
        Patient savedPatient = patientRepository.save(patient);
        Patient foundPatient = patientRepository.findById(savedPatient.getId()).orElse(null);

        // Then
        assertNotNull("Le patient sauvegardé ne doit pas être null", savedPatient);
        assertNotNull("Le patient trouvé ne doit pas être null", foundPatient);
        assertEquals("La spécialité doit être correcte", "Cardiology", foundPatient.getRequiredSpecialty());
        assertEquals("Le niveau de gravité doit être correct", "HIGH", foundPatient.getSeverityLevel());
        assertEquals("Le groupe d'âge doit être correct", "19-65", foundPatient.getAgeGroup());
        assertEquals("Le genre doit être correct", "M", foundPatient.getGender());
        assertNotNull("L'hôpital alloué ne doit pas être null", foundPatient.getAllocatedHospital());
        assertEquals("L'ID de l'hôpital doit être correct", hospital1.getId(), foundPatient.getAllocatedHospital().getId());
    }

    @Test
    public void testPatientRepository_FindNonAnonymizedPatients() {
        // Given - Créer des patients anonymisés et non anonymisés
        Patient anonymizedPatient = createValidPatient("Cardiology", 53.3976314, -2.1829641, "ANON001", true);
        patientRepository.save(anonymizedPatient);

        Patient nonAnonymizedPatient = createValidPatient("Neurology", 53.4808, -2.2426, "ANON002", false);
        patientRepository.save(nonAnonymizedPatient);

        // When
        List<Patient> nonAnonymizedPatients = patientRepository.findNonAnonymizedPatients();

        // Then
        assertEquals("Il doit y avoir 1 patient non anonymisé", 1, nonAnonymizedPatients.size());
        assertEquals("Le patient non anonymisé doit être correct", nonAnonymizedPatient.getId(), nonAnonymizedPatients.get(0).getId());
    }

    @Test
    public void testPatientRepository_FindPatientsWithMissingAnonymization() {
        // Given - Créer un patient avec des données manquantes (anonymizedName vide)
        Patient patientWithMissingData = createValidPatient("Cardiology", 53.3976314, -2.1829641, "", false);
        patientRepository.save(patientWithMissingData);

        Patient completePatient = createValidPatient("Neurology", 53.4808, -2.2426, "ANON004", true);
        patientRepository.save(completePatient);

        // When
        List<Patient> patientsWithMissingData = patientRepository.findPatientsWithMissingAnonymization();

        // Then
        assertEquals("Il doit y avoir 1 patient avec des données manquantes", 1, patientsWithMissingData.size());
        assertEquals("Le patient avec des données manquantes doit être correct", patientWithMissingData.getId(), patientsWithMissingData.get(0).getId());
    }

    @Test
    public void testPatientRepository_DeleteExpiredPatients() {
        // Given - Créer un patient expiré
        Patient expiredPatient = createValidPatient("Cardiology", 53.3976314, -2.1829641, "ANON004", true);
        expiredPatient.setDataRetentionUntil(LocalDateTime.now().minusDays(1)); // Expiré hier
        patientRepository.save(expiredPatient);

        Patient validPatient = createValidPatient("Neurology", 53.4808, -2.2426, "ANON005", true);
        patientRepository.save(validPatient);

        // When
        int deletedCount = patientRepository.deleteExpiredPatients(LocalDateTime.now());

        // Then
        assertEquals("Il doit y avoir 1 patient supprimé", 1, deletedCount);

        // Vérifier que seul le patient valide reste
        List<Patient> remainingPatients = patientRepository.findAll();
        assertEquals("Il doit rester 1 patient", 1, remainingPatients.size());
        assertEquals("Le patient restant doit être le patient valide", validPatient.getId(), remainingPatients.get(0).getId());
    }

    @Test
    public void testPatientRepository_CountBySpecialty() {
        // Given - Créer des patients avec différentes spécialités (non anonymisés pour être comptés)
        Patient cardiologyPatient1 = createValidPatient("Cardiology", 53.3976314, -2.1829641, "ANON006", false);
        Patient cardiologyPatient2 = createValidPatient("Cardiology", 53.4808, -2.2426, "ANON007", false);
        Patient neurologyPatient = createValidPatient("Neurology", 53.3976314, -2.1829641, "ANON008", false);

        patientRepository.save(cardiologyPatient1);
        patientRepository.save(cardiologyPatient2);
        patientRepository.save(neurologyPatient);

        // When
        List<Object[]> specialtyCounts = patientRepository.countPatientsBySpecialty();

        // Then
        assertNotNull("Les comptages ne doivent pas être null", specialtyCounts);
        assertEquals("Il doit y avoir 2 spécialités", 2, specialtyCounts.size());

        // Vérifier les comptages (ordre non garanti)
        boolean foundCardiology = false;
        boolean foundNeurology = false;

        for (Object[] count : specialtyCounts) {
            String specialty = (String) count[0];
            Long specialtyCount = (Long) count[1];

            if ("Cardiology".equals(specialty)) {
                assertEquals("Il doit y avoir 2 patients en cardiologie", Long.valueOf(2), specialtyCount);
                foundCardiology = true;
            } else if ("Neurology".equals(specialty)) {
                assertEquals("Il doit y avoir 1 patient en neurologie", Long.valueOf(1), specialtyCount);
                foundNeurology = true;
            }
        }

        assertTrue("Cardiology doit être trouvé", foundCardiology);
        assertTrue("Neurology doit être trouvé", foundNeurology);
    }

    @Test
    public void testSpecialityRepository_SaveAndFind() {
        // Given
        Speciality newSpeciality = new Speciality();
        newSpeciality.setName("Pediatrics");

        // When
        Speciality savedSpeciality = specialityRepository.save(newSpeciality);
        Speciality foundSpeciality = specialityRepository.findById(savedSpeciality.getId()).orElse(null);

        // Then
        assertNotNull("La spécialité sauvegardée ne doit pas être null", savedSpeciality);
        assertNotNull("La spécialité trouvée ne doit pas être null", foundSpeciality);
        assertEquals("Le nom doit être correct", "Pediatrics", foundSpeciality.getName());
    }

    @Test
    public void testHospitalSpecialityRelationship() {
        // Given
        Hospital hospital = hospitalRepository.findById(hospital1.getId()).orElse(null);
        assertNotNull("L'hôpital doit être trouvé", hospital);

        // When - Vérifier la relation
        Set<Speciality> specialities = hospital.getSpecialities();

        // Then
        assertNotNull("Les spécialités ne doivent pas être null", specialities);
        assertEquals("Il doit y avoir 1 spécialité", 1, specialities.size());
        assertTrue("L'hôpital doit avoir la spécialité Cardiology", hospital.hasSpecialty("Cardiology"));
        assertFalse("L'hôpital ne doit pas avoir la spécialité Neurology", hospital.hasSpecialty("Neurology"));
    }
}
