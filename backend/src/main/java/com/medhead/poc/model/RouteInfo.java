package com.medhead.poc.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Modèle pour les informations de route retournées par l'API Google Maps
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RouteInfo {
    
    @JsonProperty("routes")
    private List<Route> routes;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("error_message")
    private String errorMessage;
    
    public List<Route> getRoutes() {
        return routes;
    }
    
    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Route {
        @JsonProperty("legs")
        private List<Leg> legs;
        
        public List<Leg> getLegs() {
            return legs;
        }
        
        public void setLegs(List<Leg> legs) {
            this.legs = legs;
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Leg {
        @JsonProperty("distance")
        private Distance distance;
        
        @JsonProperty("duration")
        private Duration duration;
        
        @JsonProperty("duration_in_traffic")
        private Duration durationInTraffic;
        
        public Distance getDistance() {
            return distance;
        }
        
        public void setDistance(Distance distance) {
            this.distance = distance;
        }
        
        public Duration getDuration() {
            return duration;
        }
        
        public void setDuration(Duration duration) {
            this.duration = duration;
        }
        
        public Duration getDurationInTraffic() {
            return durationInTraffic;
        }
        
        public void setDurationInTraffic(Duration durationInTraffic) {
            this.durationInTraffic = durationInTraffic;
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Distance {
        @JsonProperty("value")
        private long value; // en mètres
        
        @JsonProperty("text")
        private String text;
        
        public long getValue() {
            return value;
        }
        
        public void setValue(long value) {
            this.value = value;
        }
        
        public String getText() {
            return text;
        }
        
        public void setText(String text) {
            this.text = text;
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Duration {
        @JsonProperty("value")
        private long value; // en secondes
        
        @JsonProperty("text")
        private String text;
        
        public long getValue() {
            return value;
        }
        
        public void setValue(long value) {
            this.value = value;
        }
        
        public String getText() {
            return text;
        }
        
        public void setText(String text) {
            this.text = text;
        }
    }
}
