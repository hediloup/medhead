import http from 'k6/http';
import { check } from 'k6';

// Test rapide pour vérifier la connectivité et la fonctionnalité de base
export const options = {
  vus: 1,
  duration: '10s',
  thresholds: {
    http_req_duration: ['p(95)<1000'], // Seuil plus permissif pour le test rapide
    http_req_failed: ['rate<0.1'],     // Permet jusqu'à 10% d'erreurs
  },
};

const testData = {
  specialty: 'Cardiology',
  latitude: 53.3976314,
  longitude: -2.1829641
};

const headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json',
};

export default function () {
  console.log('Testing MedHead Hospital Allocation API...');
  
  // Test 1: Health check
  const healthResponse = http.get('http://localhost:4200/api/health');
  check(healthResponse, {
    'Health check status is 200': (r) => r.status === 200,
    'Health check response time < 1s': (r) => r.timings.duration < 1000,
  });
  
  if (healthResponse.status !== 200) {
    console.error(`Health check failed: ${healthResponse.status} - ${healthResponse.body}`);
    return;
  }
  
  console.log('✅ Health check passed');
  
  // Test 2: Allocation request
  const payload = JSON.stringify(testData);
  const response = http.post('http://localhost:4200/api/allocate', payload, {
    headers: headers,
    timeout: '10s',
  });
  
  const checks = check(response, {
    'Allocation status is 200': (r) => r.status === 200,
    'Allocation response time < 1s': (r) => r.timings.duration < 1000,
    'Response has body': (r) => r.body && r.body.length > 0,
    'Response is valid JSON': (r) => {
      try {
        JSON.parse(r.body);
        return true;
      } catch (e) {
        return false;
      }
    },
  });
  
  if (response.status === 200) {
    try {
      const data = JSON.parse(response.body);
      console.log('✅ Allocation successful');
      console.log(`   Hospital: ${data.hospitalName || data.hospital || 'N/A'}`);
      console.log(`   Distance: ${data.distance || 'N/A'}`);
      console.log(`   Time: ${data.estimatedTime || 'N/A'}`);
      console.log(`   Response time: ${response.timings.duration}ms`);
    } catch (e) {
      console.error('❌ Invalid JSON response:', response.body);
    }
  } else {
    console.error(`❌ Allocation failed: ${response.status} - ${response.body}`);
  }
}

export function setup() {
  console.log('🚀 Starting quick connectivity test...');
  console.log('Endpoint: http://localhost:4200/api/allocate');
  console.log('Test data:', JSON.stringify(testData, null, 2));
}

export function teardown() {
  console.log('✅ Quick test completed');
  console.log('If all checks passed, you can run the full load test with: k6 run test.js');
}
