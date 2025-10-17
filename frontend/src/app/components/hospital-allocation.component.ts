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
    if (this.allocationForm.valid) {
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
        if (response && response.hospital_latitude != null && response.hospital_longitude != null) {
          const origin = { lat: latitude, lng: longitude };
          const destination = { lat: response.hospital_latitude, lng: response.hospital_longitude };
          (async () => {
            try {
                const res = await this.distanceService.getDistance(origin, destination);
                this.distanceText = res.distanceText || '';
                this.durationText = res.durationText || '';
                // Render route on map
                this.renderRouteOnMap(origin, destination);
            } catch (err: any) {
              console.warn('Distance service error', err);
              this.errorMessage = err?.message || 'Unable to retrieve live travel time/distance. Showing estimated values.';
            } finally {
              this.isLoading = false;
              this.isGeocoding = false;
            }
          })();
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
    try {
      await this.gmapsLoader.load();
      const google = (window as any).google;
      if (!google || !google.maps) return;

      // Create map if not exists
      let mapEl = document.getElementById('map');
      if (!mapEl) return;

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
        drivingOptions: {
          departureTime: new Date(),
          trafficModel: 'best_guess'
        }
      };

      this.directionsServiceInstance.route(request, (res: any, status: any) => {
        console.log('[medhead] DirectionsService callback status=', status);
        if (status === 'OK' || status === google.maps.DirectionsStatus.OK) {
          this.directionsRendererInstance.setDirections(res);
          console.log('[medhead] Directions rendered successfully');
        } else {
          console.warn('[medhead] Directions request failed: ', status, res);
        }
      });
    } catch (e) {
      console.error('Error rendering map route', e);
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
