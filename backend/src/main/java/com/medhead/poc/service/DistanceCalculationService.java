package com.medhead.poc.service;

import com.medhead.poc.model.Hospital;
import org.springframework.stereotype.Service;

/**
 * Service pour le calcul de distances géographiques.
 */
@Service
public class DistanceCalculationService {
    
    private static final double EARTH_RADIUS_KM = 6371.0;
    
    /**
     * Calcule la distance en kilomètres entre deux points géographiques
     * en utilisant la formule de Haversine.
     * 
     * @param lat1 Latitude du premier point
     * @param lon1 Longitude du premier point
     * @param lat2 Latitude du deuxième point
     * @param lon2 Longitude du deuxième point
     * @return Distance en kilomètres
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Conversion en radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);
        
        // Différences
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;
        
        // Formule de Haversine
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_KM * c;
    }
    
    /**
     * Calcule la distance entre un point géographique et un hôpital.
     * 
     * @param latitude Latitude du point
     * @param longitude Longitude du point
     * @param hospital L'hôpital
     * @return Distance en kilomètres
     */
    public double calculateDistanceToHospital(double latitude, double longitude, Hospital hospital) {
        return calculateDistance(latitude, longitude, hospital.getLatitude(), hospital.getLongitude());
    }
    
    /**
     * Estime le temps de trajet en minutes basé sur la distance.
     * Utilise une vitesse moyenne de 50 km/h en ville.
     * 
     * @param distanceKm Distance en kilomètres
     * @return Temps estimé en minutes
     */
    public int estimateTravelTime(double distanceKm) {
        // Vitesse moyenne en ville : 50 km/h
        double averageSpeedKmh = 50.0;
        double timeHours = distanceKm / averageSpeedKmh;
        return (int) Math.round(timeHours * 60);
    }
}
