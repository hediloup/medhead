import { AllocationResponse } from './allocation-response';

describe('AllocationResponse', () => {
  it('should create an instance', () => {
    const response: AllocationResponse = {
      hospital_name: 'Hôpital Saint-Antoine',
      hospital_id: 1,
      distance_km: 5.2,
      specialty: 'Cardiology',
      available_beds: 3,
      estimated_time_minutes: 12
    };
    
    expect(response).toBeTruthy();
    expect(response.hospital_name).toBe('Hôpital Saint-Antoine');
    expect(response.hospital_id).toBe(1);
    expect(response.distance_km).toBe(5.2);
    expect(response.specialty).toBe('Cardiology');
    expect(response.available_beds).toBe(3);
    expect(response.estimated_time_minutes).toBe(12);
  });

  it('should handle response with zero available beds', () => {
    const response: AllocationResponse = {
      hospital_name: 'Hôpital de la Salpêtrière',
      hospital_id: 2,
      distance_km: 3.8,
      specialty: 'Neurology',
      available_beds: 0,
      estimated_time_minutes: 8
    };
    
    expect(response.available_beds).toBe(0);
    expect(response.distance_km).toBeGreaterThan(0);
  });

  it('should handle response with large distance', () => {
    const response: AllocationResponse = {
      hospital_name: 'Centre Hospitalier de Versailles',
      hospital_id: 15,
      distance_km: 25.7,
      specialty: 'Orthopedics',
      available_beds: 5,
      estimated_time_minutes: 35
    };
    
    expect(response.distance_km).toBe(25.7);
    expect(response.estimated_time_minutes).toBe(35);
  });
});
