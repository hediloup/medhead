export interface AllocationResponse {
  hospital_name: string;
  hospital_id: number;
  distance_km: number;
  specialty: string;
  available_beds: number;
  estimated_time_minutes: number;
  // Optional coordinates (may be provided by backend)
  hospital_latitude?: number;
  hospital_longitude?: number;
}
