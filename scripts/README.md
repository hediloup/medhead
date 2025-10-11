# Scripts Directory - MedHead Application

Ce dossier contient tous les scripts de test et d'automatisation pour l'application MedHead.

## 📁 Structure des Scripts

### 🧪 Tests Principaux
- **`run-all-tests.sh`** - Script principal pour exécuter toute la pyramide de tests
- **`run-k6-tests.sh`** - Tests de performance avec K6
- **`run-jmeter-tests.sh`** - Tests de stress avec JMeter
- **`stress-test.sh`** - Tests de stress personnalisés avec curl

### 📊 Configuration des Tests
- **`k6-performance-test.js`** - Configuration des tests K6
- **`jmeter-stress-test.jmx`** - Plan de test JMeter

## 🚀 Utilisation

### Depuis le dossier racine du projet :
```bash
# Exécuter tous les tests
./run-tests.sh

# Ou directement depuis le dossier scripts
cd scripts
./run-all-tests.sh
```

### Tests individuels :
```bash
cd scripts

# Tests de performance avec K6
./run-k6-tests.sh

# Tests de stress avec JMeter
./run-jmeter-tests.sh

# Tests de stress personnalisés
./stress-test.sh
```

## 🎯 Pyramide de Tests

Le script `run-all-tests.sh` implémente une pyramide de tests complète :

```
        🔺 E2E Tests (Cypress)
       🔺🔺 Integration Tests (Docker, API)
      🔺🔺🔺 Unit Tests (Backend & Frontend)
     🔺🔺🔺🔺 Stress Tests (K6, JMeter, curl)
    🔺🔺🔺🔺🔺 Performance Tests (Load, Memory)
```

### Types de Tests Inclus :

1. **Tests Unitaires**
   - Backend (Java/Spring Boot)
   - Frontend (Angular/TypeScript)

2. **Tests d'Intégration**
   - Base de données
   - API REST
   - Docker containers

3. **Tests E2E**
   - Cypress pour le frontend
   - Scénarios utilisateur complets

4. **Tests de Stress**
   - K6 pour les tests de performance
   - JMeter pour les tests de charge
   - Tests personnalisés avec curl

5. **Tests de Performance**
   - Temps de réponse API
   - Utilisation mémoire
   - Charge concurrente

## 📊 Rapports Générés

Les rapports sont générés dans le dossier `../reports/` :

- **`test-dashboard.html`** - Dashboard visuel interactif
- **`test-summary.md`** - Résumé détaillé en Markdown
- **`backend/`** - Rapports des tests backend
- **`frontend/`** - Rapports des tests frontend
- **`stress/`** - Résultats des tests de stress
- **`performance/`** - Résultats des tests de performance
- **`integration/`** - Résultats des tests d'intégration

## ⚙️ Prérequis

### Outils Requis :
- **Java** (JDK 17+)
- **Maven** (3.6+)
- **Node.js** (16+)
- **npm**
- **Docker** & **Docker Compose** (pour les tests d'intégration)

### Outils Optionnels :
- **K6** (pour les tests de performance avancés)
- **JMeter** (pour les tests de stress avancés)

## 🔧 Configuration

### Variables d'Environnement :
```bash
export SPRING_PROFILES_ACTIVE=test
export STRESS_TESTS_ENABLED=true
export LOAD_TESTS_ENABLED=true
```

### Ports Utilisés :
- **4200** - Frontend Angular
- **8080** - Backend API
- **5433** - PostgreSQL Database
- **8082** - pgAdmin

## 📋 Portes de Qualité

Le script vérifie les critères suivants :

- ✅ Tous les tests critiques passent
- ✅ Taux de réussite ≥ 80%
- ✅ Exigences de performance respectées
- ✅ Tests de sécurité passés
- ✅ Aucune fuite mémoire détectée

## 🚨 Gestion des Erreurs

- Les tests critiques (unitaires, intégration, E2E) arrêtent l'exécution en cas d'échec
- Les tests optionnels (stress, performance) sont marqués comme ignorés s'ils échouent
- Tous les logs sont sauvegardés pour analyse
- Un rapport détaillé est généré à la fin

## 🔄 CI/CD Integration

Ces scripts peuvent être intégrés dans un pipeline CI/CD :

```yaml
# Exemple GitHub Actions
- name: Run Test Suite
  run: |
    chmod +x scripts/run-all-tests.sh
    ./scripts/run-all-tests.sh
```

## 📞 Support

Pour toute question ou problème avec les scripts de test, consultez :
- Les logs générés dans `../reports/`
- Le dashboard HTML pour une vue d'ensemble
- Le résumé Markdown pour les détails techniques
