import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

// Métriques personnalisées
const errorRate = new Rate('errors');
const slowResponseRate = new Rate('slow_responses');

export const options = {
  stages: [
    { duration: '2m', target: 100 },   // Échauffement progressif
    { duration: '3m', target: 300 },   // Montée graduelle
    { duration: '4m', target: 600 },   // Approche de la charge cible
    { duration: '6m', target: 800 },   // Charge cible de 800 req/s
    { duration: '3m', target: 0 },     // Descente progressive
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
    // Temps de réponse moyen < 80ms (optimisation)
    http_req_duration: ['avg<80'],
  },
  // Configuration optimisée pour les performances
  noConnectionReuse: false,
  userAgent: 'k6-medhead-optimized/1.0',
  // Optimisation des connexions
  batch: 15,
  batchPerHost: 8,
  // Réduction de la latence
  httpDebug: false,
  // Timeout optimisé
  httpReqDuration: '5s',
};

// Données de test pour l'allocation d'hôpitaux
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
  // Test de l'endpoint d'allocation d'hôpitaux
  const payload = JSON.stringify(testData);
  
  const response = http.post('http://localhost:8080/api/allocate', payload, {
    headers: headers,
    timeout: '3s', // Timeout optimisé
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
  
  // Enregistrement des réponses lentes
  slowResponseRate.add(response.timings.duration > 200);

  // Log des erreurs pour debugging (réduit)
  if (response.status !== 200) {
    console.error(`Error ${response.status}: ${response.body.substring(0, 50)}`);
  }

  // Log des performances pour monitoring (seuil strict)
  if (response.timings.duration > 100) {
    console.warn(`Slow response: ${response.timings.duration}ms`);
  }

  // Pause optimisée pour le débit
  sleep(0.03);
}

// Fonction de setup
export function setup() {
  console.log('🚀 Starting Optimized MedHead Load Test');
  console.log('🎯 Target: 800 requests/second');
  console.log('⚡ Performance requirement: < 200ms response time');
  console.log('🔗 Test endpoint: http://localhost:8080/api/allocate');
  
  // Test de connectivité initial
  const testResponse = http.get('http://localhost:8080/api/health');
  if (testResponse.status !== 200) {
    console.error('❌ Health check failed. Make sure the application is running.');
    return false;
  }
  
  console.log('✅ Health check passed. Starting optimized load test...');
  return true;
}

// Fonction de teardown
export function teardown(data) {
  console.log('🏁 Optimized load test completed');
  console.log('📊 Check the results above for performance metrics');
}
