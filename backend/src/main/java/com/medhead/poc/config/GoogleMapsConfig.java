package com.medhead.poc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration pour l'API Google Maps
 */
@Component
@ConfigurationProperties(prefix = "google.maps")
public class GoogleMapsConfig {
    
    private String apiKey;
    private String directionsUrl;
    
    public String getApiKey() {
        return apiKey;
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public String getDirectionsUrl() {
        return directionsUrl;
    }
    
    public void setDirectionsUrl(String directionsUrl) {
        this.directionsUrl = directionsUrl;
    }
}
