package com.medhead.poc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medhead.poc.model.AllocationRequest;
import com.medhead.poc.model.AllocationResponse;
import com.medhead.poc.service.AllocationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AllocationController.class)
class AllocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AllocationService allocationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testAllocateHospital_POST_ShouldReturnValidResponse() throws Exception {
        // Given
        AllocationRequest request = new AllocationRequest();
        request.setSpecialty("Cardiology");
        request.setLatitude(51.5074);
        request.setLongitude(-0.1278);

        AllocationResponse response = new AllocationResponse();
        response.setHospitalName("St George's Hospital");
        response.setHospitalId(1L);
        response.setDistanceKm(9.5);
        response.setSpecialty("Cardiology");
        response.setAvailableBeds(25);
        response.setEstimatedTimeMinutes(114);

        when(allocationService.allocateBestHospital(any(AllocationRequest.class)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/allocate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.hospitalName").value("St George's Hospital"))
                .andExpect(jsonPath("$.hospitalId").value(1))
                .andExpect(jsonPath("$.distanceKm").value(9.5))
                .andExpect(jsonPath("$.specialty").value("Cardiology"))
                .andExpect(jsonPath("$.availableBeds").value(25))
                .andExpect(jsonPath("$.estimatedTimeMinutes").value(114));
    }

    @Test
    @WithMockUser
    void testAllocateHospital_GET_ShouldReturnValidResponse() throws Exception {
        // Given
        AllocationResponse response = new AllocationResponse();
        response.setHospitalName("St George's Hospital");
        response.setHospitalId(1L);
        response.setDistanceKm(9.5);
        response.setSpecialty("Cardiology");
        response.setAvailableBeds(25);
        response.setEstimatedTimeMinutes(114);

        when(allocationService.allocateBestHospital(any(AllocationRequest.class)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/allocate")
                        .param("specialty", "Cardiology")
                        .param("latitude", "51.5074")
                        .param("longitude", "-0.1278"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.hospitalName").value("St George's Hospital"));
    }

    @Test
    @WithMockUser
    void testAllocateHospital_InvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Given
        AllocationRequest request = new AllocationRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/api/allocate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testAllocateHospital_ServiceException_ShouldReturnInternalServerError() throws Exception {
        // Given
        AllocationRequest request = new AllocationRequest();
        request.setSpecialty("Cardiology");
        request.setLatitude(51.5074);
        request.setLongitude(-0.1278);

        when(allocationService.allocateBestHospital(any(AllocationRequest.class)))
                .thenThrow(new RuntimeException("No available hospitals"));

        // When & Then
        mockMvc.perform(post("/api/allocate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testAllocateHospital_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // Given
        AllocationRequest request = new AllocationRequest();
        request.setSpecialty("Cardiology");
        request.setLatitude(51.5074);
        request.setLongitude(-0.1278);

        // When & Then
        mockMvc.perform(post("/api/allocate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void testHealthEndpoint_ShouldReturnOk() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Allocation API operational"));
    }
}
