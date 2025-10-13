import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

// Métriques personnalisées
const errorRate = new Rate('errors');
const slowResponseRate = new Rate('slow_responses');

export const options = {
  stages: [
    { duration: '1m', target: 50 },    // Échauffement
    { duration: '3m', target: 200 },   // Charge conservatrice
    { duration: '1m', target: 0 },     // Descente
  ],
  thresholds: {
    // Objectifs conservateurs pour validation
    http_req_duration: ['p(95)<200'],  // Objectif principal
    http_req_duration: ['p(99)<500'],  // Tolérance
    http_req_failed: ['rate<0.01'],    // 1% d'erreur
    errors: ['rate<0.01'],
    slow_responses: ['rate<0.05'],     // 5% de réponses lentes
    http_req_duration: ['avg<100'],    // Temps moyen < 100ms
  },
  // Configuration optimisée
  noConnectionReuse: false,
  userAgent: 'k6-medhead-conservative/1.0',
};

// Données de test
const testData = {
  specialty: 'Cardiology',
  latitude: 53.3976314,
  longitude: -2.1829641
};

// Headers optimisés
const headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json',
  'Connection': 'keep-alive',
};

export default function () {
  const payload = JSON.stringify(testData);
  
  const response = http.post('http://localhost:8080/api/allocate', payload, {
    headers: headers,
    timeout: '5s',
    tags: { endpoint: 'allocate' },
  });

  // Vérifications de base
  const checks = check(response, {
    'Status is 200': (r) => r.status === 200,
    'Response time < 200ms': (r) => r.timings.duration < 200,
    'Response has body': (r) => r.body && r.body.length > 0,
    'Response is JSON': (r) => {
      try {
        JSON.parse(r.body);
        return true;
      } catch (e) {
        return false;
      }
    },
  });

  // Enregistrement des métriques
  errorRate.add(response.status !== 200);
  slowResponseRate.add(response.timings.duration > 200);

  // Log des performances
  if (response.timings.duration > 150) {
    console.warn(`Slow response: ${response.timings.duration}ms`);
  }

  // Pause optimisée
  sleep(0.1);
}

export function setup() {
  console.log('🔍 Starting Conservative Performance Test');
  console.log('🎯 Target: 200 requests/second');
  console.log('⚡ Objective: < 200ms response time (P95)');
  console.log('🔗 Endpoint: http://localhost:8080/api/allocate');
  
  const testResponse = http.get('http://localhost:8080/api/health');
  if (testResponse.status !== 200) {
    console.error('❌ Health check failed');
    return false;
  }
  
  console.log('✅ Health check passed. Starting conservative test...');
  return true;
}

export function teardown(data) {
  console.log('🏁 Conservative test completed');
  console.log('📊 Check results against conservative objectives:');
  console.log('   - P(95) < 200ms');
  console.log('   - P(99) < 500ms');
  console.log('   - Error rate < 1%');
  console.log('   - 200 req/s sustained');
}
