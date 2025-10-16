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
    
    @JsonProperty("hospital_latitude")
    private Double hospitalLatitude;
    
    @JsonProperty("hospital_longitude")
    private Double hospitalLongitude;
    
    @JsonProperty("specialty")
    private String specialty;
    
    @JsonProperty("available_beds")
    private Integer availableBeds;
    
    // Constructors
    public AllocationResponse() {}
    
    public AllocationResponse(String hospitalName, Long hospitalId, Double hospitalLatitude, 
                            Double hospitalLongitude, String specialty, Integer availableBeds) {
        this.hospitalName = hospitalName;
        this.hospitalId = hospitalId;
        this.hospitalLatitude = hospitalLatitude;
        this.hospitalLongitude = hospitalLongitude;
        this.specialty = specialty;
        this.availableBeds = availableBeds;
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
    
    
    // Méthodes de compatibilité pour les tests (deprecated)
    @Deprecated
    public Double getDistance() {
        return null; // Distance now handled by frontend
    }
    
    @Deprecated
    public void setDistance(Double distance) {
        // Distance now handled by frontend
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
                ", hospitalLatitude=" + hospitalLatitude +
                ", hospitalLongitude=" + hospitalLongitude +
                ", specialty='" + specialty + '\'' +
                ", availableBeds=" + availableBeds +
                '}';
    }
}
