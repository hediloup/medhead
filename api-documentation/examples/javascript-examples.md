# 🌐 Exemples JavaScript - API MedHead

Ce fichier contient des exemples d'utilisation de l'API MedHead avec JavaScript/Node.js.

## 🚀 Configuration Initiale

### Installation des dépendances
```bash
npm init -y
npm install axios
# ou
npm install fetch
```

### Configuration de base
```javascript
const axios = require('axios');

// Configuration de base
const API_BASE_URL = 'http://localhost:8080';
const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json'
  }
});
```

## 🏥 Allocation de Lits d'Hôpitaux

### 1. Allocation basique
```javascript
async function allocateHospital(specialty, latitude, longitude) {
  try {
    const response = await api.post('/api/allocate', {
      specialty,
      latitude,
      longitude
    });
    
    console.log('Allocation réussie:', response.data);
    return response.data;
  } catch (error) {
    console.error('Erreur d\'allocation:', error.response?.data || error.message);
    throw error;
  }
}

// Utilisation
allocateHospital('Cardiology', 53.3976314, -2.1829641)
  .then(result => console.log('Hôpital recommandé:', result.hospital_name))
  .catch(error => console.error('Erreur:', error));
```

### 2. Allocation avec gestion d'erreurs complète
```javascript
async function allocateHospitalWithErrorHandling(specialty, latitude, longitude) {
  try {
    const response = await api.post('/api/allocate', {
      specialty,
      latitude,
      longitude
    });
    
    return {
      success: true,
      data: response.data,
      status: response.status
    };
  } catch (error) {
    const errorResponse = {
      success: false,
      status: error.response?.status || 500,
      message: error.response?.data?.message || error.message,
      data: error.response?.data
    };
    
    // Gestion spécifique des erreurs
    switch (errorResponse.status) {
      case 400:
        console.error('Erreur de validation:', errorResponse.message);
        break;
      case 404:
        console.error('Aucun hôpital trouvé pour cette spécialité');
        break;
      case 500:
        console.error('Erreur serveur interne');
        break;
      default:
        console.error('Erreur inconnue:', errorResponse.message);
    }
    
    return errorResponse;
  }
}
```

### 3. Allocation avec retry automatique
```javascript
async function allocateHospitalWithRetry(specialty, latitude, longitude, maxRetries = 3) {
  for (let attempt = 1; attempt <= maxRetries; attempt++) {
    try {
      console.log(`Tentative ${attempt}/${maxRetries}...`);
      
      const response = await api.post('/api/allocate', {
        specialty,
        latitude,
        longitude
      });
      
      console.log('Allocation réussie:', response.data);
      return response.data;
    } catch (error) {
      console.error(`Tentative ${attempt} échouée:`, error.message);
      
      if (attempt === maxRetries) {
        throw new Error(`Échec après ${maxRetries} tentatives: ${error.message}`);
      }
      
      // Attendre avant la prochaine tentative
      await new Promise(resolve => setTimeout(resolve, 1000 * attempt));
    }
  }
}
```

## 🔍 Tests de Diagnostic

### 1. Test de santé
```javascript
async function checkApiHealth() {
  try {
    const response = await api.get('/api/health');
    console.log('API Status:', response.data);
    return true;
  } catch (error) {
    console.error('API non disponible:', error.message);
    return false;
  }
}
```

### 2. Test de diagnostic complet
```javascript
async function runDiagnosticTests() {
  console.log('🔍 Tests de diagnostic MedHead');
  console.log('===============================');
  
  // Test de santé
  console.log('1. Test de santé...');
  const healthOk = await checkApiHealth();
  console.log(healthOk ? '✅ API opérationnelle' : '❌ API non disponible');
  
  // Test d'allocation
  console.log('2. Test d\'allocation...');
  try {
    const result = await allocateHospital('Cardiology', 53.3976314, -2.1829641);
    console.log('✅ Allocation réussie:', result.hospital_name);
  } catch (error) {
    console.log('❌ Allocation échouée:', error.message);
  }
  
  // Test de diagnostic automatique
  console.log('3. Test automatique...');
  try {
    const response = await api.get('/api/test');
    console.log('✅ Test automatique:', response.data);
  } catch (error) {
    console.log('❌ Test automatique échoué:', error.message);
  }
}
```

## 👥 Gestion des Patients (Authentification)

### 1. Configuration avec authentification
```javascript
// Configuration avec token JWT
function createAuthenticatedApi(token) {
  return axios.create({
    baseURL: API_BASE_URL,
    timeout: 5000,
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    }
  });
}

// Utilisation
const authenticatedApi = createAuthenticatedApi('YOUR_JWT_TOKEN');
```

### 2. Obtenir les statistiques patients
```javascript
async function getPatientStatistics(token) {
  try {
    const response = await authenticatedApi.get('/api/patients/statistics');
    console.log('Statistiques patients:', response.data);
    return response.data;
  } catch (error) {
    console.error('Erreur lors de la récupération des statistiques:', error.message);
    throw error;
  }
}
```

### 3. Anonymiser tous les patients
```javascript
async function anonymizeAllPatients(token) {
  try {
    const response = await authenticatedApi.post('/api/patients/anonymize-all');
    console.log('Anonymisation:', response.data);
    return response.data;
  } catch (error) {
    console.error('Erreur lors de l\'anonymisation:', error.message);
    throw error;
  }
}
```

### 4. Nettoyer les données expirées
```javascript
async function cleanupExpiredPatients(token) {
  try {
    const response = await authenticatedApi.delete('/api/patients/cleanup-expired');
    console.log('Nettoyage:', response.data);
    return response.data;
  } catch (error) {
    console.error('Erreur lors du nettoyage:', error.message);
    throw error;
  }
}
```

## 📊 Tests de Performance

### 1. Test de charge simple
```javascript
async function loadTest(concurrentRequests = 10) {
  console.log(`🚀 Test de charge avec ${concurrentRequests} requêtes simultanées`);
  
  const startTime = Date.now();
  const promises = [];
  
  for (let i = 0; i < concurrentRequests; i++) {
    promises.push(
      allocateHospital('Cardiology', 53.3976314, -2.1829641)
        .then(result => ({ success: true, index: i, result }))
        .catch(error => ({ success: false, index: i, error: error.message }))
    );
  }
  
  const results = await Promise.all(promises);
  const endTime = Date.now();
  const duration = endTime - startTime;
  
  const successful = results.filter(r => r.success).length;
  const failed = results.filter(r => !r.success).length;
  
  console.log(`📊 Résultats du test de charge:`);
  console.log(`   Durée totale: ${duration}ms`);
  console.log(`   Requêtes réussies: ${successful}`);
  console.log(`   Requêtes échouées: ${failed}`);
  console.log(`   Taux de succès: ${(successful / concurrentRequests * 100).toFixed(2)}%`);
  console.log(`   Temps moyen par requête: ${(duration / concurrentRequests).toFixed(2)}ms`);
  
  return results;
}
```

### 2. Test de charge avec métriques détaillées
```javascript
async function detailedLoadTest(concurrentRequests = 10, duration = 30000) {
  console.log(`🚀 Test de charge détaillé (${duration}ms)`);
  
  const results = [];
  const startTime = Date.now();
  let requestCount = 0;
  
  const makeRequest = async () => {
    const requestStart = Date.now();
    try {
      const result = await allocateHospital('Cardiology', 53.3976314, -2.1829641);
      const requestEnd = Date.now();
      results.push({
        success: true,
        duration: requestEnd - requestStart,
        timestamp: requestStart,
        result
      });
    } catch (error) {
      const requestEnd = Date.now();
      results.push({
        success: false,
        duration: requestEnd - requestStart,
        timestamp: requestStart,
        error: error.message
      });
    }
    requestCount++;
  };
  
  // Lancer des requêtes pendant la durée spécifiée
  const interval = setInterval(makeRequest, 100);
  
  // Arrêter après la durée spécifiée
  setTimeout(() => {
    clearInterval(interval);
    
    const endTime = Date.now();
    const totalDuration = endTime - startTime;
    
    const successful = results.filter(r => r.success);
    const failed = results.filter(r => !r.success);
    
    const avgResponseTime = successful.reduce((sum, r) => sum + r.duration, 0) / successful.length;
    const minResponseTime = Math.min(...successful.map(r => r.duration));
    const maxResponseTime = Math.max(...successful.map(r => r.duration));
    
    console.log(`📊 Résultats du test de charge détaillé:`);
    console.log(`   Durée totale: ${totalDuration}ms`);
    console.log(`   Requêtes totales: ${requestCount}`);
    console.log(`   Requêtes réussies: ${successful.length}`);
    console.log(`   Requêtes échouées: ${failed.length}`);
    console.log(`   Taux de succès: ${(successful.length / requestCount * 100).toFixed(2)}%`);
    console.log(`   Temps de réponse moyen: ${avgResponseTime.toFixed(2)}ms`);
    console.log(`   Temps de réponse min: ${minResponseTime}ms`);
    console.log(`   Temps de réponse max: ${maxResponseTime}ms`);
    console.log(`   Requêtes par seconde: ${(requestCount / (totalDuration / 1000)).toFixed(2)}`);
    
  }, duration);
}
```

## 🔄 Monitoring en Temps Réel

### 1. Monitoring continu
```javascript
class MedHeadMonitor {
  constructor(apiUrl, interval = 30000) {
    this.apiUrl = apiUrl;
    this.interval = interval;
    this.isRunning = false;
    this.stats = {
      totalRequests: 0,
      successfulRequests: 0,
      failedRequests: 0,
      averageResponseTime: 0
    };
  }
  
  async checkHealth() {
    try {
      const startTime = Date.now();
      const response = await api.get('/api/health');
      const endTime = Date.now();
      
      this.stats.totalRequests++;
      this.stats.successfulRequests++;
      this.stats.averageResponseTime = 
        (this.stats.averageResponseTime * (this.stats.successfulRequests - 1) + (endTime - startTime)) / 
        this.stats.successfulRequests;
      
      console.log(`✅ [${new Date().toISOString()}] API opérationnelle (${endTime - startTime}ms)`);
      return true;
    } catch (error) {
      this.stats.totalRequests++;
      this.stats.failedRequests++;
      console.error(`❌ [${new Date().toISOString()}] API non disponible: ${error.message}`);
      return false;
    }
  }
  
  start() {
    if (this.isRunning) return;
    
    this.isRunning = true;
    console.log(`🔍 Démarrage du monitoring (intervalle: ${this.interval}ms)`);
    
    const monitor = async () => {
      if (!this.isRunning) return;
      
      await this.checkHealth();
      setTimeout(monitor, this.interval);
    };
    
    monitor();
  }
  
  stop() {
    this.isRunning = false;
    console.log('⏹️ Monitoring arrêté');
  }
  
  getStats() {
    return {
      ...this.stats,
      successRate: this.stats.totalRequests > 0 ? 
        (this.stats.successfulRequests / this.stats.totalRequests * 100).toFixed(2) + '%' : '0%'
    };
  }
}

// Utilisation
const monitor = new MedHeadMonitor('http://localhost:8080', 30000);
monitor.start();

// Arrêter après 5 minutes
setTimeout(() => {
  monitor.stop();
  console.log('📊 Statistiques finales:', monitor.getStats());
}, 300000);
```

## 🧪 Tests Automatisés

### 1. Suite de tests complète
```javascript
class MedHeadTestSuite {
  constructor(apiUrl) {
    this.api = axios.create({
      baseURL: apiUrl,
      timeout: 5000,
      headers: { 'Content-Type': 'application/json' }
    });
    this.results = [];
  }
  
  async runTest(name, testFunction) {
    console.log(`🧪 Exécution du test: ${name}`);
    const startTime = Date.now();
    
    try {
      const result = await testFunction();
      const duration = Date.now() - startTime;
      
      this.results.push({
        name,
        success: true,
        duration,
        result
      });
      
      console.log(`✅ ${name} - Réussi (${duration}ms)`);
      return result;
    } catch (error) {
      const duration = Date.now() - startTime;
      
      this.results.push({
        name,
        success: false,
        duration,
        error: error.message
      });
      
      console.log(`❌ ${name} - Échoué (${duration}ms): ${error.message}`);
      throw error;
    }
  }
  
  async runAllTests() {
    console.log('🧪 Démarrage de la suite de tests MedHead');
    console.log('==========================================');
    
    // Test de santé
    await this.runTest('Test de santé', async () => {
      const response = await this.api.get('/api/health');
      if (response.data !== 'Allocation API operational') {
        throw new Error('Réponse de santé incorrecte');
      }
      return response.data;
    });
    
    // Test d'allocation
    await this.runTest('Test d\'allocation Cardiology', async () => {
      const response = await this.api.post('/api/allocate', {
        specialty: 'Cardiology',
        latitude: 53.3976314,
        longitude: -2.1829641
      });
      
      if (!response.data.hospital_name) {
        throw new Error('Réponse d\'allocation invalide');
      }
      return response.data;
    });
    
    // Test d'allocation avec spécialité différente
    await this.runTest('Test d\'allocation Dermatology', async () => {
      const response = await this.api.post('/api/allocate', {
        specialty: 'Dermatology',
        latitude: 51.5074,
        longitude: -0.1278
      });
      
      if (!response.data.hospital_name) {
        throw new Error('Réponse d\'allocation invalide');
      }
      return response.data;
    });
    
    // Test de diagnostic
    await this.runTest('Test de diagnostic', async () => {
      const response = await this.api.get('/api/test');
      if (!response.data.includes('Test successful')) {
        throw new Error('Test de diagnostic échoué');
      }
      return response.data;
    });
    
    // Test de gestion d'erreur
    await this.runTest('Test de gestion d\'erreur', async () => {
      try {
        await this.api.post('/api/allocate', {
          specialty: 'NonExistentSpecialty',
          latitude: 53.3976314,
          longitude: -2.1829641
        });
        throw new Error('Erreur attendue non générée');
      } catch (error) {
        if (error.response?.status === 404) {
          return 'Erreur 404 correctement gérée';
        }
        throw error;
      }
    });
    
    this.printResults();
  }
  
  printResults() {
    console.log('\n📊 Résultats de la suite de tests');
    console.log('==================================');
    
    const successful = this.results.filter(r => r.success).length;
    const failed = this.results.filter(r => !r.success).length;
    const total = this.results.length;
    
    console.log(`Tests réussis: ${successful}/${total}`);
    console.log(`Tests échoués: ${failed}/${total}`);
    console.log(`Taux de succès: ${(successful / total * 100).toFixed(2)}%`);
    
    if (failed > 0) {
      console.log('\n❌ Tests échoués:');
      this.results
        .filter(r => !r.success)
        .forEach(r => console.log(`   - ${r.name}: ${r.error}`));
    }
    
    const avgDuration = this.results.reduce((sum, r) => sum + r.duration, 0) / total;
    console.log(`Temps moyen par test: ${avgDuration.toFixed(2)}ms`);
  }
}

// Exécution de la suite de tests
const testSuite = new MedHeadTestSuite('http://localhost:8080');
testSuite.runAllTests().catch(console.error);
```

## 📝 Exemples d'Utilisation Pratique

### 1. Application de démonstration
```javascript
class MedHeadDemo {
  constructor() {
    this.api = axios.create({
      baseURL: 'http://localhost:8080',
      timeout: 5000,
      headers: { 'Content-Type': 'application/json' }
    });
  }
  
  async demonstrateAllocation() {
    console.log('🏥 Démonstration MedHead - Allocation de Lits');
    console.log('=============================================');
    
    const testCases = [
      { specialty: 'Cardiology', location: 'Manchester', lat: 53.3976314, lng: -2.1829641 },
      { specialty: 'Dermatology', location: 'London', lat: 51.5074, lng: -0.1278 },
      { specialty: 'Neurology', location: 'Birmingham', lat: 52.4862, lng: -1.8904 }
    ];
    
    for (const testCase of testCases) {
      console.log(`\n📍 Test: ${testCase.specialty} à ${testCase.location}`);
      
      try {
        const result = await this.api.post('/api/allocate', {
          specialty: testCase.specialty,
          latitude: testCase.lat,
          longitude: testCase.lng
        });
        
        console.log(`✅ Hôpital recommandé: ${result.data.hospital_name}`);
        console.log(`   Distance: ${result.data.distance_km} km`);
        console.log(`   Temps estimé: ${result.data.estimated_time_minutes} minutes`);
        console.log(`   Lits disponibles: ${result.data.available_beds}`);
        
      } catch (error) {
        console.log(`❌ Erreur: ${error.message}`);
      }
    }
  }
}

// Exécution de la démonstration
const demo = new MedHeadDemo();
demo.demonstrateAllocation();
```

---

**Note** : Assurez-vous d'avoir installé les dépendances nécessaires :
```bash
npm install axios
```

Et que l'application MedHead est démarrée :
```bash
cd /home/hedi/projects/medhead/docker
docker-compose up -d
```
