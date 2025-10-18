package com.medhead.poc.unit.service;

import com.medhead.poc.model.Hospital;
import com.medhead.poc.service.DistanceCalculationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

/**
 * Tests unitaires pour DistanceCalculationService (Haversine uniquement)
 */
@RunWith(MockitoJUnitRunner.class)
public class DistanceCalculationServiceTest {

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
        System.out.println("🔍 Test: Service Distance - Même localisation");
        double lat = 53.3976314;
        double lon = -2.1829641;
        System.out.println("   Coordonnées: " + lat + ", " + lon);

        // When
        System.out.println("   Calcul de distance entre points identiques");
        double distance = distanceCalculationService.calculateDistance(lat, lon, lat, lon);

        // Then
        System.out.println("   Distance calculée: " + distance + " km");
        assertEquals("La distance entre deux points identiques doit être 0", 0.0, distance, 0.001);
        System.out.println("   ✅ Test Distance réussi - Distance nulle pour points identiques");
    }

    @Test
    public void testCalculateDistance_KnownDistance() {
        // Given - Distance approximative entre Manchester et Liverpool
        System.out.println("🔍 Test: Service Distance - Distance connue Manchester-Liverpool");
        double manchesterLat = 53.4808;
        double manchesterLon = -2.2426;
        double liverpoolLat = 53.4106;
        double liverpoolLon = -2.9779;
        System.out.println("   Manchester: " + manchesterLat + ", " + manchesterLon);
        System.out.println("   Liverpool: " + liverpoolLat + ", " + liverpoolLon);

        // When
        System.out.println("   Calcul de distance Manchester-Liverpool");
        double distance = distanceCalculationService.calculateDistance(manchesterLat, manchesterLon, liverpoolLat, liverpoolLon);

        // Then - Distance attendue environ 50-60 km
        System.out.println("   Distance calculée: " + distance + " km");
        assertTrue("La distance doit être raisonnable", distance > 40 && distance < 70);
        System.out.println("   ✅ Test Distance réussi - Distance Manchester-Liverpool cohérente");
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
}
