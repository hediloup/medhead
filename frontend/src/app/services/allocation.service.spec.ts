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
      const mockHealthResponse = 'OK';

      service.checkHealth().subscribe(response => {
        expect(response).toBe(mockHealthResponse);
      });

      const req = httpMock.expectOne('/api/health');
      expect(req.request.method).toBe('GET');
      req.flush(mockHealthResponse);
    });

    it('should handle health check errors', () => {
      service.checkHealth().subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.message).toBe('Backend service temporarily unavailable. Please try again.');
        }
      });

      const req = httpMock.expectOne('/api/health');
      req.flush('Service Unavailable', { status: 502, statusText: 'Bad Gateway' });
    });
  });
});
