import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class GoogleMapsLoaderService {
  private loading: Promise<void> | null = null;

  load(): Promise<void> {
    if (this.loading) return this.loading;
    const apiKey = environment.googleMapsApiKey;
    if (!apiKey) {
      console.error('[GoogleMapsLoader] API key not configured');
      return Promise.reject(new Error('Google Maps API key not configured'));
    }

    this.loading = new Promise((resolve, reject) => {
      if ((window as any).google && (window as any).google.maps) {
        console.log('[GoogleMapsLoader] google.maps already present');
        resolve();
        return;
      }
      console.log('[GoogleMapsLoader] injecting script tag for Google Maps JS API with key:', apiKey.substring(0, 10) + '...');
      const script = document.createElement('script');
      script.src = `https://maps.googleapis.com/maps/api/js?key=${encodeURIComponent(apiKey)}&libraries=places`;
      script.async = true;
      script.defer = true;
      script.onload = () => {
        console.log('[GoogleMapsLoader] Google Maps JS API loaded successfully');
        resolve();
      };
      script.onerror = (err) => {
        console.error('[GoogleMapsLoader] failed to load Google Maps JS API', err);
        this.loading = null; // Reset loading state so we can retry
        reject(new Error('Failed to load Google Maps JS API'));
      };
      document.head.appendChild(script);
    });
    return this.loading;
  }
}
