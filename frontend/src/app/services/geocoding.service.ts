import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, firstValueFrom } from 'rxjs';
import { GeocodingResponse } from '../models/geocoding-response';

@Injectable({
  providedIn: 'root'
})
export class GeocodingService {
  private readonly NOMINATIM_BASE_URL = '/geocoding';

  constructor(private http: HttpClient) { }

  /**
   * Converts an address to latitude/longitude coordinates
   * @param address The address to geocode
   * @returns Observable with coordinates
   */
  geocodeAddress(address: string): Observable<GeocodingResponse[]> {
    const params = new HttpParams()
      .set('q', address)
      .set('format', 'json')
      .set('limit', '1')
      .set('addressdetails', '1');

    return this.http.get<GeocodingResponse[]>(`${this.NOMINATIM_BASE_URL}/search`, { 
      params
    });
  }

  /**
   * Converts an address to coordinates with error handling
   * @param address The address to geocode
   * @returns Promise with coordinates or null if error
   */
  async geocodeAddressAsync(address: string): Promise<{lat: number, lon: number} | null> {
    try {
      const response = await firstValueFrom(this.geocodeAddress(address));
      if (response && response.length > 0) {
        return {
          lat: parseFloat(response[0].lat.toString()),
          lon: parseFloat(response[0].lon.toString())
        };
      }
      return null;
    } catch (error) {
      console.error('Error during geocoding:', error);
      return null;
    }
  }
}
