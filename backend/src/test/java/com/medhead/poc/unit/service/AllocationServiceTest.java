package com.medhead.poc.unit.service;

import com.medhead.poc.dto.HospitalProjection;
import com.medhead.poc.model.AllocationRequest;
import com.medhead.poc.model.AllocationResponse;
import com.medhead.poc.model.Hospital;
import com.medhead.poc.model.Patient;
import com.medhead.poc.model.Speciality;
import com.medhead.poc.repository.HospitalRepository;
import com.medhead.poc.service.AllocationService;
import com.medhead.poc.service.DistanceCalculationService;
import com.medhead.poc.service.EventPublisherService;
import com.medhead.poc.service.PatientAnonymizationService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour AllocationService (sélection par distance Haversine)
 */
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

    @Mock
    private Counter allocationCounter;

    @Mock
    private Counter allocationErrorCounter;

    @Mock
    private Timer allocationTimer;

    @InjectMocks
    private AllocationService allocationService;

    private Hospital hospital1;
    private Hospital hospital2;
    private Speciality cardiology;
    private AllocationRequest validRequest;

    @Before
    public void setUp() {
        // Création des spécialités
        cardiology = new Speciality();
        cardiology.setName("Cardiology");

        // Création des hôpitaux
        hospital1 = new Hospital("Hôpital Central", 53.3976314, -2.1829641, "Manchester", "123 Main St", 5);
        hospital1.setId(1L);
        hospital1.setCity("Manchester");

        hospital2 = new Hospital("Hôpital Nord", 53.4808, -2.2426, "Manchester", "456 Oak Ave", 3);
        hospital2.setId(2L);
        hospital2.setCity("Manchester");

        // Ajout des spécialités aux hôpitaux
        Set<Speciality> specialities = new HashSet<>();
        specialities.add(cardiology);
        hospital1.setSpecialities(specialities);
        hospital2.setSpecialities(specialities);

        // Création d'une requête valide
        validRequest = new AllocationRequest("Cardiology", 53.3976314, -2.1829641);

        // Configuration des mocks pour PatientAnonymizationService
        Patient mockPatient = new Patient("Cardiology", 53.3976314, -2.1829641);
        mockPatient.setAnonymizedName("ANON123");
        mockPatient.setSeverityLevel("MEDIUM");
        mockPatient.setAgeGroup("ADULT");
        when(patientAnonymizationService.createAnonymizedPatient(anyString(), anyDouble(), anyDouble(), anyString()))
                .thenReturn(mockPatient);
        when(patientAnonymizationService.anonymizePatient(any(Patient.class)))
                .thenAnswer(invocation -> invocation.getArgument(0)); // Return the same patient

        // Configuration des mocks pour les métriques Micrometer
        try {
            when(allocationTimer.recordCallable(any())).thenAnswer(invocation -> {
                java.util.concurrent.Callable<?> callable = invocation.getArgument(0);
                try {
                    return callable.call();
                } catch (RuntimeException e) {
                    // Re-throw RuntimeException as-is (including IllegalArgumentException)
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Helper method to create a HospitalProjection mock from a Hospital
     */
    private HospitalProjection createProjection(final Hospital hospital) {
        return new HospitalProjection() {
            @Override
            public Long getId() {
                return hospital.getId();
            }

            @Override
            public String getName() {
                return hospital.getName();
            }

            @Override
            public String getSpecialty() {
                // Return the first specialty name (if any)
                return hospital.getSpecialities().stream()
                    .findFirst()
                    .map(s -> s.getName())
                    .orElse("");
            }

            @Override
            public Double getLatitude() {
                return hospital.getLatitude();
            }

            @Override
            public Double getLongitude() {
                return hospital.getLongitude();
            }

            @Override
            public Integer getAvailableBeds() {
                return hospital.getAvailableBeds();
            }
        };
    }

    @Test
    public void testFindBestHospital_Success() {
        // Given - Configuration des mocks
        System.out.println("🔍 Test: Service - Allocation réussie");
        HospitalProjection projection1 = createProjection(hospital1);
        HospitalProjection projection2 = createProjection(hospital2);
        List<HospitalProjection> eligibleProjections = Arrays.asList(projection1, projection2);
        System.out.println("   Hôpitaux éligibles: " + eligibleProjections.size());

        when(hospitalRepository.findBySpecialtyAndAvailableBedsProjection("Cardiology"))
                .thenReturn(eligibleProjections);

        // Mock des distances (hospital1 plus proche)
        when(distanceService.calculateDistanceToHospital(anyDouble(), anyDouble(), eq(hospital1))).thenReturn(5.2);
        when(distanceService.calculateDistanceToHospital(anyDouble(), anyDouble(), eq(hospital2))).thenReturn(8.1);
        System.out.println("   Distances mockées: Hôpital1=5.2km, Hôpital2=8.1km");

        // When - Exécution du test
        System.out.println("   Exécution de l'allocation");
        AllocationResponse response = allocationService.findBestHospital(validRequest);

        // Then - Vérifications
        System.out.println("   Vérification de la réponse");
        assertNotNull("La réponse ne doit pas être null", response);
        assertEquals("Le nom de l'hôpital doit être correct", "Hôpital Central", response.getHospitalName());
        assertEquals("L'ID de l'hôpital doit être correct", Long.valueOf(1L), response.getHospitalId());
        assertEquals("La distance doit être correcte", 5.2, response.getDistance().doubleValue(), 0.01);
        assertEquals("Le nombre de lits disponibles doit être correct", Integer.valueOf(4), response.getAvailableBedsAfterAllocation());
        // ETA approx à 50 km/h -> ~6 minutes
        assertEquals("Le temps estimé doit être correct", Integer.valueOf(6), response.getEstimatedTimeMinutes());
        System.out.println("   ✅ Test Service réussi - Allocation fonctionne");

        // Vérification des interactions avec les mocks
        verify(hospitalRepository).findBySpecialtyAndAvailableBedsProjection("Cardiology");
        verify(distanceService, times(1)).calculateDistanceToHospital(anyDouble(), anyDouble(), eq(hospital1));
        verify(distanceService, times(1)).calculateDistanceToHospital(anyDouble(), anyDouble(), eq(hospital2));
        verify(patientAnonymizationService).createAnonymizedPatient(anyString(), anyDouble(), anyDouble(), anyString());
        verify(eventPublisherService).publishBedReservedEvent(anyString(), anyString(), anyString(), 
                anyString(), anyString(), anyLong(), anyString(), anyString(), anyDouble(), anyInt(), anyInt());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindBestHospital_NullSpecialty() {
        // Given
        AllocationRequest requestWithNullSpecialty = new AllocationRequest(null, 53.3976314, -2.1829641);

        // When
        allocationService.findBestHospital(requestWithNullSpecialty);

        // Then - Exception attendue
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindBestHospital_EmptySpecialty() {
        // Given
        AllocationRequest requestWithEmptySpecialty = new AllocationRequest("", 53.3976314, -2.1829641);

        // When
        allocationService.findBestHospital(requestWithEmptySpecialty);

        // Then - Exception attendue
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindBestHospital_NullLatitude() {
        // Given
        AllocationRequest requestWithNullLatitude = new AllocationRequest("Cardiology", null, -2.1829641);

        // When
        allocationService.findBestHospital(requestWithNullLatitude);

        // Then - Exception attendue
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindBestHospital_NullLongitude() {
        // Given
        AllocationRequest requestWithNullLongitude = new AllocationRequest("Cardiology", 53.3976314, null);

        // When
        allocationService.findBestHospital(requestWithNullLongitude);

        // Then - Exception attendue
    }

    @Test(expected = RuntimeException.class)
    public void testFindBestHospital_NoEligibleHospitals() {
        // Given
        when(hospitalRepository.findBySpecialtyAndAvailableBedsProjection("Cardiology"))
                .thenReturn(Arrays.asList());

        // When
        allocationService.findBestHospital(validRequest);

        // Then - Exception attendue
    }

    @Test
    public void testFindBestHospital_SelectsNearestHospital() {
        // Given
        HospitalProjection projection1 = createProjection(hospital1);
        HospitalProjection projection2 = createProjection(hospital2);
        List<HospitalProjection> eligibleProjections = Arrays.asList(projection1, projection2);
        when(hospitalRepository.findBySpecialtyAndAvailableBedsProjection("Cardiology"))
                .thenReturn(eligibleProjections);

        // Mock: hospital2 plus proche
        when(distanceService.calculateDistanceToHospital(anyDouble(), anyDouble(), eq(hospital1))).thenReturn(10.0);
        when(distanceService.calculateDistanceToHospital(anyDouble(), anyDouble(), eq(hospital2))).thenReturn(3.0);

        // When
        AllocationResponse response = allocationService.findBestHospital(validRequest);

        // Then
        assertEquals("L'hôpital le plus proche doit être sélectionné", "Hôpital Nord", response.getHospitalName());
        assertEquals("Le temps estimé doit être basé sur la distance", Integer.valueOf(4), response.getEstimatedTimeMinutes()); // 3km -> ~4 min
    }
}
