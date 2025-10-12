import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { GeocodingService } from './geocoding.service';
import { GeocodingResponse } from '../models/geocoding-response';

describe('GeocodingService', () => {
  let service: GeocodingService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [GeocodingService]
    });
    service = TestBed.inject(GeocodingService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('geocodeAddress', () => {
    it('should make a GET request to /geocoding/search with correct parameters', () => {
      const address = 'Paris, France';
      const mockResponse: GeocodingResponse[] = [{
        lat: 48.8566,
        lon: 2.3522,
        display_name: 'Paris, Île-de-France, France',
        address: {
          city: 'Paris',
          country: 'France',
          postcode: '75001'
        }
      }];

      service.geocodeAddress(address).subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(req => 
        req.url === '/geocoding/search' &&
        req.params.get('q') === address &&
        req.params.get('format') === 'json' &&
        req.params.get('limit') === '1' &&
        req.params.get('addressdetails') === '1'
      );
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });

    it('should handle empty response', () => {
      const address = 'Non-existent place';
      const mockResponse: GeocodingResponse[] = [];

      service.geocodeAddress(address).subscribe(response => {
        expect(response).toEqual([]);
      });

      const req = httpMock.expectOne('/geocoding/search?q=Non-existent+place&format=json&limit=1&addressdetails=1');
      req.flush(mockResponse);
    });

    it('should handle HTTP errors', () => {
      const address = 'Paris, France';

      service.geocodeAddress(address).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error).toBeTruthy();
        }
      });

      const req = httpMock.expectOne('/geocoding/search?q=Paris%2C+France&format=json&limit=1&addressdetails=1');
      req.flush('Not Found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('geocodeAddressAsync', () => {
    it('should return coordinates when geocoding succeeds', async () => {
      const address = 'Paris, France';
      const mockResponse: GeocodingResponse[] = [{
        lat: 48.8566,
        lon: 2.3522,
        display_name: 'Paris, France'
      }];

      const promise = service.geocodeAddressAsync(address);
      
      const req = httpMock.expectOne('/geocoding/search?q=Paris%2C+France&format=json&limit=1&addressdetails=1');
      req.flush(mockResponse);

      const result = await promise;
      expect(result).toEqual({ lat: 48.8566, lon: 2.3522 });
    });

    it('should return null when no results found', async () => {
      const address = 'Non-existent place';
      const mockResponse: GeocodingResponse[] = [];

      const promise = service.geocodeAddressAsync(address);
      
      const req = httpMock.expectOne('/geocoding/search?q=Non-existent+place&format=json&limit=1&addressdetails=1');
      req.flush(mockResponse);

      const result = await promise;
      expect(result).toBeNull();
    });

    it('should return null when HTTP error occurs', async () => {
      const address = 'Paris, France';

      const promise = service.geocodeAddressAsync(address);
      
      const req = httpMock.expectOne('/geocoding/search?q=Paris%2C+France&format=json&limit=1&addressdetails=1');
      req.flush('Server Error', { status: 500, statusText: 'Internal Server Error' });

      const result = await promise;
      expect(result).toBeNull();
    });

    it('should parse string coordinates to numbers', async () => {
      const address = 'New York, USA';
      const mockResponse: GeocodingResponse[] = [{
        lat: '40.7128' as any, // Simulate string from API
        lon: '-74.0060' as any, // Simulate string from API
        display_name: 'New York, USA'
      }];

      const promise = service.geocodeAddressAsync(address);
      
      const req = httpMock.expectOne('/geocoding/search?q=New+York%2C+USA&format=json&limit=1&addressdetails=1');
      req.flush(mockResponse);

      const result = await promise;
      expect(result).toEqual({ lat: 40.7128, lon: -74.0060 });
      expect(typeof result?.lat).toBe('number');
      expect(typeof result?.lon).toBe('number');
    });
  });
});
