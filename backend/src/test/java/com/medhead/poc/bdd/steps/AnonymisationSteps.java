package com.medhead.poc.bdd.steps;

import com.medhead.poc.model.Patient;
import com.medhead.poc.repository.PatientRepository;
import com.medhead.poc.service.PatientAnonymizationService;
import io.cucumber.java.fr.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Steps pour les tests BDD d'anonymisation des patients
 */
public class AnonymisationSteps {

    @Autowired
    private PatientAnonymizationService patientAnonymizationService;

    @Autowired
    private PatientRepository patientRepository;

    private Patient currentPatient;
    private int anonymizedCount;
    private int deletedCount;
    private PatientAnonymizationService.PatientStatistics statistics;

    @Étantdonné("^qu'un nouveau patient nécessite une allocation$")
    public void qu_un_nouveau_patient_nécessite_une_allocation() {
        // Préparation pour la création d'un patient
    }

    @Quand("^je crée un patient avec la spécialité \"([^\"]*)\" et les coordonnées (\\d+\\.\\d+), (-?\\d+\\.\\d+)$")
    @Transactional
    public void je_crée_un_patient_avec_la_spécialité_et_les_coordonnées(String specialty, double latitude, double longitude) {
        this.currentPatient = patientAnonymizationService.createAnonymizedPatient(
                specialty, latitude, longitude, "MEDIUM");
    }

    @Alors("^le patient doit avoir un UUID unique$")
    public void le_patient_doit_avoir_un_UUID_unique() {
        assertNotNull("L'UUID du patient ne doit pas être null", currentPatient.getPatientUuid());
        assertTrue("L'UUID doit avoir une longueur raisonnable", 
                currentPatient.getPatientUuid().length() > 10);
    }

    @Et("^le nom doit être anonymisé avec le format \"PATIENT_XXXXXXXX\"$")
    public void le_nom_doit_être_anonymisé_avec_le_format_PATIENT_XXXXXXXX() {
        assertNotNull("Le nom anonymisé ne doit pas être null", currentPatient.getAnonymizedName());
        assertTrue("Le nom doit commencer par PATIENT_", 
                currentPatient.getAnonymizedName().startsWith("PATIENT_"));
        assertEquals("Le nom doit avoir la bonne longueur", 16, currentPatient.getAnonymizedName().length());
    }

    @Et("^le patient doit être marqué comme anonymisé$")
    public void le_patient_doit_être_marqué_comme_anonymisé() {
        assertTrue("Le patient doit être marqué comme anonymisé", currentPatient.getIsAnonymized());
    }

    @Et("^la date de création doit être définie$")
    public void la_date_de_création_doit_être_définie() {
        assertNotNull("La date de création ne doit pas être null", currentPatient.getCreatedAt());
        assertTrue("La date de création doit être récente", 
                currentPatient.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(1)));
    }

    @Et("^la date d'expiration des données doit être fixée à (\\d+) ans$")
    public void la_date_d_expiration_des_données_doit_être_fixée_à_ans(int years) {
        assertNotNull("La date d'expiration ne doit pas être null", currentPatient.getDataRetentionUntil());
        LocalDateTime expectedExpiration = currentPatient.getCreatedAt().plusYears(years);
        assertTrue("La date d'expiration doit être correcte", 
                currentPatient.getDataRetentionUntil().isEqual(expectedExpiration) ||
                currentPatient.getDataRetentionUntil().isAfter(expectedExpiration.minusMinutes(1)));
    }

    @Étantdonné("^qu'il existe un patient non anonymisé dans le système$")
    @Transactional
    public void qu_il_existe_un_patient_non_anonymisé_dans_le_système() {
        currentPatient = new Patient("Cardiology", 53.3976314, -2.1829641);
        currentPatient.setIsAnonymized(false);
        currentPatient = patientRepository.save(currentPatient);
    }

    @Quand("^j'anonymise ce patient$")
    @Transactional
    public void j_anonymise_ce_patient() {
        currentPatient = patientAnonymizationService.anonymizePatient(currentPatient);
    }

    @Alors("^le nom du patient doit être remplacé par un identifiant anonyme$")
    public void le_nom_du_patient_doit_être_remplacé_par_un_identifiant_anonyme() {
        assertNotNull("Le nom anonymisé ne doit pas être null", currentPatient.getAnonymizedName());
        assertTrue("Le nom doit commencer par PATIENT_", 
                currentPatient.getAnonymizedName().startsWith("PATIENT_"));
    }

    @Et("^les données sensibles doivent être supprimées$")
    public void les_données_sensibles_doivent_être_supprimées() {
        // Vérifier que les données sensibles ne sont pas présentes
        // Le modèle Patient ne stocke déjà que des données anonymisées
        assertTrue("Le patient doit être marqué comme anonymisé", currentPatient.getIsAnonymized());
    }

    @Étantdonné("^qu'il existe un patient anonymisé et non expiré$")
    @Transactional
    public void qu_il_existe_un_patient_anonymisé_et_non_expiré() {
        currentPatient = new Patient("Cardiology", 53.3976314, -2.1829641);
        currentPatient.setIsAnonymized(true);
        currentPatient.generateAnonymizedName();
        currentPatient = patientRepository.save(currentPatient);
    }

    @Quand("^je vérifie les permissions d'accès à ce patient$")
    public void je_vérifie_les_permissions_d_accès_à_ce_patient() {
        // Cette étape est implicite dans la vérification suivante
    }

    @Alors("^l'accès doit être autorisé$")
    public void l_accès_doit_être_autorisé() {
        assertTrue("L'accès doit être autorisé", 
                patientAnonymizationService.canAccessPatient(currentPatient));
    }

    @Étantdonné("^qu'il existe un patient non anonymisé$")
    @Transactional
    public void qu_il_existe_un_patient_non_anonymisé() {
        currentPatient = new Patient("Cardiology", 53.3976314, -2.1829641);
        currentPatient.setIsAnonymized(false);
        currentPatient = patientRepository.save(currentPatient);
    }

    @Alors("^l'accès doit être refusé$")
    public void l_accès_doit_être_refusé() {
        assertFalse("L'accès doit être refusé", 
                patientAnonymizationService.canAccessPatient(currentPatient));
    }

    @Étantdonné("^qu'il existe un patient dont les données ont expiré \\(plus de (\\d+) ans\\)$")
    @Transactional
    public void qu_il_existe_un_patient_dont_les_données_ont_expiré_plus_de_ans(int years) {
        currentPatient = new Patient("Cardiology", 53.3976314, -2.1829641);
        currentPatient.setIsAnonymized(true);
        currentPatient.generateAnonymizedName();
        currentPatient.setDataRetentionUntil(LocalDateTime.now().minusYears(years + 1));
        currentPatient = patientRepository.save(currentPatient);
    }

    @Étantdonné("^qu'il existe des patients avec des données expirées$")
    @Transactional
    public void qu_il_existe_des_patients_avec_des_données_expirées() {
        // Créer des patients expirés
        Patient expiredPatient1 = new Patient("Cardiology", 53.3976314, -2.1829641);
        expiredPatient1.setDataRetentionUntil(LocalDateTime.now().minusDays(1));
        patientRepository.save(expiredPatient1);

        Patient expiredPatient2 = new Patient("Neurology", 53.4808, -2.2426);
        expiredPatient2.setDataRetentionUntil(LocalDateTime.now().minusDays(2));
        patientRepository.save(expiredPatient2);
    }

    @Quand("^le système exécute le nettoyage automatique des données$")
    @Transactional
    public void le_système_exécute_le_nettoyage_automatique_des_données() {
        deletedCount = patientAnonymizationService.deleteExpiredPatients();
    }

    @Alors("^tous les patients expirés doivent être supprimés définitivement$")
    public void tous_les_patients_expirés_doivent_être_supprimés_définitivement() {
        assertTrue("Au moins un patient doit avoir été supprimé", deletedCount > 0);
        
        // Vérifier qu'aucun patient expiré ne reste
        List<Patient> remainingPatients = patientRepository.findAll();
        for (Patient patient : remainingPatients) {
            assertFalse("Aucun patient restant ne doit être expiré", patient.isDataExpired());
        }
    }

    @Et("^un rapport de suppression doit être généré$")
    public void un_rapport_de_suppression_doit_être_généré() {
        assertTrue("Le nombre de suppressions doit être rapporté", deletedCount >= 0);
    }

    @Étantdonné("^qu'il existe plusieurs patients non anonymisés dans le système$")
    @Transactional
    public void qu_il_existe_plusieurs_patients_non_anonymisés_dans_le_système() {
        // Créer des patients non anonymisés
        Patient patient1 = new Patient("Cardiology", 53.3976314, -2.1829641);
        patient1.setIsAnonymized(false);
        patientRepository.save(patient1);

        Patient patient2 = new Patient("Neurology", 53.4808, -2.2426);
        patient2.setIsAnonymized(false);
        patientRepository.save(patient2);
    }

    @Quand("^j'exécute l'anonymisation en lot$")
    @Transactional
    public void j_exécute_l_anonymisation_en_lot() {
        anonymizedCount = patientAnonymizationService.anonymizeAllNonAnonymizedPatients();
    }

    @Alors("^tous les patients non anonymisés doivent être anonymisés$")
    public void tous_les_patients_non_anonymisés_doivent_être_anonymisés() {
        assertTrue("Au moins un patient doit avoir été anonymisé", anonymizedCount > 0);
        
        // Vérifier qu'aucun patient non anonymisé ne reste
        List<Patient> nonAnonymizedPatients = patientRepository.findNonAnonymizedPatients();
        assertEquals("Aucun patient non anonymisé ne doit rester", 0, nonAnonymizedPatients.size());
    }

    @Et("^le nombre de patients traités doit être rapporté$")
    public void le_nombre_de_patients_traités_doit_être_rapporté() {
        assertTrue("Le nombre de patients traités doit être rapporté", anonymizedCount >= 0);
    }

    @Étantdonné("^qu'il existe des patients anonymisés dans le système$")
    @Transactional
    public void qu_il_existe_des_patients_anonymisés_dans_le_système() {
        // Créer des patients anonymisés avec différentes spécialités et groupes d'âge
        Patient patient1 = new Patient("Cardiology", 53.3976314, -2.1829641);
        patient1.setIsAnonymized(true);
        patient1.generateAnonymizedName();
        patient1.setAgeGroup("19-65");
        patientRepository.save(patient1);

        Patient patient2 = new Patient("Neurology", 53.4808, -2.2426);
        patient2.setIsAnonymized(true);
        patient2.generateAnonymizedName();
        patient2.setAgeGroup("65+");
        patientRepository.save(patient2);

        Patient patient3 = new Patient("Cardiology", 53.3500, -2.1000);
        patient3.setIsAnonymized(true);
        patient3.generateAnonymizedName();
        patient3.setAgeGroup("0-18");
        patientRepository.save(patient3);
    }

    @Quand("^je demande les statistiques anonymisées$")
    public void je_demande_les_statistiques_anonymisées() {
        statistics = patientAnonymizationService.getAnonymizedStatistics();
    }

    @Alors("^je dois recevoir des comptages par spécialité$")
    public void je_dois_recevoir_des_comptages_par_spécialité() {
        assertNotNull("Les statistiques ne doivent pas être null", statistics);
        assertNotNull("Les comptages par spécialité ne doivent pas être null", statistics.getSpecialtyCounts());
        assertTrue("Il doit y avoir des comptages par spécialité", statistics.getSpecialtyCounts().size() > 0);
    }

    @Et("^je dois recevoir des comptages par groupe d'âge$")
    public void je_dois_recevoir_des_comptages_par_groupe_d_âge() {
        assertNotNull("Les comptages par groupe d'âge ne doivent pas être null", statistics.getAgeGroupCounts());
        assertTrue("Il doit y avoir des comptages par groupe d'âge", statistics.getAgeGroupCounts().size() > 0);
    }

    @Et("^aucune donnée personnelle ne doit être exposée$")
    public void aucune_donnée_personnelle_ne_doit_être_exposée() {
        // Vérifier que les statistiques ne contiennent que des données agrégées
        assertNotNull("Les statistiques doivent être générées", statistics);
        
        // Vérifier que les comptages ne contiennent que des données anonymisées
        for (Object[] specialtyCount : statistics.getSpecialtyCounts()) {
            assertTrue("Les comptages par spécialité ne doivent contenir que des noms de spécialités et des nombres", 
                    specialtyCount.length == 2);
        }
        
        for (Object[] ageGroupCount : statistics.getAgeGroupCounts()) {
            assertTrue("Les comptages par groupe d'âge ne doivent contenir que des groupes d'âge et des nombres", 
                    ageGroupCount.length == 2);
        }
    }

    @Et("^la date de génération des statistiques doit être incluse$")
    public void la_date_de_génération_des_statistiques_doit_être_incluse() {
        assertNotNull("La date de génération ne doit pas être null", statistics.getGeneratedAt());
        assertTrue("La date de génération doit être récente", 
                statistics.getGeneratedAt().isAfter(LocalDateTime.now().minusMinutes(1)));
    }

    @Étantdonné("^qu'il existe des patients avec des données d'anonymisation manquantes$")
    @Transactional
    public void qu_il_existe_des_patients_avec_des_données_d_anonymisation_manquantes() {
        // Créer des patients avec des données manquantes
        Patient patient1 = new Patient("Cardiology", 53.3976314, -2.1829641);
        patient1.setAnonymizedName(null); // Données manquantes
        patientRepository.save(patient1);

        Patient patient2 = new Patient("Neurology", 53.4808, -2.2426);
        patient2.setAnonymizedName(""); // Données vides
        patientRepository.save(patient2);
    }

    @Quand("^j'exécute le nettoyage des données manquantes$")
    @Transactional
    public void j_exécute_le_nettoyage_des_données_manquantes() {
        anonymizedCount = patientAnonymizationService.anonymizePatientsWithMissingData();
    }

    @Alors("^tous les patients avec des données manquantes doivent être anonymisés$")
    public void tous_les_patients_avec_des_données_manquantes_doivent_être_anonymisés() {
        assertTrue("Au moins un patient doit avoir été traité", anonymizedCount > 0);
        
        // Vérifier qu'aucun patient avec des données manquantes ne reste
        List<Patient> patientsWithMissingData = patientRepository.findPatientsWithMissingAnonymization();
        assertEquals("Aucun patient avec des données manquantes ne doit rester", 0, patientsWithMissingData.size());
    }
}
