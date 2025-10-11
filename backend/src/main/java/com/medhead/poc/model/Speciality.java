package com.medhead.poc.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Modèle représentant une spécialité médicale.
 */
@Entity
@Table(name = "specialities")
public class Speciality {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @ManyToMany(mappedBy = "specialities", fetch = FetchType.LAZY)
    private Set<Hospital> hospitals = new HashSet<>();
    
    // Constructeurs
    public Speciality() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Speciality(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Set<Hospital> getHospitals() {
        return hospitals;
    }
    
    public void setHospitals(Set<Hospital> hospitals) {
        this.hospitals = hospitals;
    }
    
    // Méthodes utilitaires
    public void addHospital(Hospital hospital) {
        hospitals.add(hospital);
        hospital.getSpecialities().add(this);
    }
    
    public void removeHospital(Hospital hospital) {
        hospitals.remove(hospital);
        hospital.getSpecialities().remove(this);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Speciality)) return false;
        Speciality that = (Speciality) o;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "Speciality{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
