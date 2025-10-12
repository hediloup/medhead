import { AllocationRequest } from './allocation-request';

describe('AllocationRequest', () => {
  it('should create an instance', () => {
    const request: AllocationRequest = {
      specialty: 'Cardiology',
      latitude: 48.8566,
      longitude: 2.3522
    };
    
    expect(request).toBeTruthy();
    expect(request.specialty).toBe('Cardiology');
    expect(request.latitude).toBe(48.8566);
    expect(request.longitude).toBe(2.3522);
  });

  it('should create instance with valid medical specialty', () => {
    const request: AllocationRequest = {
      specialty: 'Neurology',
      latitude: 40.7128,
      longitude: -74.0060
    };
    
    expect(request.specialty).toBe('Neurology');
    expect(typeof request.latitude).toBe('number');
    expect(typeof request.longitude).toBe('number');
  });

  it('should handle edge case coordinates', () => {
    const request: AllocationRequest = {
      specialty: 'Emergency Medicine',
      latitude: 0,
      longitude: 0
    };
    
    expect(request.latitude).toBe(0);
    expect(request.longitude).toBe(0);
  });
});
