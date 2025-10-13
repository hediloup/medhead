package com.medhead.poc.dto;

/**
 * Projection interface for optimized hospital queries
 * Reduces memory usage and improves query performance by selecting only required fields
 */
public interface HospitalProjection {
    Long getId();
    String getName();
    String getSpecialty();
    Double getLatitude();
    Double getLongitude();
    Integer getAvailableBeds();
}
