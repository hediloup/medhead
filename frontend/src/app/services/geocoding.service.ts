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
   * Convertit une adresse en coordonnées latitude/longitude
   * @param address L'adresse à géocoder
   * @returns Observable avec les coordonnées
   */
  geocodeAddress(address: string): Observable<GeocodingResponse[]> {
    const params = new HttpParams()
      .set('q', address)
      .set('format', 'json')
      .set('limit', '1')
      .set('addressdetails', '1');

    return this.http.get<GeocodingResponse[]>(`${this.NOMINATIM_BASE_URL}/search`, { 
      params,
      headers: {
        'User-Agent': 'MedHead-App/1.0'
      }
    });
  }

  /**
   * Convertit une adresse en coordonnées avec gestion d'erreur
   * @param address L'adresse à géocoder
   * @returns Promise avec les coordonnées ou null si erreur
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
      console.error('Erreur lors du géocodage:', error);
      return null;
    }
  }
}
