package com.medhead.poc.service;

import com.medhead.poc.dto.RouteResult;
import com.medhead.poc.model.Hospital;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for geographic distance calculations with real-time traffic optimization.
 */
@Service
public class DistanceCalculationService {
    
    private static final double EARTH_RADIUS_KM = 6371.0;
    
    @Autowired
    private GoogleMapsService googleMapsService;
    
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
     * Calcule la route optimale entre deux coordonnées avec prise en compte du trafic.
     * Utilise l'API Google Maps pour obtenir des données de trafic en temps réel.
     * 
     * @param originLat Latitude de départ
     * @param originLon Longitude de départ
     * @param destinationLat Latitude d'arrivée
     * @param destinationLon Longitude d'arrivée
     * @return Informations de route avec distance, durée et trafic
     */
    public RouteResult calculateOptimalRoute(double originLat, double originLon, 
                                           double destinationLat, double destinationLon) {
        return googleMapsService.calculateRouteWithTraffic(originLat, originLon, destinationLat, destinationLon);
    }
    
    /**
     * Calcule la route optimale vers un hôpital avec prise en compte du trafic.
     * 
     * @param latitude Latitude de départ
     * @param longitude Longitude de départ
     * @param hospital L'hôpital de destination
     * @return Informations de route avec distance, durée et trafic
     */
    public RouteResult calculateOptimalRouteToHospital(double latitude, double longitude, Hospital hospital) {
        // Utilise l'API Google Maps pour un calcul précis avec trafic
        return googleMapsService.calculateRouteWithTraffic(
            latitude, 
            longitude, 
            hospital.getLatitude(), 
            hospital.getLongitude()
        );
    }
    
    /**
     * Calcule la route optimale vers le meilleur hôpital en utilisant une stratégie ultra-optimisée :
     * - Utilise Haversine pour sélectionner le meilleur hôpital (ultra-rapide)
     * - Utilise Google Maps API uniquement pour obtenir la distance et le temps précis du meilleur hôpital
     * 
     * @param latitude Latitude de départ
     * @param longitude Longitude de départ
     * @param hospitals Liste des hôpitaux
     * @return Le meilleur hôpital avec ses informations de route précises
     */
    public HospitalWithRoute findBestHospitalWithOptimizedRoute(double latitude, double longitude, 
                                                               List<Hospital> hospitals) {
        // Étape 1: Calculer les distances Haversine pour tous les hôpitaux (ultra-rapide)
        Hospital bestHospital = hospitals.stream()
            .min(Comparator.comparingDouble(hospital -> 
                calculateDistanceToHospital(latitude, longitude, hospital)))
            .orElseThrow(() -> new RuntimeException("No hospitals available"));
        
        // Étape 2: Utiliser Google Maps uniquement pour le meilleur hôpital sélectionné
        try {
            RouteResult preciseRoute = googleMapsService.calculateRouteWithTraffic(
                latitude, longitude, 
                bestHospital.getLatitude(), bestHospital.getLongitude()
            );
            return new HospitalWithRoute(bestHospital, preciseRoute);
        } catch (Exception e) {
            // Fallback: utiliser Haversine si Google Maps échoue
            double haversineDistance = calculateDistanceToHospital(latitude, longitude, bestHospital);
            int estimatedTime = estimateTravelTime(haversineDistance);
            RouteResult fallbackRoute = new RouteResult(
                haversineDistance, estimatedTime, false, "Haversine fallback - Google Maps unavailable"
            );
            return new HospitalWithRoute(bestHospital, fallbackRoute);
        }
    }
    
    /**
     * Calcule les distances pour une liste d'hôpitaux en utilisant une stratégie optimisée :
     * - Utilise Haversine pour tous les hôpitaux (rapide)
     * - Utilise Google Maps API uniquement pour les 3 hôpitaux les plus proches (précis)
     * 
     * @param latitude Latitude de départ
     * @param longitude Longitude de départ
     * @param hospitals Liste des hôpitaux
     * @return Liste des hôpitaux avec leurs informations de route optimisées
     * @deprecated Utilisez findBestHospitalWithOptimizedRoute pour de meilleures performances
     */
    @Deprecated
    public List<HospitalWithRoute> calculateOptimizedRoutesToHospitals(double latitude, double longitude, 
                                                                      List<Hospital> hospitals) {
        // Étape 1: Calculer les distances Haversine pour tous les hôpitaux (rapide)
        List<HospitalWithDistance> hospitalsWithHaversineDistance = hospitals.stream()
            .map(hospital -> {
                double haversineDistance = calculateDistanceToHospital(latitude, longitude, hospital);
                return new HospitalWithDistance(hospital, haversineDistance);
            })
            .sorted(Comparator.comparingDouble(HospitalWithDistance::getHaversineDistance))
            .collect(Collectors.toList());
        
        // Étape 2: Prendre les 3 hôpitaux les plus proches selon Haversine
        List<Hospital> closestHospitals = hospitalsWithHaversineDistance.stream()
            .limit(3)
            .map(HospitalWithDistance::getHospital)
            .collect(Collectors.toList());
        
        // Étape 3: Calculer les routes précises avec Google Maps pour les 3 plus proches
        List<HospitalWithRoute> optimizedRoutes = closestHospitals.stream()
            .map(hospital -> {
                try {
                    RouteResult routeResult = googleMapsService.calculateRouteWithTraffic(
                        latitude, longitude, hospital.getLatitude(), hospital.getLongitude()
                    );
                    return new HospitalWithRoute(hospital, routeResult);
                } catch (Exception e) {
                    // En cas d'erreur Google Maps, utiliser Haversine comme fallback
                    double haversineDistance = calculateDistanceToHospital(latitude, longitude, hospital);
                    int estimatedTime = estimateTravelTime(haversineDistance);
                    RouteResult fallbackRoute = new RouteResult(
                        haversineDistance, estimatedTime, false, "Haversine fallback"
                    );
                    return new HospitalWithRoute(hospital, fallbackRoute);
                }
            })
            .filter(hwr -> !hwr.getRouteResult().isError())
            .sorted(Comparator.comparingInt(hwr -> hwr.getRouteResult().getOptimalDurationMinutes()))
            .collect(Collectors.toList());
        
        // Étape 4: Ajouter les hôpitaux restants avec des estimations Haversine
        List<Hospital> remainingHospitals = hospitalsWithHaversineDistance.stream()
            .skip(3)
            .map(HospitalWithDistance::getHospital)
            .collect(Collectors.toList());
        
        List<HospitalWithRoute> remainingRoutes = remainingHospitals.stream()
            .map(hospital -> {
                double haversineDistance = calculateDistanceToHospital(latitude, longitude, hospital);
                int estimatedTime = estimateTravelTime(haversineDistance);
                RouteResult haversineRoute = new RouteResult(
                    haversineDistance, estimatedTime, false, "Haversine estimation"
                );
                return new HospitalWithRoute(hospital, haversineRoute);
            })
            .collect(Collectors.toList());
        
        // Étape 5: Combiner et trier par temps de trajet
        List<HospitalWithRoute> allRoutes = new ArrayList<>();
        allRoutes.addAll(optimizedRoutes);
        allRoutes.addAll(remainingRoutes);
        
        return allRoutes.stream()
            .sorted(Comparator.comparingInt(hwr -> hwr.getRouteResult().getOptimalDurationMinutes()))
            .collect(Collectors.toList());
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
    
    /**
     * Estimates travel time in minutes based on distance and custom speed.
     * 
     * @param distanceKm Distance in kilometers
     * @param speedKmh Speed in kilometers per hour
     * @return Estimated time in minutes
     */
    public int estimateTravelTime(double distanceKm, int speedKmh) {
        double timeHours = distanceKm / speedKmh;
        return (int) Math.round(timeHours * 60);
    }
    
    /**
     * Classe interne pour stocker un hôpital avec sa distance Haversine.
     */
    public static class HospitalWithDistance {
        private final Hospital hospital;
        private final double haversineDistance;
        
        public HospitalWithDistance(Hospital hospital, double haversineDistance) {
            this.hospital = hospital;
            this.haversineDistance = haversineDistance;
        }
        
        public Hospital getHospital() {
            return hospital;
        }
        
        public double getHaversineDistance() {
            return haversineDistance;
        }
    }
    
    /**
     * Classe interne pour stocker un hôpital avec ses informations de route.
     */
    public static class HospitalWithRoute {
        private final Hospital hospital;
        private final RouteResult routeResult;
        
        public HospitalWithRoute(Hospital hospital, RouteResult routeResult) {
            this.hospital = hospital;
            this.routeResult = routeResult;
        }
        
        public Hospital getHospital() {
            return hospital;
        }
        
        public RouteResult getRouteResult() {
            return routeResult;
        }
    }
}
