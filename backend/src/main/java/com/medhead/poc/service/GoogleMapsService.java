package com.medhead.poc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import com.medhead.poc.dto.RouteResult;
import com.medhead.poc.model.RouteInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service pour les appels à l'API Google Maps Directions
 */
@Service
public class GoogleMapsService {
    
    private static final Logger logger = LoggerFactory.getLogger(GoogleMapsService.class);
    private static final int REQUEST_TIMEOUT_SECONDS = 5;
    
    @Value("${google.maps.api.key:}")
    private String apiKey;
    
    @Value("${google.maps.directions.url:https://maps.googleapis.com/maps/api/directions/json}")
    private String directionsUrl;
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<String, RouteResult> routeCache = new ConcurrentHashMap<>();
    
    @Autowired
    public GoogleMapsService() {
        this.webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Calcule la route et le temps de trajet entre deux coordonnées
     * avec prise en compte du trafic en temps réel.
     * Utilise un système de fallback en cas d'erreur.
     */
    public RouteResult calculateRouteWithTraffic(double originLat, double originLon, 
                                                double destinationLat, double destinationLon) {
        
        // Check cache first
        String cacheKey = String.format("%.4f,%.4f-%.4f,%.4f", originLat, originLon, destinationLat, destinationLon);
        RouteResult cachedResult = routeCache.get(cacheKey);
        if (cachedResult != null) {
            logger.debug("Route trouvée dans le cache");
            return cachedResult;
        }
        
        // Temporairement désactivé pour éviter les timeouts
        logger.debug("Utilisation du calcul de distance de fallback pour éviter les timeouts");
        return createFallbackResult(originLat, originLon, destinationLat, destinationLon);
    }
    
    private String buildDirectionsUrl(String origin, String destination) {
        long departureTime = Instant.now().getEpochSecond();
        
        return directionsUrl + "?" +
                "origin=" + origin +
                "&destination=" + destination +
                "&mode=driving" +
                "&departure_time=" + departureTime +
                "&traffic_model=best_guess" +
                "&key=" + apiKey;
    }
    
    private RouteResult parseRouteResponse(RouteInfo routeInfo) {
        if (!"OK".equals(routeInfo.getStatus())) {
            logger.error("Erreur API Google Maps: {} - {}", routeInfo.getStatus(), routeInfo.getErrorMessage());
            return new RouteResult("Erreur API: " + routeInfo.getErrorMessage());
        }
        
        if (routeInfo.getRoutes() == null || routeInfo.getRoutes().isEmpty()) {
            return new RouteResult("Aucune route trouvée");
        }
        
        RouteInfo.Route route = routeInfo.getRoutes().get(0);
        if (route.getLegs() == null || route.getLegs().isEmpty()) {
            return new RouteResult("Aucun segment de route trouvé");
        }
        
        RouteInfo.Leg leg = route.getLegs().get(0);
        
        // Distance en kilomètres
        double distanceKm = leg.getDistance().getValue() / 1000.0;
        
        // Durée sans trafic en minutes
        int durationMinutes = (int) Math.round(leg.getDuration().getValue() / 60.0);
        
        // Durée avec trafic en minutes (si disponible)
        int durationWithTrafficMinutes = durationMinutes;
        boolean hasTrafficData = false;
        
        if (leg.getDurationInTraffic() != null) {
            durationWithTrafficMinutes = (int) Math.round(leg.getDurationInTraffic().getValue() / 60.0);
            hasTrafficData = true;
        }
        
        logger.debug("Route calculée: {} km, {} min ({} min avec trafic)", 
                distanceKm, durationMinutes, durationWithTrafficMinutes);
        
        return new RouteResult(distanceKm, durationMinutes, durationWithTrafficMinutes, hasTrafficData);
    }
    
    private RouteResult createFallbackResult(double originLat, double originLon, 
                                           double destinationLat, double destinationLon) {
        // Utilise la formule Haversine comme fallback
        DistanceCalculationService fallbackService = new DistanceCalculationService();
        double distanceKm = fallbackService.calculateDistance(originLat, originLon, destinationLat, destinationLon);
        int durationMinutes = fallbackService.estimateTravelTime(distanceKm);
        
        return new RouteResult(distanceKm, durationMinutes, durationMinutes, false);
    }
}
