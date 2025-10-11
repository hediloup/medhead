package com.medhead.poc.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medhead.poc.model.AllocationRequest;
import com.medhead.poc.model.AllocationResponse;
import com.medhead.poc.repository.HospitalRepository;
import com.medhead.poc.repository.SpecialityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class AllocationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private SpecialityRepository specialityRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // This will use the test data from the database initialization
        // The database should be populated with test data via @Sql or test profiles
    }

    @Test
    @WithMockUser
    void testAllocationFlow_WithRealDatabase() throws Exception {
        // Given
        AllocationRequest request = new AllocationRequest();
        request.setSpecialty("Cardiology");
        request.setLatitude(51.5074); // London center
        request.setLongitude(-0.1278);

        // Verify test data exists
        assertThat(specialityRepository.findByName("Cardiology")).isPresent();
        assertThat(hospitalRepository.count()).isGreaterThan(0);

        // When
        String responseJson = mockMvc.perform(post("/api/allocate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.hospitalName").isNotEmpty())
                .andExpect(jsonPath("$.hospitalId").isNumber())
                .andExpect(jsonPath("$.distanceKm").isNumber())
                .andExpect(jsonPath("$.specialty").value("Cardiology"))
                .andExpect(jsonPath("$.availableBeds").isNumber())
                .andExpect(jsonPath("$.estimatedTimeMinutes").isNumber())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Then
        AllocationResponse response = objectMapper.readValue(responseJson, AllocationResponse.class);
        assertThat(response.getHospitalName()).isNotNull();
        assertThat(response.getDistanceKm()).isGreaterThan(0);
        assertThat(response.getAvailableBeds()).isGreaterThan(0);
        assertThat(response.getEstimatedTimeMinutes()).isGreaterThan(0);
    }

    @Test
    @WithMockUser
    void testAllocationFlow_GET_WithRealDatabase() throws Exception {
        // When
        mockMvc.perform(get("/api/allocate")
                        .param("specialty", "Cardiology")
                        .param("latitude", "51.5074")
                        .param("longitude", "-0.1278"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.hospitalName").isNotEmpty())
                .andExpect(jsonPath("$.specialty").value("Cardiology"));
    }

    @Test
    @WithMockUser
    void testAllocationFlow_NonExistentSpecialty_ShouldReturnError() throws Exception {
        // Given
        AllocationRequest request = new AllocationRequest();
        request.setSpecialty("NonExistentSpecialty");
        request.setLatitude(51.5074);
        request.setLongitude(-0.1278);

        // When & Then
        mockMvc.perform(post("/api/allocate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser
    void testAllocationFlow_InvalidCoordinates_ShouldHandleGracefully() throws Exception {
        // Given
        AllocationRequest request = new AllocationRequest();
        request.setSpecialty("Cardiology");
        request.setLatitude(999.0); // Invalid latitude
        request.setLongitude(999.0); // Invalid longitude

        // When & Then
        mockMvc.perform(post("/api/allocate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // Should still work but with large distance
                .andExpect(jsonPath("$.distanceKm").isNumber());
    }

    @Test
    void testHealthEndpoint_Integration() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Allocation API operational"));
    }

    @Test
    @WithMockUser
    void testDatabaseConnection_ShouldBeAvailable() throws Exception {
        // Verify database connectivity through repository
        assertThat(hospitalRepository).isNotNull();
        assertThat(specialityRepository).isNotNull();
        
        // Test basic repository operations
        assertThat(hospitalRepository.count()).isGreaterThanOrEqualTo(0);
        assertThat(specialityRepository.count()).isGreaterThanOrEqualTo(0);
    }
}
