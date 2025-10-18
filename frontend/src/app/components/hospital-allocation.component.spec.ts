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
    fixture.detectChanges();
    
    // Mock the automatic health check request from fixture.detectChanges()
    const healthReq1 = httpMock.expectOne('/api/health');
    healthReq1.flush('Service Unavailable', { status: 502, statusText: 'Bad Gateway' });

    expect(component.errorMessage).toBe('Backend service is not available. Please check that the server is started.');
  });
});
