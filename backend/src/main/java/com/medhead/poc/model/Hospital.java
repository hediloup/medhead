package com.medhead.poc.model;

import jakarta.persistence.*;

/**
 * Modèle représentant un hôpital avec ses spécialités et sa géolocalisation.
 */
@Entity
@Table(name = "hospitals")
public class Hospital {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private Double latitude;
    
    @Column(nullable = false)
    private Double longitude;
    
    @Column(nullable = false)
    private String specialties;
    
    @Column(nullable = false)
    private Integer availableBeds;
    
    // Constructeurs
    public Hospital() {}
    
    public Hospital(String name, Double latitude, Double longitude, String specialties, Integer availableBeds) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.specialties = specialties;
        this.availableBeds = availableBeds;
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
    
    public String getSpecialties() {
        return specialties;
    }
    
    public void setSpecialties(String specialties) {
        this.specialties = specialties;
    }
    
    public Integer getAvailableBeds() {
        return availableBeds;
    }
    
    public void setAvailableBeds(Integer availableBeds) {
        this.availableBeds = availableBeds;
    }
    
    /**
     * Vérifie si l'hôpital dispose de la spécialité demandée.
     */
    public boolean hasSpecialty(String specialty) {
        return specialties != null && specialties.toLowerCase().contains(specialty.toLowerCase());
    }
    
    /**
     * Vérifie si l'hôpital a des lits disponibles.
     */
    public boolean hasAvailableBeds() {
        return availableBeds != null && availableBeds > 0;
    }
}
