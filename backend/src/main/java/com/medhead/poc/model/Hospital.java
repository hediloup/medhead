package com.medhead.poc.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Model representing a hospital with its specialties and geolocation.
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
    private String city;
    
    @Column(columnDefinition = "TEXT")
    private String address;
    
    @Column(nullable = false)
    private Integer availableBeds;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "hospital_specialities",
        joinColumns = @JoinColumn(name = "hospital_id"),
        inverseJoinColumns = @JoinColumn(name = "speciality_id")
    )
    private Set<Speciality> specialities = new HashSet<>();
    
    // Constructors
    public Hospital() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Hospital(String name, Double latitude, Double longitude, String city, String address, Integer availableBeds) {
        this();
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.address = address;
        this.availableBeds = availableBeds;
    }
    
    // Getters and Setters
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
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public Integer getAvailableBeds() {
        return availableBeds;
    }
    
    public void setAvailableBeds(Integer availableBeds) {
        this.availableBeds = availableBeds;
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
    
    public Set<Speciality> getSpecialities() {
        return specialities;
    }
    
    public void setSpecialities(Set<Speciality> specialities) {
        this.specialities = specialities;
    }
    
    // Utility methods
    public void addSpeciality(Speciality speciality) {
        specialities.add(speciality);
        speciality.getHospitals().add(this);
    }
    
    public void removeSpeciality(Speciality speciality) {
        specialities.remove(speciality);
        speciality.getHospitals().remove(this);
    }
    
    /**
     * Checks if the hospital has the requested specialty.
     */
    public boolean hasSpecialty(String specialtyName) {
        return specialities.stream()
                .anyMatch(speciality -> speciality.getName().equalsIgnoreCase(specialtyName));
    }
    
    /**
     * Checks if the hospital has available beds.
     */
    public boolean hasAvailableBeds() {
        return availableBeds != null && availableBeds > 0;
    }
    
    /**
     * Automatically updates the updated_at timestamp
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hospital)) return false;
        Hospital hospital = (Hospital) o;
        return id != null && id.equals(hospital.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "Hospital{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", city='" + city + '\'' +
                ", availableBeds=" + availableBeds +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
