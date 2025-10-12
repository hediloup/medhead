package com.medhead.poc.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medhead.poc.controller.AllocationController;
import com.medhead.poc.model.AllocationRequest;
import com.medhead.poc.model.AllocationResponse;
import com.medhead.poc.model.Hospital;
import com.medhead.poc.repository.HospitalRepository;
import com.medhead.poc.service.AllocationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour AllocationController
 * Approche TDD : Test-Driven Development
 */
@RunWith(MockitoJUnitRunner.class)
public class AllocationControllerTest {

    @Mock
    private AllocationService allocationService;

    @Mock
    private HospitalRepository hospitalRepository;

    @InjectMocks
    private AllocationController allocationController;

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testAllocateHospital_Success() throws Exception {
        // Given
        AllocationRequest request = new AllocationRequest("Cardiology", 53.3976314, -2.1829641);
        AllocationResponse expectedResponse = new AllocationResponse(
                "Hôpital Central", 1L, 5.2, "Cardiology", 4, 15
        );

        when(allocationService.findBestHospital(request))
                .thenReturn(expectedResponse);

        // When
        ResponseEntity<AllocationResponse> response = allocationController.allocateHospital(request);

        // Then
        assertNotNull("La réponse ne doit pas être null", response);
        assertEquals("Le statut HTTP doit être OK", HttpStatus.OK, response.getStatusCode());
        assertEquals("La réponse doit contenir les bonnes données", expectedResponse, response.getBody());

        verify(allocationService).findBestHospital(request);
    }

    @Test
    public void testAllocateHospital_IllegalArgumentException() throws Exception {
        // Given
        AllocationRequest invalidRequest = new AllocationRequest(null, 53.3976314, -2.1829641);

        when(allocationService.findBestHospital(invalidRequest))
                .thenThrow(new IllegalArgumentException("Specialty is required"));

        // When
        ResponseEntity<AllocationResponse> response = allocationController.allocateHospital(invalidRequest);

        // Then
        assertNotNull("La réponse ne doit pas être null", response);
        assertEquals("Le statut HTTP doit être BAD_REQUEST", HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull("Le body doit être null", response.getBody());

        verify(allocationService).findBestHospital(invalidRequest);
    }

    @Test
    public void testAllocateHospital_RuntimeException_NoHospital() throws Exception {
        // Given
        AllocationRequest request = new AllocationRequest("Cardiology", 53.3976314, -2.1829641);

        when(allocationService.findBestHospital(request))
                .thenThrow(new RuntimeException("No hospital available"));

        // When
        ResponseEntity<AllocationResponse> response = allocationController.allocateHospital(request);

        // Then
        assertNotNull("La réponse ne doit pas être null", response);
        assertEquals("Le statut HTTP doit être NOT_FOUND", HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull("Le body doit être null", response.getBody());

        verify(allocationService).findBestHospital(request);
    }

    @Test
    public void testAllocateHospital_GenericException() throws Exception {
        // Given
        AllocationRequest request = new AllocationRequest("Cardiology", 53.3976314, -2.1829641);

        when(allocationService.findBestHospital(request))
                .thenThrow(new RuntimeException("Unexpected error"));

        // When
        ResponseEntity<AllocationResponse> response = allocationController.allocateHospital(request);

        // Then
        assertNotNull("La réponse ne doit pas être null", response);
        assertEquals("Le statut HTTP doit être NOT_FOUND", 
                HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull("Le body doit être null", response.getBody());

        verify(allocationService).findBestHospital(request);
    }

    @Test
    public void testAllocateHospitalGet_Success() throws Exception {
        // Given
        String specialty = "Cardiology";
        Double latitude = 53.3976314;
        Double longitude = -2.1829641;

        AllocationResponse expectedResponse = new AllocationResponse(
                "Hôpital Central", 1L, 5.2, "Cardiology", 4, 15
        );

        when(allocationService.findBestHospital(any(AllocationRequest.class)))
                .thenReturn(expectedResponse);

        // When
        ResponseEntity<AllocationResponse> response = allocationController.allocateHospitalGet(
                specialty, latitude, longitude);

        // Then
        assertNotNull("La réponse ne doit pas être null", response);
        assertEquals("Le statut HTTP doit être OK", HttpStatus.OK, response.getStatusCode());
        assertEquals("La réponse doit contenir les bonnes données", expectedResponse, response.getBody());

        verify(allocationService).findBestHospital(any(AllocationRequest.class));
    }

    @Test
    public void testHealth() throws Exception {
        // When
        ResponseEntity<String> response = allocationController.health();

        // Then
        assertNotNull("La réponse ne doit pas être null", response);
        assertEquals("Le statut HTTP doit être OK", HttpStatus.OK, response.getStatusCode());
        assertEquals("Le message doit être correct", "Allocation API operational", response.getBody());
    }

    @Test
    public void testTest_Success() throws Exception {
        // Given
        AllocationResponse testResponse = new AllocationResponse(
                "Hôpital Central", 1L, 5.2, "Cardiology", 4, 15
        );

        when(allocationService.findBestHospital(any(AllocationRequest.class)))
                .thenReturn(testResponse);

        // When
        ResponseEntity<String> response = allocationController.test();

        // Then
        assertNotNull("La réponse ne doit pas être null", response);
        assertEquals("Le statut HTTP doit être OK", HttpStatus.OK, response.getStatusCode());
        assertTrue("Le message doit contenir 'Test successful'", 
                response.getBody().contains("Test successful"));
        assertTrue("Le message doit contenir le nom de l'hôpital", 
                response.getBody().contains("Hôpital Central"));

        verify(allocationService).findBestHospital(any(AllocationRequest.class));
    }

    @Test
    public void testTest_Failure() throws Exception {
        // Given
        when(allocationService.findBestHospital(any(AllocationRequest.class)))
                .thenThrow(new RuntimeException("Test failed"));

        // When
        ResponseEntity<String> response = allocationController.test();

        // Then
        assertNotNull("La réponse ne doit pas être null", response);
        assertEquals("Le statut HTTP doit être INTERNAL_SERVER_ERROR", 
                HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue("Le message doit contenir 'Test failed'", 
                response.getBody().contains("Test failed"));

        verify(allocationService).findBestHospital(any(AllocationRequest.class));
    }

    @Test
    public void testDebugHospitals_Success() throws Exception {
        // Given
        Hospital hospital1 = new Hospital("Hôpital Central", 53.3976314, -2.1829641, 
                "Manchester", "123 Main St", 5);
        hospital1.setId(1L);

        Hospital hospital2 = new Hospital("Hôpital Nord", 53.4808, -2.2426, 
                "Manchester", "456 Oak Ave", 3);
        hospital2.setId(2L);

        List<Hospital> hospitals = Arrays.asList(hospital1, hospital2);

        when(hospitalRepository.findBySpecialtyAndAvailableBeds("Cardiology"))
                .thenReturn(hospitals);

        // When
        ResponseEntity<String> response = allocationController.debugHospitals();

        // Then
        assertNotNull("La réponse ne doit pas être null", response);
        assertEquals("Le statut HTTP doit être OK", HttpStatus.OK, response.getStatusCode());
        assertTrue("Le message doit contenir le titre", 
                response.getBody().contains("Hôpitaux avec spécialité Cardiology"));
        assertTrue("Le message doit contenir le premier hôpital", 
                response.getBody().contains("Hôpital Central"));
        assertTrue("Le message doit contenir le deuxième hôpital", 
                response.getBody().contains("Hôpital Nord"));

        verify(hospitalRepository).findBySpecialtyAndAvailableBeds("Cardiology");
    }

    @Test
    public void testDebugHospitals_Exception() throws Exception {
        // Given
        when(hospitalRepository.findBySpecialtyAndAvailableBeds("Cardiology"))
                .thenThrow(new RuntimeException("Database error"));

        // When
        ResponseEntity<String> response = allocationController.debugHospitals();

        // Then
        assertNotNull("La réponse ne doit pas être null", response);
        assertEquals("Le statut HTTP doit être INTERNAL_SERVER_ERROR", 
                HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue("Le message doit contenir 'Error'", 
                response.getBody().contains("Error"));
        assertTrue("Le message doit contenir l'erreur", 
                response.getBody().contains("Database error"));

        verify(hospitalRepository).findBySpecialtyAndAvailableBeds("Cardiology");
    }
}
