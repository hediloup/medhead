package com.medhead.poc.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Modèle représentant la réponse à une demande d'allocation de lit.
 */
public class AllocationResponse {
    
    @JsonProperty("hospital_name")
    private String hospitalName;
    
    @JsonProperty("hospital_id")
    private Long hospitalId;
    
    @JsonProperty("distance_km")
    private Double distanceKm;
    
    @JsonProperty("specialty")
    private String specialty;
    
    @JsonProperty("available_beds")
    private Integer availableBeds;
    
    @JsonProperty("estimated_time_minutes")
    private Integer estimatedTimeMinutes;
    
    // Constructeurs
    public AllocationResponse() {}
    
    public AllocationResponse(String hospitalName, Long hospitalId, Double distanceKm, 
                            String specialty, Integer availableBeds, Integer estimatedTimeMinutes) {
        this.hospitalName = hospitalName;
        this.hospitalId = hospitalId;
        this.distanceKm = distanceKm;
        this.specialty = specialty;
        this.availableBeds = availableBeds;
        this.estimatedTimeMinutes = estimatedTimeMinutes;
    }
    
    // Getters et Setters
    public String getHospitalName() {
        return hospitalName;
    }
    
    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }
    
    public Long getHospitalId() {
        return hospitalId;
    }
    
    public void setHospitalId(Long hospitalId) {
        this.hospitalId = hospitalId;
    }
    
    public Double getDistanceKm() {
        return distanceKm;
    }
    
    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }
    
    public String getSpecialty() {
        return specialty;
    }
    
    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
    
    public Integer getAvailableBeds() {
        return availableBeds;
    }
    
    public void setAvailableBeds(Integer availableBeds) {
        this.availableBeds = availableBeds;
    }
    
    public Integer getEstimatedTimeMinutes() {
        return estimatedTimeMinutes;
    }
    
    public void setEstimatedTimeMinutes(Integer estimatedTimeMinutes) {
        this.estimatedTimeMinutes = estimatedTimeMinutes;
    }
    
    @Override
    public String toString() {
        return "AllocationResponse{" +
                "hospitalName='" + hospitalName + '\'' +
                ", hospitalId=" + hospitalId +
                ", distanceKm=" + distanceKm +
                ", specialty='" + specialty + '\'' +
                ", availableBeds=" + availableBeds +
                ", estimatedTimeMinutes=" + estimatedTimeMinutes +
                '}';
    }
}
