import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class GoogleMapsLoaderService {
  private loading: Promise<void> | null = null;

  load(): Promise<void> {
    if (this.loading) return this.loading;
    const apiKey = environment.googleMapsApiKey;
    if (!apiKey) {
      return Promise.reject(new Error('Google Maps API key not configured'));
    }

    this.loading = new Promise((resolve, reject) => {
      if ((window as any).google && (window as any).google.maps) {
        resolve();
        return;
      }
      const script = document.createElement('script');
      script.src = `https://maps.googleapis.com/maps/api/js?key=${encodeURIComponent(apiKey)}&libraries=places`;
      script.async = true;
      script.defer = true;
      script.onload = () => resolve();
      script.onerror = () => reject(new Error('Failed to load Google Maps JS API'));
      document.head.appendChild(script);
    });
    return this.loading;
  }
}
