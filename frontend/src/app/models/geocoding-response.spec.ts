import { GeocodingResponse } from './geocoding-response';

describe('GeocodingResponse', () => {
  it('should create an instance', () => {
    const response: GeocodingResponse = {
      lat: 48.8566,
      lon: 2.3522,
      display_name: 'Paris, France'
    };
    
    expect(response).toBeTruthy();
    expect(response.lat).toBe(48.8566);
    expect(response.lon).toBe(2.3522);
    expect(response.display_name).toBe('Paris, France');
  });

  it('should create instance with address details', () => {
    const response: GeocodingResponse = {
      lat: 40.7128,
      lon: -74.0060,
      display_name: 'New York, NY, USA',
      address: {
        city: 'New York',
        country: 'United States',
        postcode: '10001'
      }
    };
    
    expect(response.address).toBeTruthy();
    expect(response.address?.city).toBe('New York');
    expect(response.address?.country).toBe('United States');
    expect(response.address?.postcode).toBe('10001');
  });

  it('should handle response without address details', () => {
    const response: GeocodingResponse = {
      lat: 51.5074,
      lon: -0.1278,
      display_name: 'London, UK'
    };
    
    expect(response.address).toBeUndefined();
    expect(response.display_name).toBe('London, UK');
  });

  it('should handle edge case coordinates', () => {
    const response: GeocodingResponse = {
      lat: 0,
      lon: 0,
      display_name: 'Equator, Prime Meridian'
    };
    
    expect(response.lat).toBe(0);
    expect(response.lon).toBe(0);
  });
});
