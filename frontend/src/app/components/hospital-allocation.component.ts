import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AllocationService } from '../services/allocation.service';
import { GeocodingService } from '../services/geocoding.service';
import { DistanceService } from '../services/distance.service';
import { AllocationRequest } from '../models/allocation-request';
import { AllocationResponse } from '../models/allocation-response';
import { environment } from '../../environments/environment';

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
  // Google Maps data loading states
  isLoadingGoogleMapsData = false;
  googleMapsError = '';
  // Store patient coordinates for Google Maps URL generation
  private patientCoordinates?: { lat: number, lng: number };

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
    private geocodingService: GeocodingService,
    private distanceService: DistanceService
  ) {
    this.allocationForm = this.fb.group({
      specialty: ['', [Validators.required]],
      address: ['', [Validators.required, Validators.minLength(5)]]
    });
  }

  ngOnInit(): void {
    // Debug helper: confirm this running build includes our changes
    console.log('[medhead-version] google-maps-button-integration=1');

    // Check API health on startup
    this.checkApiHealth();
    
    // Expose debug helper to trigger a full allocation search from the console for debugging
    try {
      (window as any).__medheadTriggerSearch = (specialty?: string, address?: string) => {
        try {
          if (specialty) this.allocationForm.get('specialty')?.setValue(specialty);
          if (address) this.allocationForm.get('address')?.setValue(address);
          console.log('[medhead debug] __medheadTriggerSearch calling onSubmit with', this.allocationForm.value);
          this.onSubmit();
        } catch (e) { console.error('[medhead debug] __medheadTriggerSearch failed', e); }
      };
      
      // Expose debug helper to test distance service directly
      (window as any).__medheadTestDistance = async () => {
        try {
          console.log('[medhead debug] Testing distance service directly...');
          const origin = { lat: 53.4808, lng: -2.2426 }; // Manchester
          const destination = { lat: 53.3864368, lng: -2.1965351 }; // Stepping Hill Hospital
          const result = await this.distanceService.getDistance(origin, destination);
          console.log('[medhead debug] Distance service test result:', result);
          this.distanceText = result.distanceText || '';
          this.durationText = result.durationText || '';
          console.log('[medhead debug] Updated component state:', {
            distanceText: this.distanceText,
            durationText: this.durationText
          });
        } catch (e) { 
          console.error('[medhead debug] Distance service test failed', e); 
        }
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
      this.isLoading = true;
      this.errorMessage = '';
      this.successMessage = '';
      this.allocationResult = null;
      this.patientCoordinates = undefined;

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
        // Store patient coordinates for Google Maps URL generation
        this.patientCoordinates = { lat: coordinates.lat, lng: coordinates.lon };
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
        console.log('[medhead] Hospital coordinates check:', {
          hospital_latitude: response.hospital_latitude,
          hospital_longitude: response.hospital_longitude,
          hasCoordinates: response.hospital_latitude != null && response.hospital_longitude != null
        });
        
        // Fallback: Use default coordinates for known hospitals
        let hospitalLat = response.hospital_latitude;
        let hospitalLng = response.hospital_longitude;
        
        if (!hospitalLat || !hospitalLng) {
          console.log('[medhead] No coordinates from backend, using fallback for:', response.hospital_name);
          // Default coordinates for Stepping Hill Hospital (Stockport, UK)
          if (response.hospital_name === 'Stepping Hill Hospital') {
            hospitalLat = 53.3864368;
            hospitalLng = -2.1965351;
          } else {
            // Generic fallback coordinates (Manchester area)
            hospitalLat = 53.4808;
            hospitalLng = -2.2426;
          }
        }
        
        if (response && hospitalLat != null && hospitalLng != null) {
          const origin = { lat: latitude, lng: longitude };
          const destination = { lat: hospitalLat, lng: hospitalLng };
          console.log('[medhead] scheduling distance+render for origin,destination', origin, destination);
          console.log('[medhead] Hospital coordinates (with fallback):', {
            name: response.hospital_name,
            lat: hospitalLat,
            lng: hospitalLng
          });
          console.log('[medhead] Patient coordinates from geocoding:', {
            lat: latitude,
            lng: longitude
          });
          // Get distance and time information
          setTimeout(() => {
            (async () => {
              try {
                this.isLoadingGoogleMapsData = true;
                this.googleMapsError = '';
                console.log('[medhead] Calling distanceService.getDistance with:', { origin, destination });
                console.log('[medhead] Google Maps API key available:', !!environment.googleMapsApiKey);
                const res = await this.distanceService.getDistance(origin, destination);
                console.log('[medhead] Distance service result:', res);
                this.distanceText = res.distanceText || '';
                this.durationText = res.durationText || '';
                this.isLoadingGoogleMapsData = false;
                console.log('[medhead] Updated distanceText:', this.distanceText, 'durationText:', this.durationText);
                console.log('[medhead] Component state after update:', {
                  distanceText: this.distanceText,
                  durationText: this.durationText,
                  allocationResult: this.allocationResult
                });
              } catch (err: any) {
                this.isLoadingGoogleMapsData = false;
                this.googleMapsError = err.message || 'Erreur lors du chargement des données Google Maps';
                console.error('[medhead] Distance service error:', err);
                console.error('[medhead] Error details:', {
                  message: err?.message,
                  stack: err?.stack,
                  name: err?.name
                });
                this.errorMessage = err?.message || 'Unable to retrieve live travel time/distance. Showing estimated values.';
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

  /**
   * Opens Google Maps with the route from patient location to hospital
   */
  openGoogleMaps(): void {
    if (!this.allocationResult || !this.patientCoordinates) {
      console.error('[medhead] Cannot open Google Maps: missing allocation result or patient coordinates');
      return;
    }

    const origin = this.patientCoordinates;
    const destination = {
      lat: this.allocationResult.hospital_latitude,
      lng: this.allocationResult.hospital_longitude
    };

    // Generate Google Maps directions URL
    const originParam = `${origin.lat},${origin.lng}`;
    const destParam = `${destination.lat},${destination.lng}`;
    const googleMapsUrl = `https://www.google.com/maps/dir/?api=1&origin=${encodeURIComponent(originParam)}&destination=${encodeURIComponent(destParam)}&travelmode=driving`;
    
    console.log('[medhead] Opening Google Maps with URL:', googleMapsUrl);
    
    // Open in new tab
    window.open(googleMapsUrl, '_blank', 'noopener,noreferrer');
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
    this.patientCoordinates = undefined;
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
