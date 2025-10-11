// K6 Performance Test Script for MedHead API
// This script provides comprehensive performance testing using K6

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

// Custom metrics
export const errorRate = new Rate('errors');
export const responseTime = new Trend('response_time');

// Test configuration
export const options = {
  stages: [
    { duration: '30s', target: 10 }, // Ramp up to 10 users
    { duration: '1m', target: 10 },  // Stay at 10 users
    { duration: '30s', target: 50 }, // Ramp up to 50 users
    { duration: '2m', target: 50 },  // Stay at 50 users
    { duration: '30s', target: 100 }, // Ramp up to 100 users
    { duration: '1m', target: 100 }, // Stay at 100 users
    { duration: '30s', target: 0 },  // Ramp down to 0 users
  ],
  thresholds: {
    http_req_duration: ['p(95)<2000'], // 95% of requests must complete below 2s
    http_req_failed: ['rate<0.05'],    // Error rate must be below 5%
    errors: ['rate<0.05'],             // Custom error rate below 5%
    response_time: ['p(95)<2000'],     // Custom response time below 2s
  },
};

// Test data
const testData = {
  specialty: 'Cardiology',
  latitude: 51.5074,
  longitude: -0.1278,
};

// Base URL
const BASE_URL = __ENV.API_BASE_URL || 'http://localhost:8080';

export default function () {
  // Test 1: Health Check
  const healthResponse = http.get(`${BASE_URL}/api/health`);
  check(healthResponse, {
    'health check status is 200': (r) => r.status === 200,
    'health check response time < 500ms': (r) => r.timings.duration < 500,
  });
  errorRate.add(healthResponse.status !== 200);
  responseTime.add(healthResponse.timings.duration);

  // Test 2: Allocation API Request
  const payload = JSON.stringify({
    specialty: testData.specialty,
    latitude: testData.latitude,
    longitude: testData.longitude,
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  const response = http.post(`${BASE_URL}/api/allocate`, payload, params);

  // Assertions
  check(response, {
    'allocation status is 200': (r) => r.status === 200,
    'allocation response time < 2000ms': (r) => r.timings.duration < 2000,
    'allocation response has hospital name': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.hospitalName && body.hospitalName.length > 0;
      } catch (e) {
        return false;
      }
    },
    'allocation response has distance': (r) => {
      try {
        const body = JSON.parse(r.body);
        return typeof body.distanceKm === 'number' && body.distanceKm > 0;
      } catch (e) {
        return false;
      }
    },
    'allocation response has available beds': (r) => {
      try {
        const body = JSON.parse(r.body);
        return typeof body.availableBeds === 'number' && body.availableBeds > 0;
      } catch (e) {
        return false;
      }
    },
  });

  errorRate.add(response.status !== 200);
  responseTime.add(response.timings.duration);

  // Test 3: GET Request (alternative endpoint)
  const getResponse = http.get(`${BASE_URL}/api/allocate?specialty=${testData.specialty}&latitude=${testData.latitude}&longitude=${testData.longitude}`);
  
  check(getResponse, {
    'GET allocation status is 200': (r) => r.status === 200,
    'GET allocation response time < 2000ms': (r) => r.timings.duration < 2000,
  });
  
  errorRate.add(getResponse.status !== 200);
  responseTime.add(getResponse.timings.duration);

  // Small delay between requests
  sleep(0.1);
}

// Setup function (runs once at the beginning)
export function setup() {
  console.log('Starting MedHead API performance test...');
  console.log(`Base URL: ${BASE_URL}`);
  
  // Verify API is accessible
  const healthCheck = http.get(`${BASE_URL}/api/health`);
  if (healthCheck.status !== 200) {
    throw new Error(`API health check failed: ${healthCheck.status}`);
  }
  
  console.log('API health check passed');
  return { baseUrl: BASE_URL };
}

// Teardown function (runs once at the end)
export function teardown(data) {
  console.log('Performance test completed');
  console.log(`Tested against: ${data.baseUrl}`);
}

// Custom function to generate random coordinates
function getRandomCoordinates() {
  const baseLat = 51.5074; // London
  const baseLon = -0.1278;
  const range = 0.1; // 10km radius
  
  return {
    latitude: baseLat + (Math.random() - 0.5) * range,
    longitude: baseLon + (Math.random() - 0.5) * range,
  };
}

// Custom function to test with random coordinates
export function testWithRandomCoordinates() {
  const coords = getRandomCoordinates();
  const payload = JSON.stringify({
    specialty: testData.specialty,
    latitude: coords.latitude,
    longitude: coords.longitude,
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  const response = http.post(`${BASE_URL}/api/allocate`, payload, params);
  
  check(response, {
    'random coordinates status is 200': (r) => r.status === 200,
    'random coordinates response time < 2000ms': (r) => r.timings.duration < 2000,
  });
  
  errorRate.add(response.status !== 200);
  responseTime.add(response.timings.duration);
}
