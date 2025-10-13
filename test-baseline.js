import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

// Métriques personnalisées
const errorRate = new Rate('errors');
const slowResponseRate = new Rate('slow_responses');

export const options = {
  stages: [
    { duration: '1m', target: 50 },    // Échauffement
    { duration: '2m', target: 100 },   // Test de base
    { duration: '2m', target: 200 },   // Montée progressive
    { duration: '2m', target: 300 },   // Augmentation
    { duration: '2m', target: 400 },   // Approche de la charge cible
    { duration: '2m', target: 0 },     // Descente
  ],
  thresholds: {
    // Objectifs réalistes pour identifier le point de rupture
    http_req_duration: ['p(95)<500'],  // Seuil plus permissif
    http_req_duration: ['p(99)<1000'], // Tolérance
    http_req_failed: ['rate<0.05'],    // 5% d'erreur acceptable
    errors: ['rate<0.05'],
    slow_responses: ['rate<0.10'],     // 10% de réponses lentes
  },
  // Configuration optimisée
  noConnectionReuse: false,
  userAgent: 'k6-medhead-baseline/1.0',
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
    timeout: '10s',
    tags: { endpoint: 'allocate' },
  });

  // Vérifications de base
  const checks = check(response, {
    'Status is 200': (r) => r.status === 200,
    'Response time < 500ms': (r) => r.timings.duration < 500,
    'Response has body': (r) => r.body && r.body.length > 0,
  });

  // Enregistrement des métriques
  errorRate.add(response.status !== 200);
  slowResponseRate.add(response.timings.duration > 500);

  // Log des performances (seuil plus permissif)
  if (response.timings.duration > 300) {
    console.warn(`Slow response: ${response.timings.duration}ms`);
  }

  // Pause optimisée
  sleep(0.1);
}

export function setup() {
  console.log('🔍 Starting Baseline Performance Test');
  console.log('📈 Will gradually increase load to find performance limits');
  console.log('🎯 Target: Identify sustainable load level');
  
  const testResponse = http.get('http://localhost:8080/api/health');
  if (testResponse.status !== 200) {
    console.error('❌ Health check failed');
    return false;
  }
  
  console.log('✅ Health check passed. Starting baseline test...');
  return true;
}

export function teardown(data) {
  console.log('🏁 Baseline test completed');
  console.log('📊 Analyze results to identify:');
  console.log('   - Sustainable load level');
  console.log('   - Performance bottlenecks');
  console.log('   - Optimization opportunities');
}
