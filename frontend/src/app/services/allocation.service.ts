import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { AllocationRequest } from '../models/allocation-request';
import { AllocationResponse } from '../models/allocation-response';

@Injectable({
  providedIn: 'root'
})
export class AllocationService {
  private readonly API_BASE_URL = '/api';

  constructor(private http: HttpClient) { }

  /**
   * Demande une allocation d'hôpital via l'API backend
   * @param request La demande d'allocation
   * @returns Observable avec la réponse d'allocation
   */
  allocateHospital(request: AllocationRequest): Observable<AllocationResponse> {
    return this.http.post<AllocationResponse>(`${this.API_BASE_URL}/allocate`, request)
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Vérifie la santé de l'API
   * @returns Observable avec le statut de l'API
   */
  checkHealth(): Observable<string> {
    return this.http.get(`${this.API_BASE_URL}/health`, { responseType: 'text' })
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Gestionnaire d'erreurs pour les appels API
   * @param error L'erreur HTTP
   * @returns Observable d'erreur avec message utilisateur
   */
  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'Une erreur inattendue s\'est produite';

    if (error.error instanceof ErrorEvent) {
      // Erreur côté client
      errorMessage = `Erreur: ${error.error.message}`;
    } else {
      // Erreur côté serveur
      switch (error.status) {
        case 400:
          errorMessage = 'Données invalides. Veuillez vérifier vos informations.';
          break;
        case 404:
          errorMessage = 'Aucun hôpital disponible pour cette spécialité.';
          break;
        case 500:
          errorMessage = 'Erreur serveur. Veuillez réessayer plus tard.';
          break;
        case 0:
          errorMessage = 'Impossible de contacter le serveur. Vérifiez votre connexion.';
          break;
        default:
          errorMessage = `Erreur ${error.status}: ${error.message}`;
      }
    }

    return throwError(() => new Error(errorMessage));
  }
}
