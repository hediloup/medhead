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
   * Requests a hospital allocation via the backend API
   * @param request The allocation request
   * @returns Observable with the allocation response
   */
  allocateHospital(request: AllocationRequest): Observable<AllocationResponse> {
    return this.http.post<AllocationResponse>(`${this.API_BASE_URL}/allocate`, request)
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Checks API health
   * @returns Observable with API status
   */
  checkHealth(): Observable<string> {
    return this.http.get(`${this.API_BASE_URL}/health`, { responseType: 'text' })
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Error handler for API calls
   * @param error The HTTP error
   * @returns Error Observable with user message
   */
  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An unexpected error occurred';

    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Server-side error
      switch (error.status) {
        case 400:
          errorMessage = 'Invalid data. Please check your information.';
          break;
        case 404:
          errorMessage = 'No hospital available for this specialty.';
          break;
        case 500:
          errorMessage = 'Server error. Please try again later.';
          break;
        case 502:
          errorMessage = 'Backend service temporarily unavailable. Please try again.';
          break;
        case 503:
          errorMessage = 'Service temporarily unavailable. Please try again later.';
          break;
        case 504:
          errorMessage = 'Request timeout. The service is taking too long to respond. Please try again.';
          break;
        case 0:
          errorMessage = 'Unable to contact the server. Check your connection.';
          break;
        default:
          errorMessage = `Error ${error.status}: ${error.message}`;
      }
    }

    // Log error for debugging (excluding browser extension errors)
    if (!error.message?.includes('runtime.lastError') && 
        !error.message?.includes('message port closed')) {
      console.error('API Error:', error);
    }

    return throwError(() => new Error(errorMessage));
  }
}
