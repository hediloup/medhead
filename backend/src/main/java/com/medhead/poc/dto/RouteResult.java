package com.medhead.poc.dto;

/**
 * DTO pour les résultats de calcul de route
 */
public class RouteResult {
    
    private double distanceKm;
    private int durationMinutes;
    private int durationWithTrafficMinutes;
    private boolean hasTrafficData;
    private String errorMessage;
    
    public RouteResult() {}
    
    public RouteResult(double distanceKm, int durationMinutes, int durationWithTrafficMinutes, boolean hasTrafficData) {
        this.distanceKm = distanceKm;
        this.durationMinutes = durationMinutes;
        this.durationWithTrafficMinutes = durationWithTrafficMinutes;
        this.hasTrafficData = hasTrafficData;
    }
    
    public RouteResult(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public double getDistanceKm() {
        return distanceKm;
    }
    
    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }
    
    public int getDurationMinutes() {
        return durationMinutes;
    }
    
    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    
    public int getDurationWithTrafficMinutes() {
        return durationWithTrafficMinutes;
    }
    
    public void setDurationWithTrafficMinutes(int durationWithTrafficMinutes) {
        this.durationWithTrafficMinutes = durationWithTrafficMinutes;
    }
    
    public boolean isHasTrafficData() {
        return hasTrafficData;
    }
    
    public void setHasTrafficData(boolean hasTrafficData) {
        this.hasTrafficData = hasTrafficData;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public boolean isError() {
        return errorMessage != null && !errorMessage.isEmpty();
    }
    
    /**
     * Retourne le temps de trajet optimal (avec trafic si disponible, sinon sans trafic)
     */
    public int getOptimalDurationMinutes() {
        return hasTrafficData ? durationWithTrafficMinutes : durationMinutes;
    }
    
    /**
     * Définit le temps de trajet optimal
     */
    public void setOptimalDurationMinutes(int optimalDurationMinutes) {
        if (hasTrafficData) {
            this.durationWithTrafficMinutes = optimalDurationMinutes;
        } else {
            this.durationMinutes = optimalDurationMinutes;
        }
    }
    
    /**
     * Définit si il y a une erreur
     */
    public void setError(boolean error) {
        if (error && this.errorMessage == null) {
            this.errorMessage = "Unknown error";
        } else if (!error) {
            this.errorMessage = null;
        }
    }
}
