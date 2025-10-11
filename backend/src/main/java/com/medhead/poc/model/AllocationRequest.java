package com.medhead.poc.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Model representing a hospital bed allocation request.
 */
public class AllocationRequest {
    
    @JsonProperty("specialty")
    private String specialty;
    
    @JsonProperty("latitude")
    private Double latitude;
    
    @JsonProperty("longitude")
    private Double longitude;
    
    // Constructors
    public AllocationRequest() {}
    
    public AllocationRequest(String specialty, Double latitude, Double longitude) {
        this.specialty = specialty;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    // Getters and Setters
    public String getSpecialty() {
        return specialty;
    }
    
    public void setSpecialty(String specialty) {
        this.specialty = specialty;
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
    
    @Override
    public String toString() {
        return "AllocationRequest{" +
                "specialty='" + specialty + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
