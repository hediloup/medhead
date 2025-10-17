import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

// Métriques personnalisées
const errorRate = new Rate('errors');

export const options = {
  // Utilise un scénario à taux d'arrivée pour viser un débit précis (req/s)
  scenarios: {
    allocate_api_rate: {
      executor: 'ramping-arrival-rate',
      startRate: 100,           // démarrage à 100 req/s
      timeUnit: '1s',
      preAllocatedVUs: 1000,    // VUs pré-alloués pour absorber les pointes
      maxVUs: 2000,             // plafond de VUs si nécessaire
      stages: [
        { duration: '1m', target: 200 },  // 200 req/s
        { duration: '2m', target: 400 },  // 400 req/s
        { duration: '3m', target: 600 },  // 600 req/s
        { duration: '5m', target: 800 },  // 800 req/s (palier)
        { duration: '2m', target: 0 },    // descente
      ],
      tags: { test: 'allocate' },
      exec: 'default',
    },
  },
  thresholds: {
    'http_req_duration{expected_response:true}': [
      'p(95)<200',
      'p(99)<500',
      'avg<100'
    ],
    http_req_failed: ['rate<0.01'],
    errors: ['rate<0.01'],
  },
  // Réduction du coût client
  discardResponseBodies: true,
  noConnectionReuse: false,
  userAgent: 'k6-medhead-load-test/1.0',
};

// Données de test pour l'allocation d'hôpitaux
const testData = {
  specialty: 'Cardiology',
  latitude: 53.3976314,
  longitude: -2.1829641
};

// Headers pour la requête
const headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json',
};

export default function () {
  // Test de l'endpoint d'allocation d'hôpitaux
  const payload = JSON.stringify(testData);
  
  const response = http.post('http://localhost:8080/api/allocate', payload, {
    headers: headers,
    timeout: '5s', // Timeout réduit pour détecter les lenteurs
    tags: { endpoint: 'allocate' },
  });

  // Vérifications de la réponse (optimisées)
  const checks = check(response, {
    'Status is 200': (r) => r.status === 200,
    'Response time < 200ms': (r) => r.timings.duration < 200,
  });

  // Enregistrement des erreurs
  errorRate.add(response.status !== 200);

  // Log des erreurs pour debugging (réduit)
  if (response.status !== 200) {
    console.error(`Error ${response.status}: ${response.body.substring(0, 100)}`);
  }

  // Log des performances pour monitoring (seuil plus strict)
  // (désactivé pour limiter l'overhead côté client)

  // Pas de pause: le scénario à taux d'arrivée cadence déjà les itérations
  // sleep(0);
}

// Fonction de setup (optionnelle)
export function setup() {
  console.log('Starting MedHead Hospital Allocation Load Test');
  console.log('Target: 800 requests/second');
  console.log('Performance requirement: < 200ms response time');
  console.log('Test endpoint: http://localhost:8080/api/allocate');
  
  // Test de connectivité initial
  const testResponse = http.get('http://localhost:8080/api/health');
  if (testResponse.status !== 200) {
    console.error('Health check failed. Make sure the application is running.');
    return false;
  }
  
  console.log('Health check passed. Starting load test...');
  return true;
}

// Fonction de teardown (optionnelle)
export function teardown(data) {
  console.log('Load test completed');
  console.log('Check the results above for performance metrics');
}
