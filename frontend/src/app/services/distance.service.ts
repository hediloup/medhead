import { Injectable } from '@angular/core';
import { Injectable } from '@angular/core';

export interface DistanceResult {
  distanceText?: string;
  distanceMeters?: number;
  durationText?: string;
  durationSeconds?: number;
}

// Helper to load Google Maps JS API dynamically
function loadGoogleMapsApi(apiKey: string): Promise<void> {
  return new Promise((resolve, reject) => {
    if ((window as any).google && (window as any).google.maps) {
      return resolve();
    }
    if (!apiKey) {
      return reject(new Error('Google Maps API key not provided'));
    }
    const script = document.createElement('script');
    script.src = `https://maps.googleapis.com/maps/api/js?key=${encodeURIComponent(apiKey)}&libraries=places`;
    script.async = true;
    script.defer = true;
    script.onload = () => resolve();
    script.onerror = (err) => reject(new Error('Failed to load Google Maps JS API'));
    document.head.appendChild(script);
  });
}

@Injectable({ providedIn: 'root' })
export class DistanceService {
  // Note: the API key must be provided in the frontend environment (see environment.ts)
  private get apiKey(): string {
    try {
      // dynamic import to avoid bundling environment in this snippet
      // eslint-disable-next-line @typescript-eslint/no-var-requires
      const env = require('../../environments/environment');
      return env?.environment?.googleMapsApiKey || env?.googleMapsApiKey || '';
    } catch (e) {
      return '';
    }
  }

  constructor() {}

  async getDistance(origin: {lat: number, lng: number}, destination: {lat: number, lng: number}): Promise<DistanceResult> {
    const key = this.apiKey;
    if (!key) {
      throw new Error('Google Maps API key is not configured (set environment.googleMapsApiKey)');
    }

    await loadGoogleMapsApi(key);

    return new Promise<DistanceResult>((resolve, reject) => {
      try {
        const directionsService = new (window as any).google.maps.DirectionsService();
        const request = {
          origin: new (window as any).google.maps.LatLng(origin.lat, origin.lng),
          destination: new (window as any).google.maps.LatLng(destination.lat, destination.lng),
          travelMode: (window as any).google.maps.TravelMode.DRIVING,
          drivingOptions: {
            departureTime: new Date(),
            trafficModel: 'best_guess'
          },
          provideRouteAlternatives: false
        };

        directionsService.route(request, (result: any, status: any) => {
          if (status !== (window as any).google.maps.DirectionsStatus.OK && status !== 'OK') {
            return reject(new Error('Directions request failed: ' + status));
          }
          const route = result.routes?.[0];
          const leg = route?.legs?.[0];
          if (!leg) {
            return reject(new Error('No legs in directions result'));
          }
          resolve({
            distanceText: leg.distance?.text,
            distanceMeters: leg.distance?.value,
            durationText: leg.duration_in_traffic?.text || leg.duration?.text,
            durationSeconds: leg.duration_in_traffic?.value || leg.duration?.value
          });
        });
      } catch (err) {
        reject(err);
      }
    });
  }
}
