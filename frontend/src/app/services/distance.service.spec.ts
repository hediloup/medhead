import { TestBed } from '@angular/core/testing';
import { DistanceService } from './distance.service';
import { environment } from '../../environments/environment';

describe('DistanceService', () => {
  let service: DistanceService;
  let originalGoogleMapsApiKey: string | undefined;
  let originalGoogle: any;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [DistanceService]
    });
    service = TestBed.inject(DistanceService);

    originalGoogleMapsApiKey = environment.googleMapsApiKey;
    environment.googleMapsApiKey = 'mock-api-key';
    
    // Store original google object
    originalGoogle = (window as any).google;
  });

  afterEach(() => {
    environment.googleMapsApiKey = originalGoogleMapsApiKey;
    (window as any).google = originalGoogle;
  });

  it('should be created', () => {
    console.log('🔍 Test: DistanceService - Création du service');
    expect(service).toBeTruthy();
    console.log('   ✅ Service créé avec succès');
  });

  it('should have getDistance method', () => {
    console.log('🔍 Test: DistanceService - Vérification de la méthode getDistance');
    expect(typeof service.getDistance).toBe('function');
    console.log('   ✅ Méthode getDistance présente');
  });

  it('should throw error when API key is not configured', async () => {
    console.log('🔍 Test: DistanceService - Erreur sans clé API');
    environment.googleMapsApiKey = '';
    
    const origin = { lat: 48.8566, lng: 2.3522 };
    const destination = { lat: 48.8606, lng: 2.3376 };
    
    console.log('   Test avec coordonnées: Paris -> Paris (proche)');
    
    try {
      await service.getDistance(origin, destination);
      fail('Should have thrown an error');
    } catch (error: any) {
      console.log('   Erreur capturée:', error.message);
      expect(error.message).toContain('Google Maps API key is not configured');
      console.log('   ✅ Erreur de clé API gérée correctement');
    }
  });

  it('should handle API key configuration', () => {
    console.log('🔍 Test: DistanceService - Configuration de la clé API');
    environment.googleMapsApiKey = 'test-key-123';
    
    // Test that the service can access the API key
    const serviceInstance = new DistanceService();
    expect(serviceInstance).toBeTruthy();
    console.log('   ✅ Service créé avec clé API configurée');
  });

  it('should handle invalid coordinates gracefully', async () => {
    console.log('🔍 Test: DistanceService - Coordonnées invalides');
    environment.googleMapsApiKey = 'test-key';
    
    // Mock google.maps to avoid actual API calls
    (window as any).google = {
      maps: {
        DirectionsService: function() {
          return {
            route: function(request: any, callback: any) {
              // Simulate error response
              callback(null, 'INVALID_REQUEST');
            }
          };
        },
        LatLng: function(lat: number, lng: number) {
          return { lat, lng };
        },
        TravelMode: { DRIVING: 'DRIVING' },
        TrafficModel: { BEST_GUESS: 'BEST_GUESS' },
        DirectionsStatus: { OK: 'OK' }
      }
    };

    const origin = { lat: 999, lng: 999 }; // Invalid coordinates
    const destination = { lat: 888, lng: 888 };
    
    console.log('   Test avec coordonnées invalides');
    
    try {
      await service.getDistance(origin, destination);
      fail('Should have thrown an error');
    } catch (error: any) {
      console.log('   Erreur capturée:', error.message);
      expect(error.message).toContain('Directions request failed');
      console.log('   ✅ Coordonnées invalides gérées correctement');
    }
  });

  it('should handle successful distance calculation', async () => {
    console.log('🔍 Test: DistanceService - Calcul de distance réussi');
    environment.googleMapsApiKey = 'test-key';
    
    // Mock successful google.maps response
    (window as any).google = {
      maps: {
        DirectionsService: function() {
          return {
            route: function(request: any, callback: any) {
              // Simulate successful response
              const mockResult = {
                routes: [{
                  legs: [{
                    distance: { text: '1.2 km', value: 1200 },
                    duration: { text: '5 mins', value: 300 },
                    duration_in_traffic: { text: '6 mins', value: 360 }
                  }]
                }]
              };
              callback(mockResult, 'OK');
            }
          };
        },
        LatLng: function(lat: number, lng: number) {
          return { lat, lng };
        },
        TravelMode: { DRIVING: 'DRIVING' },
        TrafficModel: { BEST_GUESS: 'BEST_GUESS' },
        DirectionsStatus: { OK: 'OK' }
      }
    };

    const origin = { lat: 48.8566, lng: 2.3522 };
    const destination = { lat: 48.8606, lng: 2.3376 };
    
    console.log('   Test avec coordonnées valides: Paris -> Paris (proche)');
    
    const result = await service.getDistance(origin, destination);
    
    console.log('   Résultat obtenu:', result);
    expect(result).toBeDefined();
    expect(result.distanceText).toBe('1.2 km');
    expect(result.distanceMeters).toBe(1200);
    expect(result.durationText).toBe('6 mins');
    expect(result.durationSeconds).toBe(360);
    console.log('   ✅ Calcul de distance réussi');
  });

  it('should handle retry logic when driving options fail', async () => {
    console.log('🔍 Test: DistanceService - Logique de retry');
    environment.googleMapsApiKey = 'test-key';
    
    let callCount = 0;
    (window as any).google = {
      maps: {
        DirectionsService: function() {
          return {
            route: function(request: any, callback: any) {
              callCount++;
              if (callCount === 1) {
                // First call fails (with driving options)
                throw new Error('Driving options not supported');
              } else {
                // Second call succeeds (without driving options)
                const mockResult = {
                  routes: [{
                    legs: [{
                      distance: { text: '2.5 km', value: 2500 },
                      duration: { text: '8 mins', value: 480 }
                    }]
                  }]
                };
                callback(mockResult, 'OK');
              }
            }
          };
        },
        LatLng: function(lat: number, lng: number) {
          return { lat, lng };
        },
        TravelMode: { DRIVING: 'DRIVING' },
        TrafficModel: { BEST_GUESS: 'BEST_GUESS' },
        DirectionsStatus: { OK: 'OK' }
      }
    };

    const origin = { lat: 48.8566, lng: 2.3522 };
    const destination = { lat: 48.8606, lng: 2.3376 };
    
    console.log('   Test de retry avec options de conduite');
    
    const result = await service.getDistance(origin, destination);
    
    console.log('   Nombre d\'appels:', callCount);
    expect(callCount).toBe(2); // Should have retried
    expect(result).toBeDefined();
    expect(result.distanceText).toBe('2.5 km');
    console.log('   ✅ Logique de retry fonctionne');
  });

  it('should handle multiple routes and select best one', async () => {
    console.log('🔍 Test: DistanceService - Sélection de la meilleure route');
    environment.googleMapsApiKey = 'test-key';
    
    (window as any).google = {
      maps: {
        DirectionsService: function() {
          return {
            route: function(request: any, callback: any) {
              // Simulate multiple routes with different durations
              const mockResult = {
                routes: [
                  {
                    legs: [{
                      distance: { text: '3.0 km', value: 3000 },
                      duration: { text: '10 mins', value: 600 },
                      duration_in_traffic: { text: '12 mins', value: 720 }
                    }]
                  },
                  {
                    legs: [{
                      distance: { text: '2.8 km', value: 2800 },
                      duration: { text: '8 mins', value: 480 },
                      duration_in_traffic: { text: '9 mins', value: 540 }
                    }]
                  }
                ]
              };
              callback(mockResult, 'OK');
            }
          };
        },
        LatLng: function(lat: number, lng: number) {
          return { lat, lng };
        },
        TravelMode: { DRIVING: 'DRIVING' },
        TrafficModel: { BEST_GUESS: 'BEST_GUESS' },
        DirectionsStatus: { OK: 'OK' }
      }
    };

    const origin = { lat: 48.8566, lng: 2.3522 };
    const destination = { lat: 48.8606, lng: 2.3376 };
    
    console.log('   Test avec plusieurs routes disponibles');
    
    const result = await service.getDistance(origin, destination);
    
    console.log('   Route sélectionnée:', result);
    expect(result).toBeDefined();
    // Should select the route with shorter duration (9 mins vs 12 mins)
    expect(result.durationText).toBe('9 mins');
    expect(result.durationSeconds).toBe(540);
    console.log('   ✅ Meilleure route sélectionnée');
  });
});
