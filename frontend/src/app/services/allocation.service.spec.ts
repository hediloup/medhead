import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AllocationService } from './allocation.service';
import { AllocationRequest } from '../models/allocation-request';
import { AllocationResponse } from '../models/allocation-response';

describe('AllocationService', () => {
  let service: AllocationService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AllocationService]
    });
    service = TestBed.inject(AllocationService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('allocateHospital', () => {
    it('should make a POST request to /api/allocate', () => {
      const mockRequest: AllocationRequest = {
        specialty: 'Cardiology',
        latitude: 48.8566,
        longitude: 2.3522
      };

      const mockResponse: AllocationResponse = {
        hospital_name: 'Hôpital Saint-Antoine',
        hospital_id: 1,
        distance_km: 5.2,
        specialty: 'Cardiology',
        available_beds: 3,
        estimated_time_minutes: 12
      };

      service.allocateHospital(mockRequest).subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne('/api/allocate');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockRequest);
      req.flush(mockResponse);
    });

    it('should handle HTTP errors properly', () => {
      const mockRequest: AllocationRequest = {
        specialty: 'Cardiology',
        latitude: 48.8566,
        longitude: 2.3522
      };

      service.allocateHospital(mockRequest).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.message).toBe('No hospital available for this specialty.');
        }
      });

      const req = httpMock.expectOne('/api/allocate');
      req.flush('Not Found', { status: 404, statusText: 'Not Found' });
    });

    it('should handle server errors (500)', () => {
      const mockRequest: AllocationRequest = {
        specialty: 'Neurology',
        latitude: 40.7128,
        longitude: -74.0060
      };

      service.allocateHospital(mockRequest).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.message).toBe('Server error. Please try again later.');
        }
      });

      const req = httpMock.expectOne('/api/allocate');
      req.flush('Internal Server Error', { status: 500, statusText: 'Internal Server Error' });
    });

    it('should handle connection errors', () => {
      const mockRequest: AllocationRequest = {
        specialty: 'Orthopedics',
        latitude: 51.5074,
        longitude: -0.1278
      };

      service.allocateHospital(mockRequest).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.message).toBe('Unable to contact the server. Check your connection.');
        }
      });

      const req = httpMock.expectOne('/api/allocate');
      req.flush('Connection Error', { status: 0, statusText: 'Unknown Error' });
    });
  });

  describe('checkHealth', () => {
    it('should make a GET request to /api/health', () => {
      console.log('🔍 Test: Service - Vérification de la santé de l\'API');
      const mockHealthResponse = 'OK';

      service.checkHealth().subscribe(response => {
        expect(response).toBe(mockHealthResponse);
        console.log('   ✅ Vérification de la santé réussie');
      });

      const req = httpMock.expectOne('/api/health');
      expect(req.request.method).toBe('GET');
      req.flush(mockHealthResponse);
    });

    it('should handle health check errors', () => {
      console.log('🔍 Test: Service - Gestion des erreurs de santé');
      service.checkHealth().subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.message).toBe('Backend service temporarily unavailable. Please try again.');
          console.log('   ✅ Erreur de santé gérée correctement');
        }
      });

      const req = httpMock.expectOne('/api/health');
      req.flush('Service Unavailable', { status: 502, statusText: 'Bad Gateway' });
    });
  });

  describe('handleError - Comprehensive Error Testing', () => {
    it('should handle client-side errors', () => {
      console.log('🔍 Test: Service - Erreurs côté client');
      const mockRequest: AllocationRequest = {
        specialty: 'Cardiology',
        latitude: 48.8566,
        longitude: 2.3522
      };

      service.allocateHospital(mockRequest).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.message).toContain('Error:');
          console.log('   ✅ Erreur côté client gérée');
        }
      });

      const req = httpMock.expectOne('/api/allocate');
      req.error(new ErrorEvent('Network error', { message: 'Connection failed' }));
    });

    it('should handle 400 Bad Request', () => {
      console.log('🔍 Test: Service - Erreur 400 Bad Request');
      const mockRequest: AllocationRequest = {
        specialty: 'Cardiology',
        latitude: 48.8566,
        longitude: 2.3522
      };

      service.allocateHospital(mockRequest).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.message).toBe('Invalid data. Please check your information.');
          console.log('   ✅ Erreur 400 gérée');
        }
      });

      const req = httpMock.expectOne('/api/allocate');
      req.flush('Bad Request', { status: 400, statusText: 'Bad Request' });
    });

    it('should handle 503 Service Unavailable', () => {
      console.log('🔍 Test: Service - Erreur 503 Service Unavailable');
      const mockRequest: AllocationRequest = {
        specialty: 'Cardiology',
        latitude: 48.8566,
        longitude: 2.3522
      };

      service.allocateHospital(mockRequest).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.message).toBe('Service temporarily unavailable. Please try again later.');
          console.log('   ✅ Erreur 503 gérée');
        }
      });

      const req = httpMock.expectOne('/api/allocate');
      req.flush('Service Unavailable', { status: 503, statusText: 'Service Unavailable' });
    });

    it('should handle 504 Gateway Timeout', () => {
      console.log('🔍 Test: Service - Erreur 504 Gateway Timeout');
      const mockRequest: AllocationRequest = {
        specialty: 'Cardiology',
        latitude: 48.8566,
        longitude: 2.3522
      };

      service.allocateHospital(mockRequest).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.message).toBe('Request timeout. The service is taking too long to respond. Please try again.');
          console.log('   ✅ Erreur 504 gérée');
        }
      });

      const req = httpMock.expectOne('/api/allocate');
      req.flush('Gateway Timeout', { status: 504, statusText: 'Gateway Timeout' });
    });

    it('should handle unknown status codes', () => {
      console.log('🔍 Test: Service - Codes de statut inconnus');
      const mockRequest: AllocationRequest = {
        specialty: 'Cardiology',
        latitude: 48.8566,
        longitude: 2.3522
      };

      service.allocateHospital(mockRequest).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.message).toContain('Error 418:');
          console.log('   ✅ Code de statut inconnu géré');
        }
      });

      const req = httpMock.expectOne('/api/allocate');
      req.flush('I\'m a teapot', { status: 418, statusText: 'I\'m a teapot' });
    });

    it('should handle browser extension errors gracefully', () => {
      console.log('🔍 Test: Service - Erreurs d\'extension de navigateur');
      const mockRequest: AllocationRequest = {
        specialty: 'Cardiology',
        latitude: 48.8566,
        longitude: 2.3522
      };

      service.allocateHospital(mockRequest).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.message).toBe('Error: runtime.lastError');
          console.log('   ✅ Erreur d\'extension gérée');
        }
      });

      const req = httpMock.expectOne('/api/allocate');
      const errorEvent = new ErrorEvent('Error', { message: 'runtime.lastError' });
      req.error(errorEvent);
    });
  });

  describe('checkHealth', () => {
    it('should make a GET request to /api/health', () => {
      console.log('🔍 Test: Service - Vérification de la santé de l\'API');
      service.checkHealth().subscribe(response => {
        expect(response).toBe('{"status":"UP"}');
        console.log('   ✅ Vérification de la santé réussie');
      });

      const req = httpMock.expectOne('/api/health');
      expect(req.request.method).toBe('GET');
      req.flush('{"status":"UP"}');
    });

    it('should handle health check errors', () => {
      console.log('🔍 Test: Service - Erreurs de vérification de la santé');
      service.checkHealth().subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.message).toBe('Backend service temporarily unavailable. Please try again.');
          console.log('   ✅ Erreur de santé gérée');
        }
      });

      const req = httpMock.expectOne('/api/health');
      req.flush('Service Unavailable', { status: 502, statusText: 'Bad Gateway' });
    });
  });
});
