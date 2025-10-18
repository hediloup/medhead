package com.medhead.poc.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Model representing the response to a bed allocation request.
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
    
    @JsonProperty("hospital_latitude")
    private Double hospitalLatitude;
    
    @JsonProperty("hospital_longitude")
    private Double hospitalLongitude;
    
    // Constructors
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
    
    public AllocationResponse(String hospitalName, Long hospitalId, Double distanceKm, 
                            String specialty, Integer availableBeds, Integer estimatedTimeMinutes,
                            Double hospitalLatitude, Double hospitalLongitude) {
        this.hospitalName = hospitalName;
        this.hospitalId = hospitalId;
        this.distanceKm = distanceKm;
        this.specialty = specialty;
        this.availableBeds = availableBeds;
        this.estimatedTimeMinutes = estimatedTimeMinutes;
        this.hospitalLatitude = hospitalLatitude;
        this.hospitalLongitude = hospitalLongitude;
    }
    
    // Getters and Setters
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
    
    public Double getHospitalLatitude() {
        return hospitalLatitude;
    }
    
    public void setHospitalLatitude(Double hospitalLatitude) {
        this.hospitalLatitude = hospitalLatitude;
    }
    
    public Double getHospitalLongitude() {
        return hospitalLongitude;
    }
    
    public void setHospitalLongitude(Double hospitalLongitude) {
        this.hospitalLongitude = hospitalLongitude;
    }
    
    // Méthodes de compatibilité pour les tests
    public Double getDistance() {
        return getDistanceKm();
    }
    
    public void setDistance(Double distance) {
        setDistanceKm(distance);
    }
    
    public Integer getAvailableBedsAfterAllocation() {
        return getAvailableBeds();
    }
    
    public void setAvailableBedsAfterAllocation(Integer availableBedsAfterAllocation) {
        setAvailableBeds(availableBedsAfterAllocation);
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
                ", hospitalLatitude=" + hospitalLatitude +
                ", hospitalLongitude=" + hospitalLongitude +
                '}';
    }
}
