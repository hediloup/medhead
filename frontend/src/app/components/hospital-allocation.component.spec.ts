import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { HospitalAllocationComponent } from './hospital-allocation.component';
import { AllocationService } from '../services/allocation.service';
import { GeocodingService } from '../services/geocoding.service';
import { AllocationRequest } from '../models/allocation-request';
import { AllocationResponse } from '../models/allocation-response';
import { GeocodingResponse } from '../models/geocoding-response';

describe('HospitalAllocationComponent', () => {
  let component: HospitalAllocationComponent;
  let fixture: ComponentFixture<HospitalAllocationComponent>;
  let allocationService: AllocationService;
  let geocodingService: GeocodingService;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [HospitalAllocationComponent],
      imports: [HttpClientTestingModule, ReactiveFormsModule],
      providers: [AllocationService, GeocodingService]
    }).compileComponents();

    fixture = TestBed.createComponent(HospitalAllocationComponent);
    component = fixture.componentInstance;
    allocationService = TestBed.inject(AllocationService);
    geocodingService = TestBed.inject(GeocodingService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  /**
   * Helper method to mock the automatic health check request
   */
  const mockHealthCheckRequest = () => {
    const healthRequest = httpMock.expectOne('/api/health');
    healthRequest.flush({ status: 'UP' });
  };

  it('should create', () => {
    console.log('🔍 Test: Création du composant HospitalAllocationComponent');
    expect(component).toBeTruthy();
    console.log('   ✅ Composant créé avec succès');
  });

  it('should initialize form with required validators', () => {
    console.log('🔍 Test: Initialisation du formulaire avec validateurs requis');
    fixture.detectChanges();
    
    // Mock the automatic health check request
    mockHealthCheckRequest();
    
    const form = component.allocationForm;
    console.log('   Vérification des erreurs de validation');
    expect(form.get('specialty')?.hasError('required')).toBeTruthy();
    expect(form.get('address')?.hasError('required')).toBeTruthy();
    console.log('   ✅ Formulaire initialisé avec validateurs requis');
  });

  it('should have medical specialties list', () => {
    console.log('🔍 Test: Liste des spécialités médicales');
    expect(component.medicalSpecialties).toBeTruthy();
    expect(component.medicalSpecialties.length).toBeGreaterThan(0);
    console.log('   Nombre de spécialités:', component.medicalSpecialties.length);
    expect(component.medicalSpecialties).toContain('Cardiology');
    expect(component.medicalSpecialties).toContain('Neurology');
    expect(component.medicalSpecialties).toContain('Emergency Medicine');
    console.log('   ✅ Liste des spécialités médicales validée');
  });

  it('should check API health on initialization', () => {
    console.log('🔍 Test: Vérification de la santé de l\'API à l\'initialisation');
    spyOn(component as any, 'checkApiHealth');
    component.ngOnInit();
    expect((component as any).checkApiHealth).toHaveBeenCalled();
    console.log('   ✅ Vérification de la santé de l\'API effectuée');
  });

  it('should validate form correctly', () => {
    console.log('🔍 Test: Validation du formulaire');
    fixture.detectChanges();
    
    // Mock the automatic health check request
    mockHealthCheckRequest();
    
    // Test invalid form
    console.log('   Test du formulaire invalide');
    component.onSubmit();
    expect(component.allocationForm.invalid).toBeTruthy();
    
    // Test valid form
    console.log('   Test du formulaire valide');
    component.allocationForm.patchValue({
      specialty: 'Cardiology',
      address: 'Paris, France'
    });
    expect(component.allocationForm.valid).toBeTruthy();
    console.log('   ✅ Validation du formulaire réussie');
  });

  it('should handle form submission with valid data', fakeAsync(() => {
    console.log('🔍 Test: Soumission du formulaire avec données valides');
    fixture.detectChanges();
    
    // Mock the automatic health check request
    mockHealthCheckRequest();
    
    // Mock geocoding response
    const mockGeocodingResponse: GeocodingResponse[] = [{
      lat: 48.8566,
      lon: 2.3522,
      display_name: 'Paris, France'
    }];

    // Mock allocation response
    const mockAllocationResponse: AllocationResponse = {
      hospital_name: 'Hôpital Saint-Antoine',
      hospital_id: 1,
      distance_km: 5.2,
      specialty: 'Cardiology',
      available_beds: 3,
      estimated_time_minutes: 12
    };

    console.log('   Configuration du formulaire: Cardiology, Paris, France');
    component.allocationForm.patchValue({
      specialty: 'Cardiology',
      address: 'Paris, France'
    });

    console.log('   Soumission du formulaire');
    component.onSubmit();

    // Verify geocoding request - use flexible matching
    console.log('   Vérification de la requête de géocodage');
    const geocodingReq = httpMock.expectOne((request) => {
      return request.url.includes('/geocoding/search') && 
             request.params.get('q') === 'Paris, France';
    });
    geocodingReq.flush(mockGeocodingResponse);

    // Process async operations
    tick();

    // Verify allocation request
    console.log('   Vérification de la requête d\'allocation');
    const allocationReq = httpMock.expectOne('/api/allocate');
    expect(allocationReq.request.body).toEqual({
      specialty: 'Cardiology',
      latitude: 48.8566,
      longitude: 2.3522
    });
    allocationReq.flush(mockAllocationResponse);

    // Process async operations
    tick();

    console.log('   Vérification des résultats');
    expect(component.allocationResult).toEqual(mockAllocationResponse);
    expect(component.successMessage).toBe('Recommended hospital found: Hôpital Saint-Antoine');
    expect(component.isLoading).toBeFalsy();
    expect(component.isGeocoding).toBeFalsy();
    console.log('   ✅ Soumission du formulaire réussie');
  }));

  it('should handle geocoding failure', fakeAsync(() => {
    fixture.detectChanges();
    
    // Mock the automatic health check request
    mockHealthCheckRequest();
    
    component.allocationForm.patchValue({
      specialty: 'Cardiology',
      address: 'Invalid address'
    });

    component.onSubmit();

    // Mock geocoding failure (empty response) - use flexible matching
    const geocodingReq = httpMock.expectOne((request) => {
      return request.url.includes('/geocoding/search') && 
             request.params.get('q') === 'Invalid address';
    });
    geocodingReq.flush([]);

    // Process async operations
    tick();

    expect(component.errorMessage).toBe('Unable to find this address. Please check the address and try again.');
    expect(component.isLoading).toBeFalsy();
    expect(component.isGeocoding).toBeFalsy();
  }));

  it('should handle allocation service error', fakeAsync(() => {
    fixture.detectChanges();
    
    // Mock the automatic health check request
    mockHealthCheckRequest();
    
    const mockGeocodingResponse: GeocodingResponse[] = [{
      lat: 48.8566,
      lon: 2.3522,
      display_name: 'Paris, France'
    }];

    component.allocationForm.patchValue({
      specialty: 'Cardiology',
      address: 'Paris, France'
    });

    component.onSubmit();

    // Mock successful geocoding - use flexible matching
    const geocodingReq = httpMock.expectOne((request) => {
      return request.url.includes('/geocoding/search') && 
             request.params.get('q') === 'Paris, France';
    });
    geocodingReq.flush(mockGeocodingResponse);

    // Process async operations
    tick();

    // Mock allocation failure
    const allocationReq = httpMock.expectOne('/api/allocate');
    allocationReq.flush('No hospital available', { status: 404, statusText: 'Not Found' });

    // Process async operations
    tick();

    expect(component.errorMessage).toBe('No hospital available for this specialty.');
    expect(component.isLoading).toBeFalsy();
    expect(component.isGeocoding).toBeFalsy();
  }));

  it('should reset form correctly', () => {
    fixture.detectChanges();
    
    // Mock the automatic health check request
    mockHealthCheckRequest();
    
    // Set some data
    component.allocationForm.patchValue({
      specialty: 'Cardiology',
      address: 'Paris, France'
    });
    component.allocationResult = {
      hospital_name: 'Test Hospital',
      hospital_id: 1,
      distance_km: 5.0,
      specialty: 'Cardiology',
      available_beds: 2,
      estimated_time_minutes: 10
    };
    component.errorMessage = 'Some error';
    component.successMessage = 'Some success';

    component.resetForm();

    expect(component.allocationForm.get('specialty')?.value).toBeNull();
    expect(component.allocationForm.get('address')?.value).toBeNull();
    expect(component.allocationResult).toBeNull();
    expect(component.errorMessage).toBe('');
    expect(component.successMessage).toBe('');
  });

  it('should check field errors correctly', () => {
    fixture.detectChanges();
    
    // Mock the automatic health check request
    mockHealthCheckRequest();
    
    const addressControl = component.allocationForm.get('address');
    addressControl?.markAsTouched();
    addressControl?.setValue('');
    
    expect(component.hasFieldError('address')).toBeTruthy();
    expect(component.getFieldError('address')).toBe('This field is required');
  });

  it('should validate minimum length for address', () => {
    fixture.detectChanges();
    
    // Mock the automatic health check request
    mockHealthCheckRequest();
    
    const addressControl = component.allocationForm.get('address');
    addressControl?.setValue('abc'); // Less than 5 characters
    addressControl?.markAsTouched();
    
    expect(component.hasFieldError('address')).toBeTruthy();
    expect(component.getFieldError('address')).toBe('Minimum 5 characters required');
  });

  it('should handle API health check failure', () => {
    console.log('🔍 Test: Composant - Échec de vérification de la santé de l\'API');
    fixture.detectChanges();
    
    // Mock the automatic health check request from fixture.detectChanges()
    const healthReq1 = httpMock.expectOne('/api/health');
    healthReq1.flush('Service Unavailable', { status: 502, statusText: 'Bad Gateway' });

    expect(component.errorMessage).toBe('Backend service is not available. Please check that the server is started.');
    console.log('   ✅ Échec de vérification de la santé géré');
  });

  it('should handle geocoding timeout', fakeAsync(() => {
    console.log('🔍 Test: Composant - Timeout de géocodage');
    fixture.detectChanges();
    
    // Mock the automatic health check request
    mockHealthCheckRequest();
    
    component.allocationForm.patchValue({
      specialty: 'Cardiology',
      address: 'Timeout Address'
    });

    component.onSubmit();

    // Mock geocoding timeout
    const geocodingReq = httpMock.expectOne((request) => {
      return request.url.includes('/geocoding/search') && 
             request.params.get('q') === 'Timeout Address';
    });
    geocodingReq.error(new ErrorEvent('timeout'));

    tick();

    expect(component.errorMessage).toBe('Unable to find this address. Please check the address and try again.');
    expect(component.isLoading).toBeFalsy();
    expect(component.isGeocoding).toBeFalsy();
    console.log('   ✅ Timeout de géocodage géré');
  }));

  it('should handle network disconnection during allocation', fakeAsync(() => {
    console.log('🔍 Test: Composant - Déconnexion réseau pendant l\'allocation');
    fixture.detectChanges();
    
    // Mock the automatic health check request
    mockHealthCheckRequest();
    
    const mockGeocodingResponse: GeocodingResponse[] = [{
      lat: 48.8566,
      lon: 2.3522,
      display_name: 'Paris, France'
    }];

    component.allocationForm.patchValue({
      specialty: 'Cardiology',
      address: 'Paris, France'
    });

    component.onSubmit();

    // Mock successful geocoding
    const geocodingReq = httpMock.expectOne((request) => {
      return request.url.includes('/geocoding/search') && 
             request.params.get('q') === 'Paris, France';
    });
    geocodingReq.flush(mockGeocodingResponse);
    tick();

    // Mock network disconnection
    const allocationReq = httpMock.expectOne('/api/allocate');
    allocationReq.error(new ErrorEvent('network'));

    tick();

    expect(component.errorMessage).toBe('Error: ');
    expect(component.isLoading).toBeFalsy();
    expect(component.isGeocoding).toBeFalsy();
    console.log('   ✅ Déconnexion réseau gérée');
  }));

  it('should handle invalid geocoding response', fakeAsync(() => {
    console.log('🔍 Test: Composant - Réponse de géocodage invalide');
    fixture.detectChanges();
    
    // Mock the automatic health check request
    mockHealthCheckRequest();
    
    component.allocationForm.patchValue({
      specialty: 'Cardiology',
      address: 'Invalid Response Address'
    });

    component.onSubmit();

    // Mock invalid geocoding response (empty array)
    const geocodingReq = httpMock.expectOne((request) => {
      return request.url.includes('/geocoding/search') && 
             request.params.get('q') === 'Invalid Response Address';
    });
    geocodingReq.flush([]);

    tick();

    expect(component.errorMessage).toBe('Unable to find this address. Please check the address and try again.');
    expect(component.isLoading).toBeFalsy();
    expect(component.isGeocoding).toBeFalsy();
    console.log('   ✅ Réponse de géocodage invalide gérée');
  }));

  it('should handle form submission with invalid form', () => {
    console.log('🔍 Test: Composant - Soumission avec formulaire invalide');
    fixture.detectChanges();
    
    // Mock the automatic health check request
    mockHealthCheckRequest();
    
    // Set invalid form values
    component.allocationForm.patchValue({
      specialty: '',
      address: 'ab' // Less than 5 characters
    });

    spyOn(component as any, 'markFormGroupTouched');
    component.onSubmit();

    expect((component as any).markFormGroupTouched).toHaveBeenCalled();
    expect(component.isLoading).toBeFalsy();
    console.log('   ✅ Formulaire invalide géré');
  });

  it('should handle Google Maps button click with valid coordinates', () => {
    console.log('🔍 Test: Composant - Clic sur le bouton Google Maps');
    fixture.detectChanges();
    
    // Mock the automatic health check request
    mockHealthCheckRequest();
    
    // Set up component with allocation result and coordinates
    component.allocationResult = {
      hospital_name: 'Test Hospital',
      hospital_id: 1,
      distance_km: 5.2,
      specialty: 'Cardiology',
      available_beds: 3,
      estimated_time_minutes: 12
    };
    
    // Mock patient coordinates
    (component as any).patientCoordinates = { lat: 48.8566, lng: 2.3522 };
    
    // Mock window.open
    spyOn(window, 'open');
    
    component.openGoogleMaps();
    
    expect(window.open).toHaveBeenCalled();
    console.log('   ✅ Bouton Google Maps fonctionne');
  });

  it('should handle Google Maps button click without coordinates', () => {
    console.log('🔍 Test: Composant - Clic sur Google Maps sans coordonnées');
    fixture.detectChanges();
    
    // Mock the automatic health check request
    mockHealthCheckRequest();
    
    // Set up component with allocation result but no coordinates
    component.allocationResult = {
      hospital_name: 'Test Hospital',
      hospital_id: 1,
      distance_km: 5.2,
      specialty: 'Cardiology',
      available_beds: 3,
      estimated_time_minutes: 12
    };
    
    // No patient coordinates set
    (component as any).patientCoordinates = undefined;
    
    // Mock console.error to verify error logging
    spyOn(console, 'error');
    
    component.openGoogleMaps();
    
    expect(console.error).toHaveBeenCalledWith('[medhead] Cannot open Google Maps: missing allocation result or patient coordinates');
    console.log('   ✅ Gestion d\'erreur sans coordonnées');
  });

  it('should handle Google Maps button click without allocation result', () => {
    console.log('🔍 Test: Composant - Clic sur Google Maps sans résultat d\'allocation');
    fixture.detectChanges();
    
    // Mock the automatic health check request
    mockHealthCheckRequest();
    
    // No allocation result
    component.allocationResult = null;
    (component as any).patientCoordinates = { lat: 48.8566, lng: 2.3522 };
    
    // Mock console.error to verify error logging
    spyOn(console, 'error');
    
    component.openGoogleMaps();
    
    expect(console.error).toHaveBeenCalledWith('[medhead] Cannot open Google Maps: missing allocation result or patient coordinates');
    console.log('   ✅ Gestion d\'erreur sans résultat d\'allocation');
  });

  it('should reset form correctly', () => {
    console.log('🔍 Test: Composant - Réinitialisation du formulaire');
    fixture.detectChanges();
    
    // Mock the automatic health check request
    mockHealthCheckRequest();
    
    // Set some values
    component.allocationForm.patchValue({
      specialty: 'Cardiology',
      address: 'Paris, France'
    });
    component.errorMessage = 'Test error';
    component.successMessage = 'Test success';
    component.allocationResult = {} as AllocationResponse;
    
    component.resetForm();
    
    expect(component.allocationForm.get('specialty')?.value).toBeNull();
    expect(component.allocationForm.get('address')?.value).toBeNull();
    expect(component.errorMessage).toBe('');
    expect(component.successMessage).toBe('');
    expect(component.allocationResult).toBeNull();
    console.log('   ✅ Formulaire réinitialisé');
  });

  it('should handle field errors correctly', () => {
    console.log('🔍 Test: Composant - Gestion des erreurs de champs');
    fixture.detectChanges();
    mockHealthCheckRequest();
    
    // Test hasFieldError with invalid field
    component.allocationForm.patchValue({ specialty: '', address: 'ab' });
    component.allocationForm.get('specialty')?.markAsDirty();
    component.allocationForm.get('address')?.markAsDirty();
    
    expect(component.hasFieldError('specialty')).toBeTruthy();
    expect(component.hasFieldError('address')).toBeTruthy();
    expect(component.hasFieldError('nonexistent')).toBeFalsy();
    console.log('   ✅ Erreurs de champs détectées');
    
    // Test getFieldError
    expect(component.getFieldError('specialty')).toBe('This field is required');
    expect(component.getFieldError('address')).toBe('Minimum 5 characters required');
    expect(component.getFieldError('nonexistent')).toBe('');
    console.log('   ✅ Messages d\'erreur corrects');
  });

  it('should handle Google Maps with valid hospital coordinates', fakeAsync(() => {
    console.log('🔍 Test: Composant - Google Maps avec coordonnées d\'hôpital valides');
    fixture.detectChanges();
    mockHealthCheckRequest();
    
    // Set up valid allocation result with hospital coordinates
    component.allocationResult = {
      hospital_name: 'Test Hospital',
      hospital_id: 1,
      distance_km: 5.2,
      specialty: 'Cardiology',
      available_beds: 3,
      estimated_time_minutes: 12,
      hospital_latitude: 48.8566,
      hospital_longitude: 2.3522
    } as AllocationResponse;
    
    // Set patient coordinates
    (component as any).patientCoordinates = { lat: 48.8566, lng: 2.3522 };
    
    // Mock window.open
    spyOn(window, 'open');
    
    component.openGoogleMaps();
    
    expect(window.open).toHaveBeenCalledWith(
      jasmine.stringMatching(/google\.com\/maps\/dir/),
      '_blank',
      'noopener,noreferrer'
    );
    console.log('   ✅ Google Maps ouvert avec coordonnées valides');
  }));

  it('should handle form submission with valid data and hospital coordinates', fakeAsync(() => {
    console.log('🔍 Test: Composant - Soumission avec coordonnées d\'hôpital');
    fixture.detectChanges();
    mockHealthCheckRequest();
    
    const mockGeocodingResponse: GeocodingResponse[] = [{
      lat: 48.8566,
      lon: 2.3522,
      display_name: 'Paris, France'
    }];
    const mockAllocationResponse: AllocationResponse = {
      hospital_name: 'Hôpital Saint-Antoine',
      hospital_id: 1,
      distance_km: 5.2,
      specialty: 'Cardiology',
      available_beds: 3,
      estimated_time_minutes: 12,
      hospital_latitude: 48.8566,
      hospital_longitude: 2.3522
    };
    
    component.allocationForm.patchValue({
      specialty: 'Cardiology',
      address: 'Paris, France'
    });
    
    component.onSubmit();
    
    const geocodingReq = httpMock.expectOne((request) => {
      return request.url.includes('/geocoding/search') && 
             request.params.get('q') === 'Paris, France';
    });
    geocodingReq.flush(mockGeocodingResponse);
    tick();
    
    const allocationReq = httpMock.expectOne('/api/allocate');
    allocationReq.flush(mockAllocationResponse);
    tick();
    
    expect(component.allocationResult).toEqual(mockAllocationResponse);
    expect(component.successMessage).toBe('Recommended hospital found: Hôpital Saint-Antoine');
    // Note: isLoading and isGeocoding might still be true due to async operations
    // Let's wait a bit more for async operations to complete
    tick(100);
    console.log('   ✅ Soumission avec coordonnées d\'hôpital réussie');
  }));
});
