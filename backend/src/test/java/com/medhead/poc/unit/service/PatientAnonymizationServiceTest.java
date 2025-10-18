package com.medhead.poc.unit.service;

import com.medhead.poc.model.Patient;
import com.medhead.poc.repository.PatientRepository;
import com.medhead.poc.service.PatientAnonymizationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour PatientAnonymizationService
 * Approche TDD : Test-Driven Development
 */
@RunWith(MockitoJUnitRunner.class)
public class PatientAnonymizationServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientAnonymizationService patientAnonymizationService;

    private Patient testPatient;

    @Before
    public void setUp() {
        testPatient = new Patient("Cardiology", 53.3976314, -2.1829641);
        testPatient.setId(1L);
        testPatient.setSeverityLevel("MEDIUM");
    }

    @Test
    public void testAnonymizePatient_Success() {
        // Given
        System.out.println("🔍 Test: Service Anonymisation - Anonymisation réussie");
        System.out.println("   Patient initial: " + testPatient.getRequiredSpecialty() + " - " + testPatient.getSeverityLevel());
        when(patientRepository.save(any(Patient.class))).thenReturn(testPatient);

        // When
        System.out.println("   Exécution de l'anonymisation");
        Patient result = patientAnonymizationService.anonymizePatient(testPatient);

        // Then
        System.out.println("   Vérification de l'anonymisation");
        assertNotNull("Le résultat ne doit pas être null", result);
        assertTrue("Le patient doit être marqué comme anonymisé", result.getIsAnonymized());
        assertNotNull("Le nom anonymisé doit être généré", result.getAnonymizedName());
        assertTrue("Le nom anonymisé doit commencer par PATIENT_", result.getAnonymizedName().startsWith("PATIENT_"));
        System.out.println("   Nom anonymisé généré: " + result.getAnonymizedName());
        System.out.println("   ✅ Test Anonymisation réussi - Patient anonymisé correctement");

        verify(patientRepository).save(testPatient);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAnonymizePatient_NullPatient() {
        // When
        patientAnonymizationService.anonymizePatient(null);

        // Then - Exception attendue
    }

    @Test
    public void testCreateAnonymizedPatient_Success() {
        // Given
        System.out.println("🔍 Test: Service Anonymisation - Création de patient anonymisé");
        String specialty = "Cardiology";
        Double latitude = 53.3976314;
        Double longitude = -2.1829641;
        String severityLevel = "HIGH";
        System.out.println("   Paramètres: " + specialty + " à " + latitude + ", " + longitude + " - " + severityLevel);

        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient patient = invocation.getArgument(0);
            patient.setIsAnonymized(true); // S'assurer que le patient est marqué comme anonymisé
            return patient;
        });

        // When
        System.out.println("   Création du patient anonymisé");
        Patient result = patientAnonymizationService.createAnonymizedPatient(specialty, latitude, longitude, severityLevel);

        // Then
        System.out.println("   Vérification du patient créé");
        assertNotNull("Le résultat ne doit pas être null", result);
        assertTrue("Le patient doit être marqué comme anonymisé", result.getIsAnonymized());
        assertEquals("La spécialité doit être correcte", specialty, result.getRequiredSpecialty());
        assertEquals("La latitude doit être correcte", latitude, result.getLatitude());
        assertEquals("La longitude doit être correcte", longitude, result.getLongitude());
        assertEquals("Le niveau de gravité doit être correct", severityLevel, result.getSeverityLevel());
        System.out.println("   ✅ Test Anonymisation réussi - Patient anonymisé créé correctement");

        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    public void testCreateAnonymizedPatient_DefaultSeverity() {
        // Given
        String specialty = "Cardiology";
        Double latitude = 53.3976314;
        Double longitude = -2.1829641;

        when(patientRepository.save(any(Patient.class))).thenReturn(testPatient);

        // When
        Patient result = patientAnonymizationService.createAnonymizedPatient(specialty, latitude, longitude, null);

        // Then
        assertNotNull("Le résultat ne doit pas être null", result);
        assertEquals("Le niveau de gravité par défaut doit être MEDIUM", "MEDIUM", result.getSeverityLevel());

        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    public void testAnonymizeAllNonAnonymizedPatients() {
        // Given
        Patient patient1 = new Patient("Cardiology", 53.3976314, -2.1829641);
        patient1.setIsAnonymized(false);
        
        Patient patient2 = new Patient("Neurology", 53.4808, -2.2426);
        patient2.setIsAnonymized(false);

        List<Patient> nonAnonymizedPatients = Arrays.asList(patient1, patient2);

        when(patientRepository.findNonAnonymizedPatients()).thenReturn(nonAnonymizedPatients);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient1, patient2);

        // When
        int count = patientAnonymizationService.anonymizeAllNonAnonymizedPatients();

        // Then
        assertEquals("Le nombre de patients anonymisés doit être correct", 2, count);
        verify(patientRepository).findNonAnonymizedPatients();
        verify(patientRepository, times(2)).save(any(Patient.class));
    }

    @Test
    public void testDeleteExpiredPatients() {
        // Given
        int expectedDeletedCount = 5;

        when(patientRepository.deleteExpiredPatients(any(LocalDateTime.class))).thenReturn(expectedDeletedCount);

        // When
        int deletedCount = patientAnonymizationService.deleteExpiredPatients();

        // Then
        assertEquals("Le nombre de patients supprimés doit être correct", expectedDeletedCount, deletedCount);
        verify(patientRepository).deleteExpiredPatients(any(LocalDateTime.class));
    }

    @Test
    public void testAnonymizePatientsWithMissingData() {
        // Given
        Patient patient1 = new Patient("Cardiology", 53.3976314, -2.1829641);
        patient1.setAnonymizedName(null); // Données manquantes

        List<Patient> patientsToAnonymize = Arrays.asList(patient1);

        when(patientRepository.findPatientsWithMissingAnonymization()).thenReturn(patientsToAnonymize);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient1);

        // When
        int count = patientAnonymizationService.anonymizePatientsWithMissingData();

        // Then
        assertEquals("Le nombre de patients anonymisés doit être correct", 1, count);
        verify(patientRepository).findPatientsWithMissingAnonymization();
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    public void testGenerateAnonymousId() {
        // When
        String anonymousId = patientAnonymizationService.generateAnonymousId();

        // Then
        assertNotNull("L'ID anonyme ne doit pas être null", anonymousId);
        assertTrue("L'ID doit commencer par PATIENT_", anonymousId.startsWith("PATIENT_"));
        assertEquals("L'ID doit avoir la bonne longueur", 16, anonymousId.length()); // "PATIENT_" + 8 caractères
    }

    @Test
    public void testCanAccessPatient_ValidPatient() {
        // Given
        Patient validPatient = new Patient("Cardiology", 53.3976314, -2.1829641);
        validPatient.setIsAnonymized(true);
        // Le patient n'est pas expiré par défaut (créé il y a moins de 7 ans)

        // When
        boolean canAccess = patientAnonymizationService.canAccessPatient(validPatient);

        // Then
        assertTrue("L'accès doit être autorisé pour un patient valide", canAccess);
    }

    @Test
    public void testCanAccessPatient_NullPatient() {
        // When
        boolean canAccess = patientAnonymizationService.canAccessPatient(null);

        // Then
        assertFalse("L'accès doit être refusé pour un patient null", canAccess);
    }

    @Test
    public void testCanAccessPatient_NotAnonymized() {
        // Given
        Patient notAnonymizedPatient = new Patient("Cardiology", 53.3976314, -2.1829641);
        notAnonymizedPatient.setIsAnonymized(false);

        // When
        boolean canAccess = patientAnonymizationService.canAccessPatient(notAnonymizedPatient);

        // Then
        assertFalse("L'accès doit être refusé pour un patient non anonymisé", canAccess);
    }

    @Test
    public void testGetAnonymizedStatistics() {
        // Given
        List<Object[]> specialtyCounts = Arrays.asList(
                new Object[]{"Cardiology", 10L},
                new Object[]{"Neurology", 5L}
        );
        
        List<Object[]> ageGroupCounts = Arrays.asList(
                new Object[]{"0-18", 3L},
                new Object[]{"19-65", 8L},
                new Object[]{"65+", 4L}
        );

        when(patientRepository.countPatientsBySpecialty()).thenReturn(specialtyCounts);
        when(patientRepository.countPatientsByAgeGroup()).thenReturn(ageGroupCounts);

        // When
        PatientAnonymizationService.PatientStatistics stats = patientAnonymizationService.getAnonymizedStatistics();

        // Then
        assertNotNull("Les statistiques ne doivent pas être null", stats);
        assertEquals("Les comptages de spécialité doivent être corrects", specialtyCounts, stats.getSpecialtyCounts());
        assertEquals("Les comptages de groupe d'âge doivent être corrects", ageGroupCounts, stats.getAgeGroupCounts());
        assertNotNull("La date de génération doit être définie", stats.getGeneratedAt());

        verify(patientRepository).countPatientsBySpecialty();
        verify(patientRepository).countPatientsByAgeGroup();
    }
}
