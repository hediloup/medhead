package com.medhead.poc.unit.service;

import com.medhead.poc.dto.RouteResult;
import com.medhead.poc.model.Hospital;
import com.medhead.poc.service.DistanceCalculationService;
import com.medhead.poc.service.GoogleMapsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires pour DistanceCalculationService
 * Approche TDD : Test-Driven Development
 */
@RunWith(MockitoJUnitRunner.class)
public class DistanceCalculationServiceTest {

    @Mock
    private GoogleMapsService googleMapsService;

    @InjectMocks
    private DistanceCalculationService distanceCalculationService;

    private Hospital testHospital;

    @Before
    public void setUp() {
        testHospital = new Hospital("Test Hospital", 53.3976314, -2.1829641, "Manchester", "Test Address", 10);
        testHospital.setId(1L);
    }

    @Test
    public void testCalculateDistance_SameLocation() {
        // Given
        double lat = 53.3976314;
        double lon = -2.1829641;

        // When
        double distance = distanceCalculationService.calculateDistance(lat, lon, lat, lon);

        // Then
        assertEquals("La distance entre deux points identiques doit être 0", 0.0, distance, 0.001);
    }

    @Test
    public void testCalculateDistance_KnownDistance() {
        // Given - Distance approximative entre Manchester et Liverpool
        double manchesterLat = 53.4808;
        double manchesterLon = -2.2426;
        double liverpoolLat = 53.4106;
        double liverpoolLon = -2.9779;

        // When
        double distance = distanceCalculationService.calculateDistance(manchesterLat, manchesterLon, liverpoolLat, liverpoolLon);

        // Then - Distance attendue environ 50-60 km
        assertTrue("La distance doit être raisonnable", distance > 40 && distance < 70);
    }

    @Test
    public void testCalculateDistanceToHospital() {
        // Given
        double patientLat = 53.4808;
        double patientLon = -2.2426;

        // When
        double distance = distanceCalculationService.calculateDistanceToHospital(patientLat, patientLon, testHospital);

        // Then
        assertTrue("La distance doit être positive", distance > 0);
        assertTrue("La distance doit être raisonnable", distance < 100); // Moins de 100 km
    }

    @Test
    public void testCalculateOptimalRoute_Success() {
        // Given
        double originLat = 53.4808;
        double originLon = -2.2426;
        double destinationLat = 53.3976314;
        double destinationLon = -2.1829641;

        RouteResult expectedRouteResult = new RouteResult();
        expectedRouteResult.setDistanceKm(5.2);
        expectedRouteResult.setOptimalDurationMinutes(15);
        expectedRouteResult.setError(false);

        when(googleMapsService.calculateRouteWithTraffic(originLat, originLon, destinationLat, destinationLon))
                .thenReturn(expectedRouteResult);

        // When
        RouteResult result = distanceCalculationService.calculateOptimalRoute(originLat, originLon, destinationLat, destinationLon);

        // Then
        assertNotNull("Le résultat ne doit pas être null", result);
        assertEquals("La distance doit être correcte", 5.2, result.getDistanceKm(), 0.001);
        assertEquals("La durée doit être correcte", 15, result.getOptimalDurationMinutes());
        assertFalse("Il ne doit pas y avoir d'erreur", result.isError());
    }

    @Test
    public void testCalculateOptimalRoute_Error() {
        // Given
        double originLat = 53.4808;
        double originLon = -2.2426;
        double destinationLat = 53.3976314;
        double destinationLon = -2.1829641;

        RouteResult errorRouteResult = new RouteResult();
        errorRouteResult.setError(true);
        errorRouteResult.setErrorMessage("Route calculation failed");

        when(googleMapsService.calculateRouteWithTraffic(originLat, originLon, destinationLat, destinationLon))
                .thenReturn(errorRouteResult);

        // When
        RouteResult result = distanceCalculationService.calculateOptimalRoute(originLat, originLon, destinationLat, destinationLon);

        // Then
        assertNotNull("Le résultat ne doit pas être null", result);
        assertTrue("Il doit y avoir une erreur", result.isError());
        assertEquals("Le message d'erreur doit être correct", "Route calculation failed", result.getErrorMessage());
    }

    @Test
    public void testCalculateOptimalRouteToHospital() {
        // Given
        double latitude = 53.4808;
        double longitude = -2.2426;

        RouteResult expectedRouteResult = new RouteResult();
        expectedRouteResult.setDistanceKm(8.5);
        expectedRouteResult.setOptimalDurationMinutes(20);
        expectedRouteResult.setError(false);

        when(googleMapsService.calculateRouteWithTraffic(latitude, longitude, testHospital.getLatitude(), testHospital.getLongitude()))
                .thenReturn(expectedRouteResult);

        // When
        RouteResult result = distanceCalculationService.calculateOptimalRouteToHospital(latitude, longitude, testHospital);

        // Then
        assertNotNull("Le résultat ne doit pas être null", result);
        assertEquals("La distance doit être correcte", 8.5, result.getDistanceKm(), 0.001);
        assertEquals("La durée doit être correcte", 20, result.getOptimalDurationMinutes());
        assertFalse("Il ne doit pas y avoir d'erreur", result.isError());
    }

    @Test
    public void testEstimateTravelTime_ShortDistance() {
        // Given
        double distanceKm = 5.0; // 5 km

        // When
        int timeMinutes = distanceCalculationService.estimateTravelTime(distanceKm);

        // Then
        assertEquals("Temps estimé pour 5 km à 50 km/h", 6, timeMinutes); // 5/50 * 60 = 6 minutes
    }

    @Test
    public void testEstimateTravelTime_LongDistance() {
        // Given
        double distanceKm = 50.0; // 50 km

        // When
        int timeMinutes = distanceCalculationService.estimateTravelTime(distanceKm);

        // Then
        assertEquals("Temps estimé pour 50 km à 50 km/h", 60, timeMinutes); // 50/50 * 60 = 60 minutes
    }

    @Test
    public void testEstimateTravelTime_ZeroDistance() {
        // Given
        double distanceKm = 0.0;

        // When
        int timeMinutes = distanceCalculationService.estimateTravelTime(distanceKm);

        // Then
        assertEquals("Temps estimé pour 0 km", 0, timeMinutes);
    }

    @Test
    public void testEstimateTravelTime_VeryLongDistance() {
        // Given
        double distanceKm = 100.0; // 100 km

        // When
        int timeMinutes = distanceCalculationService.estimateTravelTime(distanceKm);

        // Then
        assertEquals("Temps estimé pour 100 km à 50 km/h", 120, timeMinutes); // 100/50 * 60 = 120 minutes
    }
}
