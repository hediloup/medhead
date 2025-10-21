import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

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
      console.log('[DistanceService] google.maps already present');
      return resolve();
    }
    if (!apiKey) {
      return reject(new Error('Google Maps API key not provided'));
    }
    console.log('[DistanceService] injecting Google Maps JS API script');
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
    return environment?.googleMapsApiKey || '';
  }

  constructor() {}

  async getDistance(origin: {lat: number, lng: number}, destination: {lat: number, lng: number}): Promise<DistanceResult> {
    console.log('[DistanceService] getDistance called with:', { origin, destination });
    const key = this.apiKey;
    console.log('[DistanceService] API key available:', !!key);
    if (!key) {
      throw new Error('Clé API Google Maps non configurée. Veuillez configurer environment.googleMapsApiKey');
    }

    // Validate coordinates
    if (!origin || !destination || 
        typeof origin.lat !== 'number' || typeof origin.lng !== 'number' ||
        typeof destination.lat !== 'number' || typeof destination.lng !== 'number') {
      throw new Error('Coordonnées invalides fournies à getDistance');
    }

    console.log('[DistanceService] Loading Google Maps API...');
    try {
      await loadGoogleMapsApi(key);
      console.log('[DistanceService] Google Maps API loaded successfully');
    } catch (error: any) {
      console.error('[DistanceService] Failed to load Google Maps API:', error);
      if (error.message && error.message.includes('ApiTargetBlockedMapError')) {
        throw new Error('Clé API Google Maps bloquée. Vérifiez les restrictions dans Google Cloud Console.');
      }
      throw new Error('Impossible de charger l\'API Google Maps: ' + (error.message || 'Erreur inconnue'));
    }

    return new Promise<DistanceResult>((resolve, reject) => {
      try {
        const google = (window as any).google;
        console.log('[DistanceService] Google Maps object available:', !!google);
        console.log('[DistanceService] DirectionsService available:', !!google?.maps?.DirectionsService);
        
        if (!google || !google.maps || !google.maps.DirectionsService) {
          throw new Error('API Google Maps non disponible. Vérifiez votre connexion internet et la clé API.');
        }
        
        const directionsService = new google.maps.DirectionsService();

        const request = {
          origin: new google.maps.LatLng(origin.lat, origin.lng),
          destination: new google.maps.LatLng(destination.lat, destination.lng),
          travelMode: google.maps.TravelMode.DRIVING,
          drivingOptions: {
            departureTime: new Date(),
            trafficModel: google.maps.TrafficModel.BEST_GUESS
          },
          provideRouteAlternatives: true
        };
        
        console.log('[DistanceService] Making directions request:', request);

        const callRoute = (req: any) => {
          try {
            console.log('[DistanceService] Calling directionsService.route...');
            directionsService.route(req, (directionsResult: any, status: any) => {
              console.log('[DistanceService] Directions response received:', { status, result: directionsResult });
              if (status !== google.maps.DirectionsStatus.OK && status !== 'OK') {
                console.error('[DistanceService] Directions request failed with status:', status);
                return reject(new Error('Directions request failed: ' + status));
              }
              // Select the best route based on traffic conditions
              let bestRoute = directionsResult.routes?.[0];
              if (directionsResult.routes && directionsResult.routes.length > 1) {
                bestRoute = directionsResult.routes.reduce((best: any, current: any) => {
                  const bestDuration = best.legs[0]?.duration_in_traffic?.value || best.legs[0]?.duration?.value || Infinity;
                  const currentDuration = current.legs[0]?.duration_in_traffic?.value || current.legs[0]?.duration?.value || Infinity;
                  return currentDuration < bestDuration ? current : best;
                });
              }
              
              const leg = bestRoute?.legs?.[0];
              if (!leg) {
                console.error('[DistanceService] No legs in directions result');
                return reject(new Error('No legs in directions result'));
              }
              
              const finalResult = {
                distanceText: leg.distance?.text,
                distanceMeters: leg.distance?.value,
                durationText: leg.duration_in_traffic?.text || leg.duration?.text,
                durationSeconds: leg.duration_in_traffic?.value || leg.duration?.value
              };
              
              console.log('[DistanceService] Resolving with result:', finalResult);
              resolve(finalResult);
            });
          } catch (err) {
            console.warn('[DistanceService] directions.route threw, retrying without drivingOptions', err);
            const simpleReq = {
              origin: req.origin,
              destination: req.destination,
              travelMode: req.travelMode,
              provideRouteAlternatives: req.provideRouteAlternatives
            };
            try {
              directionsService.route(simpleReq, (retryResult: any, status: any) => {
                if (status !== google.maps.DirectionsStatus.OK && status !== 'OK') {
                  return reject(new Error('Directions request failed on retry: ' + status));
                }
                // Select the best route based on traffic conditions
                let bestRoute = retryResult.routes?.[0];
                if (retryResult.routes && retryResult.routes.length > 1) {
                  bestRoute = retryResult.routes.reduce((best: any, current: any) => {
                    const bestDuration = best.legs[0]?.duration_in_traffic?.value || best.legs[0]?.duration?.value || Infinity;
                    const currentDuration = current.legs[0]?.duration_in_traffic?.value || current.legs[0]?.duration?.value || Infinity;
                    return currentDuration < bestDuration ? current : best;
                  });
                }
                
                const leg = bestRoute?.legs?.[0];
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
            } catch (err2) {
              return reject(err2);
            }
          }
        };

        callRoute(request);
      } catch (err) {
        reject(err);
      }
    });
  }
}
