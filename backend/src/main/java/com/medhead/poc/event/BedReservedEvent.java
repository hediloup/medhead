package com.medhead.poc.event;

import java.time.LocalDateTime;

/**
 * Event published when a hospital bed is reserved for a patient.
 * Contains only necessary and anonymized data.
 */
public class BedReservedEvent {
    
    private String eventId;
    private String eventType;
    private LocalDateTime timestamp;
    
    // Anonymized patient data
    private String patientUuid;
    private String anonymizedPatientId;
    private String requiredSpecialty;
    private String severityLevel;
    private String ageGroup;
    
    // Hospital data
    private Long hospitalId;
    private String hospitalName;
    private String hospitalCity;
    private Double distanceKm;
    
    // Allocation metadata
    private Integer availableBedsAfter;
    private Integer estimatedTimeMinutes;
    private String allocationStatus;
    
    // Constructors
    public BedReservedEvent() {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.eventType = "BED_RESERVED";
        this.timestamp = LocalDateTime.now();
    }
    
    public BedReservedEvent(String patientUuid, 
                           String anonymizedPatientId,
                           String requiredSpecialty,
                           String severityLevel,
                           String ageGroup,
                           Long hospitalId,
                           String hospitalName,
                           String hospitalCity,
                           Double distanceKm,
                           Integer availableBedsAfter,
                           Integer estimatedTimeMinutes) {
        this();
        this.patientUuid = patientUuid;
        this.anonymizedPatientId = anonymizedPatientId;
        this.requiredSpecialty = requiredSpecialty;
        this.severityLevel = severityLevel;
        this.ageGroup = ageGroup;
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
        this.hospitalCity = hospitalCity;
        this.distanceKm = distanceKm;
        this.availableBedsAfter = availableBedsAfter;
        this.estimatedTimeMinutes = estimatedTimeMinutes;
        this.allocationStatus = "CONFIRMED";
    }
    
    // Getters and Setters
    public String getEventId() {
        return eventId;
    }
    
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getPatientUuid() {
        return patientUuid;
    }
    
    public void setPatientUuid(String patientUuid) {
        this.patientUuid = patientUuid;
    }
    
    public String getAnonymizedPatientId() {
        return anonymizedPatientId;
    }
    
    public void setAnonymizedPatientId(String anonymizedPatientId) {
        this.anonymizedPatientId = anonymizedPatientId;
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
    
    public String getAgeGroup() {
        return ageGroup;
    }
    
    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }
    
    public Long getHospitalId() {
        return hospitalId;
    }
    
    public void setHospitalId(Long hospitalId) {
        this.hospitalId = hospitalId;
    }
    
    public String getHospitalName() {
        return hospitalName;
    }
    
    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }
    
    public String getHospitalCity() {
        return hospitalCity;
    }
    
    public void setHospitalCity(String hospitalCity) {
        this.hospitalCity = hospitalCity;
    }
    
    public Double getDistanceKm() {
        return distanceKm;
    }
    
    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }
    
    public Integer getAvailableBedsAfter() {
        return availableBedsAfter;
    }
    
    public void setAvailableBedsAfter(Integer availableBedsAfter) {
        this.availableBedsAfter = availableBedsAfter;
    }
    
    public Integer getEstimatedTimeMinutes() {
        return estimatedTimeMinutes;
    }
    
    public void setEstimatedTimeMinutes(Integer estimatedTimeMinutes) {
        this.estimatedTimeMinutes = estimatedTimeMinutes;
    }
    
    public String getAllocationStatus() {
        return allocationStatus;
    }
    
    public void setAllocationStatus(String allocationStatus) {
        this.allocationStatus = allocationStatus;
    }
    
    @Override
    public String toString() {
        return "BedReservedEvent{" +
                "eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", timestamp=" + timestamp +
                ", patientUuid='" + patientUuid + '\'' +
                ", anonymizedPatientId='" + anonymizedPatientId + '\'' +
                ", requiredSpecialty='" + requiredSpecialty + '\'' +
                ", severityLevel='" + severityLevel + '\'' +
                ", hospitalId=" + hospitalId +
                ", hospitalName='" + hospitalName + '\'' +
                ", allocationStatus='" + allocationStatus + '\'' +
                '}';
    }
}
