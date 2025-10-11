import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AllocationService } from '../services/allocation.service';
import { GeocodingService } from '../services/geocoding.service';
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
  errorMessage = '';
  successMessage = '';

  // Liste des spécialités médicales disponibles
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
  ) {
    this.allocationForm = this.fb.group({
      specialty: ['', [Validators.required]],
      address: ['', [Validators.required, Validators.minLength(5)]]
    });
  }

  ngOnInit(): void {
    // Vérifier la santé de l'API au démarrage
    this.checkApiHealth();
  }

  /**
   * Vérifie la santé de l'API backend
   */
  private checkApiHealth(): void {
    this.allocationService.checkHealth().subscribe({
      next: (response) => {
        console.log('API Health Check:', response);
      },
      error: (error) => {
        console.error('API Health Check Failed:', error);
        this.errorMessage = 'Le service backend n\'est pas disponible. Veuillez vérifier que le serveur est démarré.';
      }
    });
  }

  /**
   * Soumet le formulaire pour demander une allocation d'hôpital
   */
  onSubmit(): void {
    if (this.allocationForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      this.successMessage = '';
      this.allocationResult = null;

      const formValue = this.allocationForm.value;
      
      // Étape 1: Géocoder l'adresse
      this.geocodeAddress(formValue.address, formValue.specialty);
    } else {
      this.markFormGroupTouched();
    }
  }

  /**
   * Géocode l'adresse et lance la demande d'allocation
   */
  private async geocodeAddress(address: string, specialty: string): Promise<void> {
    this.isGeocoding = true;
    
    try {
      const coordinates = await this.geocodingService.geocodeAddressAsync(address);
      
      if (coordinates) {
        // Étape 2: Demander l'allocation avec les coordonnées
        this.requestAllocation(specialty, coordinates.lat, coordinates.lon);
      } else {
        this.errorMessage = 'Impossible de trouver cette adresse. Veuillez vérifier l\'adresse et réessayer.';
        this.isLoading = false;
        this.isGeocoding = false;
      }
    } catch (error) {
      this.errorMessage = 'Erreur lors de la recherche de l\'adresse. Veuillez réessayer.';
      this.isLoading = false;
      this.isGeocoding = false;
    }
  }

  /**
   * Demande l'allocation d'hôpital avec les coordonnées géographiques
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
        this.successMessage = `Hôpital recommandé trouvé: ${response.hospital_name}`;
        this.isLoading = false;
        this.isGeocoding = false;
      },
      error: (error) => {
        this.errorMessage = error.message;
        this.isLoading = false;
        this.isGeocoding = false;
      }
    });
  }

  /**
   * Marque tous les champs du formulaire comme touchés pour afficher les erreurs
   */
  private markFormGroupTouched(): void {
    Object.keys(this.allocationForm.controls).forEach(key => {
      const control = this.allocationForm.get(key);
      control?.markAsTouched();
    });
  }

  /**
   * Remet à zéro le formulaire et les résultats
   */
  resetForm(): void {
    this.allocationForm.reset();
    this.allocationResult = null;
    this.errorMessage = '';
    this.successMessage = '';
  }

  /**
   * Vérifie si un champ du formulaire a des erreurs
   */
  hasFieldError(fieldName: string): boolean {
    const field = this.allocationForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  /**
   * Retourne le message d'erreur pour un champ
   */
  getFieldError(fieldName: string): string {
    const field = this.allocationForm.get(fieldName);
    if (field && field.errors) {
      if (field.errors['required']) {
        return 'Ce champ est obligatoire';
      }
      if (field.errors['minlength']) {
        return `Minimum ${field.errors['minlength'].requiredLength} caractères requis`;
      }
    }
    return '';
  }
}
