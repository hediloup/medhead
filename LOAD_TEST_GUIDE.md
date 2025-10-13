# 🚀 Guide de Test de Charge K6 - MedHead Hospital Allocation

## 📋 Vue d'ensemble

Ce script K6 teste les performances de l'API d'allocation d'hôpitaux MedHead sous une charge de **800 requêtes/seconde** avec un objectif de **< 200ms de temps de réponse**.

## 🎯 Objectifs de Performance

- **Charge cible** : 800 requêtes/seconde
- **Temps de réponse** : < 200ms (95% des requêtes)
- **Taux d'erreur** : < 1%
- **Endpoint testé** : `POST /api/allocate`

## 🛠️ Installation de K6

### Ubuntu/Debian
```bash
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
echo "deb https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list
sudo apt-get update
sudo apt-get install k6
```

### macOS
```bash
brew install k6
```

### Windows
```bash
choco install k6
```

### Docker
```bash
docker pull grafana/k6:latest
```

## 🚀 Exécution du Test

### 1. Prérequis
Assurez-vous que l'application MedHead est démarrée :
```bash
cd docker
./start-medhead.sh
```

### 2. Test de base
```bash
k6 run test.js
```

### 3. Test avec Docker
```bash
docker run --rm -i grafana/k6:latest run - <test.js
```

### 4. Test avec options avancées
```bash
# Test avec sortie détaillée
k6 run --out json=results.json test.js

# Test avec seuils personnalisés
k6 run --threshold http_req_duration=p(95)<150 test.js

# Test avec plus de VUs
k6 run --vus 1000 --duration 5m test.js
```

## 📊 Métriques Surveillées

### Performance
- **http_req_duration** : Temps de réponse des requêtes
- **http_req_failed** : Taux d'échec des requêtes
- **http_reqs** : Nombre total de requêtes

### Seuils de Performance
- `p(95)<200` : 95% des requêtes < 200ms
- `p(99)<500` : 99% des requêtes < 500ms
- `rate<0.01` : Taux d'erreur < 1%

## 🔍 Analyse des Résultats

### Exemple de sortie réussie
```
✓ Status is 200
✓ Response time < 200ms
✓ Response time < 500ms
✓ Response has body
✓ Response is JSON
✓ Response contains hospital
✓ Response contains distance

checks.........................: 100.00% ✓ 48000      ✗ 0
data_received..................: 12 MB   200 kB/s
data_sent......................: 7.2 MB  120 kB/s
http_req_duration..............: avg=45ms    min=12ms med=38ms max=180ms p(95)=120ms p(99)=150ms
http_req_failed................: 0.00%   ✓ 0          ✗ 48000
http_reqs......................: 48000   800/s
```

### Interprétation
- **http_req_duration p(95)=120ms** : ✅ Objectif atteint (< 200ms)
- **http_req_failed 0.00%** : ✅ Aucune erreur
- **http_reqs 800/s** : ✅ Charge cible atteinte

## 🚨 Dépannage

### Erreurs courantes

#### 1. Connection refused
```
Error: dial tcp [::1]:4200: connect: connection refused
```
**Solution** : Vérifiez que l'application est démarrée sur le port 4200

#### 2. Timeout
```
Error: context deadline exceeded
```
**Solution** : Augmentez le timeout ou réduisez la charge

#### 3. 503 Service Unavailable
```
Error 503: Service temporarily unavailable
```
**Solution** : L'application est surchargée, réduisez la charge

### Optimisation des performances

#### 1. Réduction de la charge
```javascript
stages: [
  { duration: '1m', target: 400 },   // Réduire à 400 req/s
  { duration: '2m', target: 400 },
  { duration: '30s', target: 0 },
],
```

#### 2. Augmentation du timeout
```javascript
const response = http.post('http://localhost:4200/api/allocate', payload, {
  headers: headers,
  timeout: '60s', // Augmenter le timeout
});
```

## 📈 Scénarios de Test

### 1. Test de Montée en Charge
```javascript
stages: [
  { duration: '2m', target: 100 },
  { duration: '5m', target: 200 },
  { duration: '5m', target: 400 },
  { duration: '5m', target: 800 },
  { duration: '10m', target: 800 },
  { duration: '5m', target: 0 },
],
```

### 2. Test de Stress
```javascript
stages: [
  { duration: '2m', target: 1000 },  // Au-delà de la capacité normale
  { duration: '5m', target: 1000 },
  { duration: '2m', target: 0 },
],
```

### 3. Test de Volume
```javascript
stages: [
  { duration: '30m', target: 800 },  // Test prolongé
],
```

## 🔧 Configuration Avancée

### Variables d'environnement
```bash
export K6_VUS=800
export K6_DURATION=5m
k6 run test.js
```

### Fichier de configuration
```javascript
// config.js
export const options = {
  vus: 800,
  duration: '5m',
  thresholds: {
    http_req_duration: ['p(95)<200'],
  },
};
```

## 📝 Rapports et Monitoring

### Génération de rapports
```bash
# Rapport JSON
k6 run --out json=results.json test.js

# Rapport InfluxDB
k6 run --out influxdb=http://localhost:8086/k6 test.js

# Rapport Cloud
k6 cloud test.js
```

### Intégration avec Grafana
```bash
# Démarrage de Grafana + InfluxDB
docker-compose up -d grafana influxdb

# Test avec envoi vers InfluxDB
k6 run --out influxdb=http://localhost:8086/k6 test.js
```

## 🎯 Bonnes Pratiques

1. **Toujours commencer par un test de santé**
2. **Monter progressivement la charge**
3. **Surveiller les métriques système** (CPU, RAM, réseau)
4. **Tester dans un environnement similaire à la production**
5. **Documenter les résultats et les optimisations**

## 📞 Support

En cas de problème :
1. Vérifiez les logs de l'application
2. Consultez la documentation K6 : https://k6.io/docs/
3. Analysez les métriques système
4. Ajustez les seuils de performance selon vos besoins
