package com.medhead.poc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medhead.poc.config.GoogleMapsConfig;
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

/**
 * Service pour les appels à l'API Google Maps Directions
 */
@Service
public class GoogleMapsService {
    
    private static final Logger logger = LoggerFactory.getLogger(GoogleMapsService.class);
    private static final int REQUEST_TIMEOUT_SECONDS = 10;
    
    private final GoogleMapsConfig config;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public GoogleMapsService(GoogleMapsConfig config) {
        this.config = config;
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
        
        if (config.getApiKey() == null || config.getApiKey().isEmpty()) {
            logger.warn("Clé API Google Maps non configurée, utilisation du calcul de distance de fallback");
            return createFallbackResult(originLat, originLon, destinationLat, destinationLon);
        }
        
        try {
            String origin = String.format("%.6f,%.6f", originLat, originLon);
            String destination = String.format("%.6f,%.6f", destinationLat, destinationLon);
            
            String url = buildDirectionsUrl(origin, destination);
            
            logger.debug("Appel API Google Maps: {}", url);
            
            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS))
                    .block();
            
            RouteInfo routeInfo = objectMapper.readValue(response, RouteInfo.class);
            
            return parseRouteResponse(routeInfo);
            
        } catch (WebClientResponseException e) {
            logger.error("Erreur HTTP lors de l'appel à l'API Google Maps: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            logger.warn("Utilisation du calcul de distance de fallback en raison de l'erreur API");
            return createFallbackResult(originLat, originLon, destinationLat, destinationLon);
            
        } catch (Exception e) {
            logger.error("Erreur lors du calcul de route avec Google Maps", e);
            logger.warn("Utilisation du calcul de distance de fallback en raison de l'erreur");
            return createFallbackResult(originLat, originLon, destinationLat, destinationLon);
        }
    }
    
    private String buildDirectionsUrl(String origin, String destination) {
        long departureTime = Instant.now().getEpochSecond();
        
        return config.getDirectionsUrl() + "?" +
                "origin=" + origin +
                "&destination=" + destination +
                "&mode=driving" +
                "&departure_time=" + departureTime +
                "&traffic_model=best_guess" +
                "&key=" + config.getApiKey();
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
