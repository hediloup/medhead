package com.medhead.poc.service;

import com.medhead.poc.model.AllocationRequest;
import com.medhead.poc.model.AllocationResponse;
import com.medhead.poc.model.Hospital;
import com.medhead.poc.model.Speciality;
import com.medhead.poc.repository.HospitalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AllocationServiceTest {

    @Mock
    private HospitalRepository hospitalRepository;

    @InjectMocks
    private AllocationService allocationService;

    private Hospital hospital1;
    private Hospital hospital2;
    private Speciality cardiology;

    @BeforeEach
    void setUp() {
        cardiology = new Speciality();
        cardiology.setId(1L);
        cardiology.setName("Cardiology");

        hospital1 = new Hospital();
        hospital1.setId(1L);
        hospital1.setName("St George's Hospital");
        hospital1.setLatitude(51.4253);
        hospital1.setLongitude(-0.1780);
        hospital1.setAvailableBeds(25);
        hospital1.setCity("London");

        hospital2 = new Hospital();
        hospital2.setId(2L);
        hospital2.setName("King's College Hospital");
        hospital2.setLatitude(51.4686);
        hospital2.setLongitude(-0.0994);
        hospital2.setAvailableBeds(30);
        hospital2.setCity("London");
    }

    @Test
    void testAllocateBestHospital_ShouldReturnClosestHospital() {
        // Given
        AllocationRequest request = new AllocationRequest();
        request.setSpecialty("Cardiology");
        request.setLatitude(51.5074); // London center
        request.setLongitude(-0.1278);

        when(hospitalRepository.findBySpecialitiesNameAndAvailableBedsGreaterThan(anyString(), any()))
                .thenReturn(Arrays.asList(hospital1, hospital2));

        // When
        AllocationResponse response = allocationService.allocateBestHospital(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getHospitalName()).isEqualTo("King's College Hospital");
        assertThat(response.getSpecialty()).isEqualTo("Cardiology");
        assertThat(response.getAvailableBeds()).isEqualTo(30);
        assertThat(response.getDistanceKm()).isGreaterThan(0);
    }

    @Test
    void testAllocateBestHospital_NoAvailableHospitals_ShouldThrowException() {
        // Given
        AllocationRequest request = new AllocationRequest();
        request.setSpecialty("Cardiology");
        request.setLatitude(51.5074);
        request.setLongitude(-0.1278);

        when(hospitalRepository.findBySpecialitiesNameAndAvailableBedsGreaterThan(anyString(), any()))
                .thenReturn(Arrays.asList());

        // When & Then
        try {
            allocationService.allocateBestHospital(request);
            assertThat(false).as("Should have thrown an exception").isTrue();
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("No available hospitals found");
        }
    }

    @Test
    void testAllocateBestHospital_InvalidCoordinates_ShouldHandleGracefully() {
        // Given
        AllocationRequest request = new AllocationRequest();
        request.setSpecialty("Cardiology");
        request.setLatitude(999.0); // Invalid latitude
        request.setLongitude(999.0); // Invalid longitude

        when(hospitalRepository.findBySpecialitiesNameAndAvailableBedsGreaterThan(anyString(), any()))
                .thenReturn(Arrays.asList(hospital1));

        // When
        AllocationResponse response = allocationService.allocateBestHospital(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getDistanceKm()).isGreaterThan(0);
    }

    @Test
    void testCalculateDistance_ShouldReturnCorrectDistance() {
        // Given
        double lat1 = 51.5074, lon1 = -0.1278; // London
        double lat2 = 51.4253, lon2 = -0.1780; // St George's Hospital

        // When
        double distance = allocationService.calculateDistance(lat1, lon1, lat2, lon2);

        // Then
        assertThat(distance).isCloseTo(9.5, org.assertj.core.data.Offset.offset(1.0));
    }

    @Test
    void testAllocateBestHospital_MultipleHospitalsSameDistance_ShouldReturnFirst() {
        // Given
        AllocationRequest request = new AllocationRequest();
        request.setSpecialty("Cardiology");
        request.setLatitude(51.5074);
        request.setLongitude(-0.1278);

        // Create hospitals at the same distance
        Hospital hospital3 = new Hospital();
        hospital3.setId(3L);
        hospital3.setName("Same Distance Hospital");
        hospital3.setLatitude(51.5074);
        hospital3.setLongitude(-0.1278);
        hospital3.setAvailableBeds(15);
        hospital3.setCity("London");

        when(hospitalRepository.findBySpecialitiesNameAndAvailableBedsGreaterThan(anyString(), any()))
                .thenReturn(Arrays.asList(hospital1, hospital3));

        // When
        AllocationResponse response = allocationService.allocateBestHospital(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getDistanceKm()).isCloseTo(0.0, org.assertj.core.data.Offset.offset(0.1));
    }
}
