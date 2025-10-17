import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AllocationService } from '../services/allocation.service';
import { GeocodingService } from '../services/geocoding.service';
import { DistanceService } from '../services/distance.service';
import { GoogleMapsLoaderService } from '../services/google-maps-loader.service';
import { AllocationRequest } from '../models/allocation-request';
import { AllocationResponse } from '../models/allocation-response';

@Component({
  selector: 'app-hospital-allocation',
  templateUrl: './hospital-allocation.component.html',
  styleUrls: ['./hospital-allocation.component.css']
})
export class HospitalAllocationComponent implements OnInit {
  allocationForm: FormGroup;
  isLoading = false;
  isGeocoding = false;
  allocationResult: AllocationResponse | null = null;
  distanceText = '';
  durationText = '';
  errorMessage = '';
  successMessage = '';
  // When directions fail, we expose a fallback URL to open Google Maps
  googleMapsFallbackUrl?: string;
  // Map and renderer instances (persisted)
  private mapInstance: any = null;
  private directionsRendererInstance: any = null;
  private directionsServiceInstance: any = null;

  // List of available medical specialties
  medicalSpecialties = [
    'Cardiology',
    'Neurology',
    'Orthopedics',
    'Emergency Medicine',
    'Pediatrics',
    'Oncology',
    'Radiology',
    'Anesthesiology',
    'Dermatology',
    'Psychiatry',
    'Gynecology',
    'Urology',
    'Ophthalmology',
    'ENT',
    'Internal Medicine',
    'General Surgery'
  ];

  constructor(
    private fb: FormBuilder,
    private allocationService: AllocationService,
    private geocodingService: GeocodingService
    , private distanceService: DistanceService
    , private gmapsLoader: GoogleMapsLoaderService
  ) {
    this.allocationForm = this.fb.group({
      specialty: ['', [Validators.required]],
      address: ['', [Validators.required, Validators.minLength(5)]]
    });
  }

  ngOnInit(): void {
    // Debug helper: confirm this running build includes our changes
    console.log('[medhead-version] google-maps-integration=1');

    // Check API health on startup
    this.checkApiHealth();
    // Prepare map container if google key available later
    // Map will be initialized on first allocation result
    // Expose debug helper to window so you can trigger route rendering manually from console
    try {
      (window as any).__medheadRenderRoute = async (olat: number, olng: number, dlat: number, dlng: number) => {
        console.log('[medhead debug] manual renderRoute called', olat, olng, dlat, dlng);
        await this.renderRouteOnMap({lat: olat, lng: olng}, {lat: dlat, lng: dlng});
      };
      // Expose helper to trigger a full allocation search from the console for debugging
      (window as any).__medheadTriggerSearch = (specialty?: string, address?: string) => {
        try {
          if (specialty) this.allocationForm.get('specialty')?.setValue(specialty);
          if (address) this.allocationForm.get('address')?.setValue(address);
          console.log('[medhead debug] __medheadTriggerSearch calling onSubmit with', this.allocationForm.value);
          this.onSubmit();
        } catch (e) { console.error('[medhead debug] __medheadTriggerSearch failed', e); }
      };
    } catch (e) { /* ignore in non-browser env */ }
  }

  /**
   * Checks backend API health
   */
  private checkApiHealth(): void {
    this.allocationService.checkHealth().subscribe({
      next: (response) => {
        console.log('API Health Check:', response);
      },
      error: (error) => {
        console.error('API Health Check Failed:', error);
        this.errorMessage = 'Backend service is not available. Please check that the server is started.';
      }
    });
  }

  /**
   * Submits the form to request a hospital allocation
   */
  onSubmit(): void {
    console.log('[medhead] onSubmit called, form value=', this.allocationForm.value);
    if (this.allocationForm.valid) {
      // Clear any previous fallback link
      this.googleMapsFallbackUrl = undefined;
      this.isLoading = true;
      this.errorMessage = '';
      this.successMessage = '';
      this.allocationResult = null;

      const formValue = this.allocationForm.value;
      
      // Step 1: Geocode the address
      this.geocodeAddress(formValue.address, formValue.specialty);
    } else {
      this.markFormGroupTouched();
    }
  }

  /**
   * Geocodes the address and launches the allocation request
   */
  private async geocodeAddress(address: string, specialty: string): Promise<void> {
    console.log('[medhead] geocodeAddress called with', address, specialty);
    this.isGeocoding = true;
    
    try {
      const coordinates = await this.geocodingService.geocodeAddressAsync(address);
      
      if (coordinates) {
        // Step 2: Request allocation with coordinates
        this.requestAllocation(specialty, coordinates.lat, coordinates.lon);
      } else {
        this.errorMessage = 'Unable to find this address. Please check the address and try again.';
        this.isLoading = false;
        this.isGeocoding = false;
      }
    } catch (error) {
      this.errorMessage = 'Error while searching for the address. Please try again.';
      this.isLoading = false;
      this.isGeocoding = false;
    }
  }

  /**
   * Requests hospital allocation with geographic coordinates
   */
  private requestAllocation(specialty: string, latitude: number, longitude: number): void {
    console.log('[medhead] requestAllocation called with', { specialty, latitude, longitude });
    const request: AllocationRequest = {
      specialty,
      latitude,
      longitude
    };

    this.allocationService.allocateHospital(request).subscribe({
      next: (response) => {
        this.allocationResult = response;
        this.successMessage = `Recommended hospital found: ${response.hospital_name}`;
        // After allocation, fetch distance/time from Google via backend
        console.log('[medhead] allocation response received', response);
        if (response && response.hospital_latitude != null && response.hospital_longitude != null) {
          const origin = { lat: latitude, lng: longitude };
          const destination = { lat: response.hospital_latitude, lng: response.hospital_longitude };
          console.log('[medhead] scheduling distance+render for origin,destination', origin, destination);
          // Delay the distance / render call to ensure Angular has time to render
          // the map container (it is shown using *ngIf="allocationResult"). Without this,
          // renderRouteOnMap can run before the DOM element exists; users reported the map
          // only appears when manually invoking the helper from the console.
          setTimeout(() => {
            (async () => {
              try {
                const res = await this.distanceService.getDistance(origin, destination);
                this.distanceText = res.distanceText || '';
                this.durationText = res.durationText || '';
                // Render route on map with additional delay to ensure DOM is ready
                setTimeout(() => {
                  this.renderRouteOnMap(origin, destination);
                }, 100);
              } catch (err: any) {
                console.warn('Distance service error', err);
                this.errorMessage = err?.message || 'Unable to retrieve live travel time/distance. Showing estimated values.';
                // Still try to render the map even if distance service fails
                setTimeout(() => {
                  this.renderRouteOnMap(origin, destination);
                }, 100);
              } finally {
                this.isLoading = false;
                this.isGeocoding = false;
              }
            })();
          }, 100);
        } else {
          this.isLoading = false;
          this.isGeocoding = false;
        }
      },
      error: (error) => {
        this.errorMessage = error.message;
        this.isLoading = false;
        this.isGeocoding = false;
      }
    });
  }

  /** Initialize or update the map and show route between origin and destination */
  private async renderRouteOnMap(origin: {lat:number,lng:number}, destination: {lat:number,lng:number}) {
    console.log('[medhead] renderRouteOnMap called with origin:', origin, 'destination:', destination);
    
    try {
      await this.gmapsLoader.load();
      const google = (window as any).google;
      if (!google || !google.maps) {
        console.error('[medhead] Google Maps API not loaded');
        return;
      }

      // Create map if not exists
      // Some Angular builds add attribute selectors like _ngcontent-xxx; ensure element is found.
      // If the element isn't present yet (view not updated), retry a few times with small delay.
      let mapEl: HTMLElement | null = null;
      const findMapEl = () => {
        mapEl = document.getElementById('map') as HTMLElement | null;
        if (!mapEl) {
          const els = document.querySelectorAll('[id]');
          for (let i = 0; i < els.length; i++) {
            const el = els[i] as HTMLElement;
            if (el.id === 'map') { mapEl = el; break; }
          }
        }
        console.log('[medhead] map element search result:', mapEl);
      };

      findMapEl();
      let attempts = 0;
      while (!mapEl && attempts < 10) {
        // Wait 200ms and try again
        // eslint-disable-next-line no-await-in-loop
        await new Promise(r => setTimeout(r, 200));
        attempts++;
        findMapEl();
        console.log(`[medhead] map element search attempt ${attempts}/10`);
      }
      
      if (!mapEl) {
        console.error('[medhead] map element not found after retries; aborting render');
        // Build fallback URL
        const originParam = `${origin.lat},${origin.lng}`;
        const destParam = `${destination.lat},${destination.lng}`;
        this.googleMapsFallbackUrl = `https://www.google.com/maps/dir/?api=1&origin=${encodeURIComponent(originParam)}&destination=${encodeURIComponent(destParam)}&travelmode=driving`;
        return;
      }

      // Initialize or reuse map centered between points
      const center = { lat: (origin.lat + destination.lat)/2, lng: (origin.lng + destination.lng)/2 };
      if (!this.mapInstance) {
        console.log('[medhead] creating new map instance at center', center);
        this.mapInstance = new google.maps.Map(mapEl, { zoom: 12, center });
      } else {
        console.log('[medhead] reusing existing map instance');
        this.mapInstance.setCenter(center);
      }

      if (!this.directionsServiceInstance) {
        this.directionsServiceInstance = new google.maps.DirectionsService();
      }
      if (!this.directionsRendererInstance) {
        this.directionsRendererInstance = new google.maps.DirectionsRenderer({ map: this.mapInstance });
      } else {
        this.directionsRendererInstance.setMap(this.mapInstance);
      }

      const request = {
        origin: new google.maps.LatLng(origin.lat, origin.lng),
        destination: new google.maps.LatLng(destination.lat, destination.lng),
        travelMode: google.maps.TravelMode.DRIVING,
        // Only provide departureTime; omit trafficModel to avoid InvalidValueError on some API versions
        drivingOptions: {
          departureTime: new Date()
        }
      };

      // Wrap route call so synchronous exceptions (InvalidValueError, etc.) can be retried with a simpler request
      const callRoute = (req: any, onResult: (res:any, status:any)=>void) => {
        try {
          this.directionsServiceInstance.route(req, onResult);
        } catch (err) {
          console.warn('[medhead] DirectionsService threw, will retry without drivingOptions', err);
          // Retry without drivingOptions
          const simpleReq = {
            origin: req.origin,
            destination: req.destination,
            travelMode: req.travelMode
          };
          try {
            this.directionsServiceInstance.route(simpleReq, onResult);
          } catch (err2) {
            console.error('[medhead] DirectionsService retry also threw', err2);
            // Build fallback URL
            try {
              const originParam = `${origin.lat},${origin.lng}`;
              const destParam = `${destination.lat},${destination.lng}`;
              this.googleMapsFallbackUrl = `https://www.google.com/maps/dir/?api=1&origin=${encodeURIComponent(originParam)}&destination=${encodeURIComponent(destParam)}&travelmode=driving`;
              try { (window as any).__medheadGoogleMapsFallback = this.googleMapsFallbackUrl; } catch(e) {}
            } catch (e) { console.error('Failed to build fallback URL after route retry error', e); }
          }
        }
      };

      callRoute(request, (res: any, status: any) => {
        console.log('[medhead] DirectionsService callback status=', status);
        if (status === 'OK' || status === google.maps.DirectionsStatus.OK) {
          try { this.googleMapsFallbackUrl = undefined; (window as any).__medheadGoogleMapsFallback = undefined; } catch(e){}
          this.directionsRendererInstance.setDirections(res);
          console.log('[medhead] Directions rendered successfully');
        } else {
          console.warn('[medhead] Directions request failed: ', status, res);
          // Build a fallback URL to open Google Maps directions in a new tab
          try {
            const originParam = `${origin.lat},${origin.lng}`;
            const destParam = `${destination.lat},${destination.lng}`;
            this.googleMapsFallbackUrl = `https://www.google.com/maps/dir/?api=1&origin=${encodeURIComponent(originParam)}&destination=${encodeURIComponent(destParam)}&travelmode=driving`;
            try { (window as any).__medheadGoogleMapsFallback = this.googleMapsFallbackUrl; } catch(e) {}
          } catch (e) {
            console.error('Failed to build fallback URL', e);
          }
        }
      });
    } catch (e) {
      console.error('Error rendering map route', e);
      // If loader failed, expose fallback so user can open Google Maps directly
      try {
        const originParam = `${origin.lat},${origin.lng}`;
        const destParam = `${destination.lat},${destination.lng}`;
        this.googleMapsFallbackUrl = `https://www.google.com/maps/dir/?api=1&origin=${encodeURIComponent(originParam)}&destination=${encodeURIComponent(destParam)}&travelmode=driving`;
        try { (window as any).__medheadGoogleMapsFallback = this.googleMapsFallbackUrl; } catch(e) {}
      } catch (err) {
        console.error('Failed to build fallback URL after loader error', err);
      }
    }
  }

  /**
   * Marks all form fields as touched to display errors
   */
  private markFormGroupTouched(): void {
    Object.keys(this.allocationForm.controls).forEach(key => {
      const control = this.allocationForm.get(key);
      control?.markAsTouched();
    });
  }

  /**
   * Resets the form and results
   */
  resetForm(): void {
    this.allocationForm.reset();
    this.allocationResult = null;
    this.errorMessage = '';
    this.successMessage = '';
    this.googleMapsFallbackUrl = undefined;
    // Clear map instances to force recreation on next search
    this.mapInstance = null;
    this.directionsRendererInstance = null;
    this.directionsServiceInstance = null;
  }

  /**
   * Force reload the map (useful for debugging)
   */
  reloadMap(): void {
    if (this.allocationResult && this.allocationResult.hospital_latitude && this.allocationResult.hospital_longitude) {
      const formValue = this.allocationForm.value;
      if (formValue.address) {
        this.geocodeAddress(formValue.address, formValue.specialty).then(() => {
          console.log('[medhead] Map reloaded successfully');
        }).catch((error) => {
          console.error('[medhead] Failed to reload map:', error);
        });
      }
    }
  }

  /**
   * Checks if a form field has errors
   */
  hasFieldError(fieldName: string): boolean {
    const field = this.allocationForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  /**
   * Returns the error message for a field
   */
  getFieldError(fieldName: string): string {
    const field = this.allocationForm.get(fieldName);
    if (field && field.errors) {
      if (field.errors['required']) {
        return 'This field is required';
      }
      if (field.errors['minlength']) {
        return `Minimum ${field.errors['minlength'].requiredLength} characters required`;
      }
    }
    return '';
  }
}
