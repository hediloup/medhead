import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

// Métriques personnalisées
const errorRate = new Rate('errors');

export const options = {
  stages: [
    { duration: '1m', target: 200 },   // Montée progressive plus lente
    { duration: '2m', target: 400 },   // Augmentation graduelle
    { duration: '3m', target: 600 },   // Approche de la charge cible
    { duration: '5m', target: 800 },   // Charge cible de 800 req/s
    { duration: '2m', target: 0 },     // Descente progressive
  ],
  thresholds: {
    // Performance : 95% des requêtes < 200ms (objectif principal)
    http_req_duration: ['p(95)<200'],
    // Performance : 99% des requêtes < 500ms (tolérance)
    http_req_duration: ['p(99)<500'],
    // Taux d'erreur < 1%
    http_req_failed: ['rate<0.01'],
    // Taux d'erreur personnalisé
    errors: ['rate<0.01'],
    // Temps de réponse moyen < 100ms (optimisation)
    http_req_duration: ['avg<100'],
  },
  // Configuration optimisée pour les performances
  noConnectionReuse: false,
  userAgent: 'k6-medhead-load-test/1.0',
  // Réduction du timeout pour détecter les lenteurs
  httpDebug: false,
  // Optimisation des connexions
  batch: 20,
  batchPerHost: 10,
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

  // Enregistrement des erreurs
  errorRate.add(response.status !== 200);

  // Log des erreurs pour debugging (réduit)
  if (response.status !== 200) {
    console.error(`Error ${response.status}: ${response.body.substring(0, 100)}`);
  }

  // Log des performances pour monitoring (seuil plus strict)
  if (response.timings.duration > 150) {
    console.warn(`Slow response: ${response.timings.duration}ms`);
  }

  // Pause réduite pour augmenter le débit
  sleep(0.05);
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
