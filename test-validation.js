import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

// Métriques personnalisées
const errorRate = new Rate('errors');
const slowResponseRate = new Rate('slow_responses');

export const options = {
  stages: [
    { duration: '30s', target: 100 },   // Échauffement
    { duration: '1m', target: 400 },    // Montée progressive
    { duration: '2m', target: 800 },    // Charge cible
    { duration: '1m', target: 0 },      // Descente
  ],
  thresholds: {
    // Objectif principal : 95% des requêtes < 200ms
    http_req_duration: ['p(95)<200'],
    // Tolérance : 99% des requêtes < 500ms
    http_req_duration: ['p(99)<500'],
    // Taux d'erreur < 1%
    http_req_failed: ['rate<0.01'],
    // Taux d'erreur personnalisé
    errors: ['rate<0.01'],
    // Réponses lentes < 5%
    slow_responses: ['rate<0.05'],
    // Temps de réponse moyen < 100ms
    http_req_duration: ['avg<100'],
  },
  // Configuration optimisée
  noConnectionReuse: false,
  userAgent: 'k6-medhead-validation/1.0',
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
    timeout: '3s',
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
  sleep(0.05);
}

export function setup() {
  console.log('🔍 Starting Performance Validation Test');
  console.log('🎯 Target: 800 requests/second');
  console.log('⚡ Objective: < 200ms response time (P95)');
  console.log('🔗 Endpoint: http://localhost:8080/api/allocate');
  
  const testResponse = http.get('http://localhost:8080/api/health');
  if (testResponse.status !== 200) {
    console.error('❌ Health check failed');
    return false;
  }
  
  console.log('✅ Health check passed. Starting validation test...');
  return true;
}

export function teardown(data) {
  console.log('🏁 Validation test completed');
  console.log('📊 Check results against performance objectives:');
  console.log('   - P(95) < 200ms');
  console.log('   - P(99) < 500ms');
  console.log('   - Error rate < 1%');
  console.log('   - 800 req/s sustained');
}
