package com.medhead.poc.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Modèle représentant un patient avec protection RGPD.
 * Les données sensibles sont anonymisées et chiffrées.
 */
@Entity
@Table(name = "patients")
public class Patient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "patient_uuid", nullable = false, unique = true)
    private String patientUuid;
    
    // Données anonymisées
    @Column(name = "anonymized_name", nullable = false)
    private String anonymizedName;
    
    @Column(name = "age_group")
    private String ageGroup; // "0-18", "19-65", "65+"
    
    @Column(name = "gender")
    private String gender; // "M", "F", "O" (Other)
    
    @Column(name = "postal_code")
    private String postalCode; // Code postal (moins sensible que l'adresse complète)
    
    // Données médicales nécessaires
    @Column(name = "required_specialty", nullable = false)
    private String requiredSpecialty;
    
    @Column(name = "severity_level")
    private String severityLevel; // "LOW", "MEDIUM", "HIGH", "CRITICAL"
    
    @Column(name = "latitude")
    private Double latitude;
    
    @Column(name = "longitude")
    private Double longitude;
    
    // Métadonnées de sécurité
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "data_retention_until")
    private LocalDateTime dataRetentionUntil;
    
    @Column(name = "is_anonymized", nullable = false)
    private Boolean isAnonymized = false;
    
    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allocated_hospital_id")
    private Hospital allocatedHospital;
    
    // Constructeurs
    public Patient() {
        this.patientUuid = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        // Rétention des données : 7 ans après la dernière interaction
        this.dataRetentionUntil = this.createdAt.plusYears(7);
    }
    
    public Patient(String requiredSpecialty, Double latitude, Double longitude) {
        this();
        this.requiredSpecialty = requiredSpecialty;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getPatientUuid() {
        return patientUuid;
    }
    
    public void setPatientUuid(String patientUuid) {
        this.patientUuid = patientUuid;
    }
    
    public String getAnonymizedName() {
        return anonymizedName;
    }
    
    public void setAnonymizedName(String anonymizedName) {
        this.anonymizedName = anonymizedName;
    }
    
    public String getAgeGroup() {
        return ageGroup;
    }
    
    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getPostalCode() {
        return postalCode;
    }
    
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    public String getRequiredSpecialty() {
        return requiredSpecialty;
    }
    
    public void setRequiredSpecialty(String requiredSpecialty) {
        this.requiredSpecialty = requiredSpecialty;
    }
    
    public String getSeverityLevel() {
        return severityLevel;
    }
    
    public void setSeverityLevel(String severityLevel) {
        this.severityLevel = severityLevel;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getDataRetentionUntil() {
        return dataRetentionUntil;
    }
    
    public void setDataRetentionUntil(LocalDateTime dataRetentionUntil) {
        this.dataRetentionUntil = dataRetentionUntil;
    }
    
    public Boolean getIsAnonymized() {
        return isAnonymized;
    }
    
    public void setIsAnonymized(Boolean isAnonymized) {
        this.isAnonymized = isAnonymized;
    }
    
    public Hospital getAllocatedHospital() {
        return allocatedHospital;
    }
    
    public void setAllocatedHospital(Hospital allocatedHospital) {
        this.allocatedHospital = allocatedHospital;
    }
    
    // Méthodes utilitaires
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Vérifie si les données du patient ont expiré selon la politique de rétention
     */
    public boolean isDataExpired() {
        return LocalDateTime.now().isAfter(this.dataRetentionUntil);
    }
    
    /**
     * Génère un nom anonymisé basé sur l'UUID
     */
    public void generateAnonymizedName() {
        if (this.anonymizedName == null) {
            this.anonymizedName = "PATIENT_" + this.patientUuid.substring(0, 8).toUpperCase();
        }
    }
    
    /**
     * Détermine le groupe d'âge basé sur la date de naissance
     */
    public void setAgeGroupFromBirthDate(LocalDateTime birthDate) {
        if (birthDate != null) {
            int age = LocalDateTime.now().getYear() - birthDate.getYear();
            if (age <= 18) {
                this.ageGroup = "0-18";
            } else if (age <= 65) {
                this.ageGroup = "19-65";
            } else {
                this.ageGroup = "65+";
            }
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient)) return false;
        Patient patient = (Patient) o;
        return id != null && id.equals(patient.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", patientUuid='" + patientUuid + '\'' +
                ", anonymizedName='" + anonymizedName + '\'' +
                ", ageGroup='" + ageGroup + '\'' +
                ", requiredSpecialty='" + requiredSpecialty + '\'' +
                ", severityLevel='" + severityLevel + '\'' +
                ", isAnonymized=" + isAnonymized +
                ", createdAt=" + createdAt +
                '}';
    }
}
