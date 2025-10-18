package com.medhead.poc.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medhead.poc.dto.RouteResult;
import com.medhead.poc.model.AllocationRequest;
import com.medhead.poc.model.Hospital;
import com.medhead.poc.model.Speciality;
import com.medhead.poc.repository.HospitalRepository;
import com.medhead.poc.repository.SpecialityRepository;
import com.medhead.poc.service.GoogleMapsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;
import org.mockito.invocation.InvocationOnMock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour l'API d'allocation d'hôpitaux
 * Approche TDD : Test-Driven Development
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@Transactional
@org.springframework.test.context.ActiveProfiles("test")
public class AllocationIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private SpecialityRepository specialityRepository;

    @MockBean
    private GoogleMapsService googleMapsService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Hospital hospital1;
    private Hospital hospital2;
    private Speciality cardiology;
    private Speciality neurology;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();

        // Nettoyer les données de test
        hospitalRepository.deleteAll();
        specialityRepository.deleteAll();

        // Créer les spécialités
        cardiology = new Speciality();
        cardiology.setName("Cardiology");
        cardiology = specialityRepository.save(cardiology);

        neurology = new Speciality();
        neurology.setName("Neurology");
        neurology = specialityRepository.save(neurology);

        // Créer les hôpitaux
        hospital1 = new Hospital("Hôpital Central", 53.4808, -2.2426, "Manchester", "123 Main St", 5);
        Set<Speciality> specialities1 = new HashSet<>();
        specialities1.add(cardiology);
        hospital1.setSpecialities(specialities1);
        hospital1 = hospitalRepository.save(hospital1);

        hospital2 = new Hospital("Hôpital Nord", 53.4808, -2.2426, "Manchester", "456 Oak Ave", 3);
        Set<Speciality> specialities2 = new HashSet<>();
        specialities2.add(cardiology);
        specialities2.add(neurology);
        hospital2.setSpecialities(specialities2);
        hospital2 = hospitalRepository.save(hospital2);

        // Configurer le mock du service Google Maps pour calculer la distance réelle
        when(googleMapsService.calculateRouteWithTraffic(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenAnswer(invocation -> {
                    double originLat = invocation.getArgument(0);
                    double originLon = invocation.getArgument(1);
                    double destLat = invocation.getArgument(2);
                    double destLon = invocation.getArgument(3);
                    
                    // Calculer la distance réelle avec la formule de Haversine
                    double distance = calculateHaversineDistance(originLat, originLon, destLat, destLon);
                    
                    // Temps estimé basé sur la distance (vitesse moyenne de 30 km/h)
                    int durationMinutes = (int) (distance * 60 / 30);
                    
                    return new RouteResult(distance, durationMinutes, durationMinutes - 2, true);
                });
    }

    /**
     * Calcule la distance entre deux points géographiques avec la formule de Haversine
     */
    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0; // Rayon de la Terre en km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }

    @Test
    public void testAllocateHospital_POST_Success() throws Exception {
        // Given
        System.out.println("🔍 Test: Allocation POST - Succès");
        AllocationRequest request = new AllocationRequest("Cardiology", 53.3976314, -2.1829641);
        System.out.println("   Spécialité: " + request.getSpecialty());
        System.out.println("   Coordonnées: " + request.getLatitude() + ", " + request.getLongitude());

        // When & Then
        System.out.println("   Exécution de la requête POST /api/allocate");
        mockMvc.perform(post("/api/allocate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hospital_name").exists())
                .andExpect(jsonPath("$.hospital_id").exists())
                .andExpect(jsonPath("$.distance_km").exists())
                .andExpect(jsonPath("$.specialty").value("Cardiology"))
                .andExpect(jsonPath("$.available_beds").exists())
                .andExpect(jsonPath("$.estimated_time_minutes").exists());
        System.out.println("   ✅ Test POST réussi - Hôpital trouvé avec toutes les données requises");
    }

    @Test
    public void testAllocateHospital_GET_Success() throws Exception {
        // When & Then
        System.out.println("🔍 Test: Allocation GET - Succès");
        System.out.println("   Paramètres: specialty=Cardiology, lat=53.3976314, lng=-2.1829641");
        System.out.println("   Exécution de la requête GET /api/allocate");
        mockMvc.perform(get("/api/allocate")
                .param("specialty", "Cardiology")
                .param("latitude", "53.3976314")
                .param("longitude", "-2.1829641"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hospital_name").exists())
                .andExpect(jsonPath("$.specialty").value("Cardiology"));
        System.out.println("   ✅ Test GET réussi - Hôpital trouvé via paramètres GET");
    }

    @Test
    public void testAllocateHospital_InvalidSpecialty() throws Exception {
        // Given
        AllocationRequest request = new AllocationRequest("", 53.3976314, -2.1829641);

        // When & Then
        mockMvc.perform(post("/api/allocate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAllocateHospital_NullCoordinates() throws Exception {
        // Given
        AllocationRequest request = new AllocationRequest("Cardiology", null, -2.1829641);

        // When & Then
        mockMvc.perform(post("/api/allocate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAllocateHospital_NoAvailableHospital() throws Exception {
        // Given
        AllocationRequest request = new AllocationRequest("Pediatrics", 53.3976314, -2.1829641);

        // When & Then
        mockMvc.perform(post("/api/allocate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testHealthEndpoint() throws Exception {
        // When & Then
        System.out.println("🔍 Test: Endpoint Health");
        System.out.println("   Exécution de la requête GET /api/health");
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Allocation API operational"));
        System.out.println("   ✅ Test Health réussi - API opérationnelle");
    }

    @Test
    public void testTestEndpoint() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/test"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Test successful")));
    }

    @Test
    public void testDebugHospitalsEndpoint() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/debug/hospitals"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Hôpitaux avec spécialité Cardiology")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Hôpital Central")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Hôpital Nord")));
    }

    @Test
    public void testAllocationWithMultipleHospitals_SelectsClosest() throws Exception {
        // Given - Créer un hôpital plus proche
        Hospital closeHospital = new Hospital("Hôpital Proche", 53.3977, -2.1830, "Manchester", "789 Close St", 2);
        Set<Speciality> closeSpecialities = new HashSet<>();
        closeSpecialities.add(cardiology);
        closeHospital.setSpecialities(closeSpecialities);
        hospitalRepository.save(closeHospital);

        AllocationRequest request = new AllocationRequest("Cardiology", 53.3976314, -2.1829641);

        // When & Then
        mockMvc.perform(post("/api/allocate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hospital_name").value("Hôpital Proche"));
    }

    @Test
    public void testAllocationWithNoAvailableBeds() throws Exception {
        // Given - Créer un hôpital sans lits disponibles
        Hospital noBedsHospital = new Hospital("Hôpital Complet", 53.3976314, -2.1829641, "Manchester", "999 Full St", 0);
        Set<Speciality> noBedsSpecialities = new HashSet<>();
        noBedsSpecialities.add(cardiology);
        noBedsHospital.setSpecialities(noBedsSpecialities);
        hospitalRepository.save(noBedsHospital);

        // Supprimer les autres hôpitaux pour forcer l'utilisation de celui sans lits
        hospitalRepository.delete(hospital1);
        hospitalRepository.delete(hospital2);

        AllocationRequest request = new AllocationRequest("Cardiology", 53.3976314, -2.1829641);

        // When & Then
        mockMvc.perform(post("/api/allocate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAllocationWithSpecialtyMismatch() throws Exception {
        // Given - Créer un hôpital sans la spécialité demandée
        Hospital wrongSpecialtyHospital = new Hospital("Hôpital Mauvais", 53.3976314, -2.1829641, "Manchester", "888 Wrong St", 5);
        Set<Speciality> wrongSpecialities = new HashSet<>();
        wrongSpecialities.add(neurology); // Pas de cardiologie
        wrongSpecialtyHospital.setSpecialities(wrongSpecialities);
        hospitalRepository.save(wrongSpecialtyHospital);

        // Supprimer les autres hôpitaux
        hospitalRepository.delete(hospital1);
        hospitalRepository.delete(hospital2);

        AllocationRequest request = new AllocationRequest("Cardiology", 53.3976314, -2.1829641);

        // When & Then
        mockMvc.perform(post("/api/allocate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
