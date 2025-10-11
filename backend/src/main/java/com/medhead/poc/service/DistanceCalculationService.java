package com.medhead.poc.service;

import com.medhead.poc.model.Hospital;
import org.springframework.stereotype.Service;

/**
 * Service for geographic distance calculations.
 */
@Service
public class DistanceCalculationService {
    
    private static final double EARTH_RADIUS_KM = 6371.0;
    
    /**
     * Calculates the distance in kilometers between two geographic points
     * using the Haversine formula.
     * 
     * @param lat1 Latitude of the first point
     * @param lon1 Longitude of the first point
     * @param lat2 Latitude of the second point
     * @param lon2 Longitude of the second point
     * @return Distance in kilometers
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Convert to radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);
        
        // Differences
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;
        
        // Haversine formula
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_KM * c;
    }
    
    /**
     * Calculates the distance between a geographic point and a hospital.
     * 
     * @param latitude Latitude of the point
     * @param longitude Longitude of the point
     * @param hospital The hospital
     * @return Distance in kilometers
     */
    public double calculateDistanceToHospital(double latitude, double longitude, Hospital hospital) {
        return calculateDistance(latitude, longitude, hospital.getLatitude(), hospital.getLongitude());
    }
    
    /**
     * Estimates travel time in minutes based on distance.
     * Uses an average speed of 50 km/h in the city.
     * 
     * @param distanceKm Distance in kilometers
     * @return Estimated time in minutes
     */
    public int estimateTravelTime(double distanceKm) {
        // Average speed in city: 50 km/h
        double averageSpeedKmh = 50.0;
        double timeHours = distanceKm / averageSpeedKmh;
        return (int) Math.round(timeHours * 60);
    }
}
