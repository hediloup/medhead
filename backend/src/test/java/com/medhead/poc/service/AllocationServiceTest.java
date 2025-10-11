package com.medhead.poc.service;

import com.medhead.poc.model.AllocationRequest;
import com.medhead.poc.model.AllocationResponse;
import com.medhead.poc.model.Hospital;
import com.medhead.poc.model.Patient;
import com.medhead.poc.model.Speciality;
import com.medhead.poc.repository.HospitalRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AllocationServiceTest {

    @Mock
    private HospitalRepository hospitalRepository;

    @Mock
    private DistanceCalculationService distanceService;

    @Mock
    private PatientAnonymizationService patientAnonymizationService;

    @Mock
    private EventPublisherService eventPublisherService;

    @InjectMocks
    private AllocationService allocationService;

    private Hospital hospital1;
    private Hospital hospital2;
    private Speciality cardiology;

    @Before
    public void setUp() {
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
        hospital1.setSpecialities(Collections.singleton(cardiology));

        hospital2 = new Hospital();
        hospital2.setId(2L);
        hospital2.setName("King's College Hospital");
        hospital2.setLatitude(51.4686);
        hospital2.setLongitude(-0.0994);
        hospital2.setAvailableBeds(30);
        hospital2.setCity("London");
        hospital2.setSpecialities(Collections.singleton(cardiology));
    }

    @Test
    public void testFindBestHospital_ShouldReturnClosestHospital() {
        // Given
        AllocationRequest request = new AllocationRequest("Cardiology", 51.5074, -0.1278);

        when(hospitalRepository.findBySpecialtyAndAvailableBeds("Cardiology"))
                .thenReturn(Arrays.asList(hospital1, hospital2));

        // Mock distance calculations - hospital2 is closer
        when(distanceService.calculateDistanceToHospital(anyDouble(), anyDouble(), eq(hospital1)))
                .thenReturn(10.0);
        when(distanceService.calculateDistanceToHospital(anyDouble(), anyDouble(), eq(hospital2)))
                .thenReturn(5.0);

        when(distanceService.estimateTravelTime(anyDouble())).thenReturn(120);

        // Mock patient creation
        Patient mockPatient = new Patient();
        mockPatient.setPatientUuid("test-uuid");
        mockPatient.setAnonymizedName("PATIENT_TEST");
        mockPatient.setSeverityLevel("MEDIUM");
        mockPatient.setAgeGroup("19-65");
        when(patientAnonymizationService.createAnonymizedPatient(anyString(), anyDouble(), anyDouble(), anyString()))
                .thenReturn(mockPatient);

        // When
        AllocationResponse response = allocationService.findBestHospital(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getHospitalName()).isEqualTo("King's College Hospital");
        assertThat(response.getSpecialty()).isEqualTo("Cardiology");
        assertThat(response.getAvailableBeds()).isEqualTo(29); // 30 - 1 (reserved)
        assertThat(response.getDistanceKm()).isEqualTo(5.0);
        assertThat(response.getEstimatedTimeMinutes()).isEqualTo(120);

        verify(hospitalRepository, times(1)).findBySpecialtyAndAvailableBeds("Cardiology");
        verify(patientAnonymizationService, times(1)).createAnonymizedPatient(anyString(), anyDouble(), anyDouble(), anyString());
        verify(eventPublisherService, times(1)).publishBedReservedEvent(anyString(), anyString(), anyString(), anyString(), anyString(), anyLong(), anyString(), anyString(), anyDouble(), anyInt(), anyInt());
    }

    @Test
    public void testFindBestHospital_NoAvailableHospitals_ShouldThrowException() {
        // Given
        AllocationRequest request = new AllocationRequest("Cardiology", 51.5074, -0.1278);

        when(hospitalRepository.findBySpecialtyAndAvailableBeds("Cardiology"))
                .thenReturn(Collections.emptyList());

        // When & Then
        assertThatThrownBy(() -> allocationService.findBestHospital(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No hospital available with specialty 'Cardiology'");

        verify(hospitalRepository, times(1)).findBySpecialtyAndAvailableBeds("Cardiology");
        verify(patientAnonymizationService, never()).createAnonymizedPatient(anyString(), anyDouble(), anyDouble(), anyString());
    }

    @Test
    public void testFindBestHospital_InvalidSpecialty_ShouldThrowException() {
        // Given
        AllocationRequest request = new AllocationRequest(null, 51.5074, -0.1278);

        // When & Then
        assertThatThrownBy(() -> allocationService.findBestHospital(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Specialty is required");

        verify(hospitalRepository, never()).findBySpecialtyAndAvailableBeds(anyString());
    }

    @Test
    public void testFindBestHospital_InvalidCoordinates_ShouldThrowException() {
        // Given
        AllocationRequest request = new AllocationRequest("Cardiology", null, -0.1278);

        // When & Then
        assertThatThrownBy(() -> allocationService.findBestHospital(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Geolocation is required");

        verify(hospitalRepository, never()).findBySpecialtyAndAvailableBeds(anyString());
    }

    @Test
    public void testFindBestHospital_MultipleHospitals_ShouldReturnClosest() {
        // Given
        AllocationRequest request = new AllocationRequest("Cardiology", 51.5074, -0.1278);

        when(hospitalRepository.findBySpecialtyAndAvailableBeds("Cardiology"))
                .thenReturn(Arrays.asList(hospital1, hospital2));

        // Mock distance calculations - hospital1 is closer
        when(distanceService.calculateDistanceToHospital(anyDouble(), anyDouble(), eq(hospital1)))
                .thenReturn(3.0);
        when(distanceService.calculateDistanceToHospital(anyDouble(), anyDouble(), eq(hospital2)))
                .thenReturn(7.0);

        when(distanceService.estimateTravelTime(anyDouble())).thenReturn(90);

        // Mock patient creation
        Patient mockPatient = new Patient();
        mockPatient.setPatientUuid("test-uuid");
        mockPatient.setAnonymizedName("PATIENT_TEST");
        mockPatient.setSeverityLevel("MEDIUM");
        mockPatient.setAgeGroup("19-65");
        when(patientAnonymizationService.createAnonymizedPatient(anyString(), anyDouble(), anyDouble(), anyString()))
                .thenReturn(mockPatient);

        // When
        AllocationResponse response = allocationService.findBestHospital(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getHospitalName()).isEqualTo("St George's Hospital");
        assertThat(response.getDistanceKm()).isEqualTo(3.0);
        assertThat(response.getAvailableBeds()).isEqualTo(24); // 25 - 1 (reserved)
    }
}
