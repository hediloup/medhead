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

      // Utiliser une approche plus flexible pour matcher les requêtes
      const req = httpMock.expectOne((request) => {
        return request.url.includes('/geocoding/search') && 
               request.params.get('q') === address;
      });
      
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });

    it('should handle empty response', () => {
      const address = 'Non-existent place';
      const mockResponse: GeocodingResponse[] = [];

      service.geocodeAddress(address).subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne((request) => {
        return request.url.includes('/geocoding/search') && 
               request.params.get('q') === address;
      });
      
      req.flush(mockResponse);
    });

    it('should handle HTTP errors', () => {
      const address = 'Paris, France';

      service.geocodeAddress(address).subscribe({
        next: () => fail('Expected error'),
        error: (error) => {
          expect(error.message).toContain('Http failure response for');
        }
      });

      const req = httpMock.expectOne((request) => {
        return request.url.includes('/geocoding/search') && 
               request.params.get('q') === address;
      });
      
      req.flush('Server error', { status: 500, statusText: 'Server Error' });
    });
  });

  describe('geocodeAddressAsync', () => {
    it('should return coordinates when geocoding succeeds', async () => {
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

      const result = service.geocodeAddressAsync(address);

      const req = httpMock.expectOne((request) => {
        return request.url.includes('/geocoding/search') && 
               request.params.get('q') === address;
      });
      
      req.flush(mockResponse);

      const coordinates = await result;
      expect(coordinates).toEqual({ lat: 48.8566, lon: 2.3522 });
    });

    it('should return null when no results found', async () => {
      const address = 'Non-existent place';
      const mockResponse: GeocodingResponse[] = [];

      const result = service.geocodeAddressAsync(address);

      const req = httpMock.expectOne((request) => {
        return request.url.includes('/geocoding/search') && 
               request.params.get('q') === address;
      });
      
      req.flush(mockResponse);

      const coordinates = await result;
      expect(coordinates).toBeNull();
    });

    it('should return null when HTTP error occurs', async () => {
      const address = 'Paris, France';

      const result = service.geocodeAddressAsync(address);

      const req = httpMock.expectOne((request) => {
        return request.url.includes('/geocoding/search') && 
               request.params.get('q') === address;
      });
      
      req.flush('Server error', { status: 500, statusText: 'Server Error' });

      const coordinates = await result;
      expect(coordinates).toBeNull();
    });

    it('should parse string coordinates to numbers', async () => {
      const address = 'New York, USA';
      const mockResponse: GeocodingResponse[] = [{
        lat: '40.7128' as any,
        lon: '-74.0060' as any,
        display_name: 'New York, NY, USA',
        address: {
          city: 'New York',
          country: 'USA',
          postcode: '10001'
        }
      }];

      const result = service.geocodeAddressAsync(address);

      const req = httpMock.expectOne((request) => {
        return request.url.includes('/geocoding/search') && 
               request.params.get('q') === address;
      });
      
      req.flush(mockResponse);

      const coordinates = await result;
      expect(coordinates).toEqual({ lat: 40.7128, lon: -74.0060 });
    });
  });
});
