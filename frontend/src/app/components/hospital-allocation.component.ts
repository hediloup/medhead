import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AllocationService } from '../services/allocation.service';
import { GeocodingService } from '../services/geocoding.service';
import { DistanceService } from '../services/distance.service';
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
  ) {
    this.allocationForm = this.fb.group({
      specialty: ['', [Validators.required]],
      address: ['', [Validators.required, Validators.minLength(5)]]
    });
  }

  ngOnInit(): void {
    // Check API health on startup
    this.checkApiHealth();
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
