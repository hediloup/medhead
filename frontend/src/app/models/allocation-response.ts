export interface AllocationResponse {
  hospital_name: string;
  hospital_id: number;
  distance_km: number;
  specialty: string;
  available_beds: number;
  estimated_time_minutes: number;
}
