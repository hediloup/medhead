# 🏥 Suite de Tests Complète MedHead

## 🎯 Vue d'ensemble

Cette suite de tests implémente une **pyramide de tests complète** pour le projet MedHead, couvrant tous les niveaux de validation logicielle :

- 🧪 **Tests Unitaires** (TDD) - Backend (JUnit/Mockito) + Frontend (Jasmine/Karma)
- 🔗 **Tests d'Intégration** (TDD) - API REST + Base de données H2
- 📋 **Tests BDD** (Cucumber) - Scénarios métier en français
- 🌐 **Tests E2E** (Cypress) - Interface utilisateur complète
- ⚡ **Tests de Performance** - Monitoring système et applications

## 🚀 Scripts Disponibles

### 1. `run-complete-test-suite.sh` - Script Principal
**Orchestration complète de la pyramide de tests**

```bash
# Exécution complète
./run-complete-test-suite.sh

# Options disponibles
./run-complete-test-suite.sh --skip-e2e --performance
./run-complete-test-suite.sh --help
```

**Fonctionnalités :**
- ✅ Vérification automatique des prérequis
- ✅ Exécution séquentielle selon la pyramide
- ✅ Gestion d'erreurs et rapports détaillés
- ✅ Génération de rapports HTML consolidés
- ✅ Nettoyage automatique des ressources

### 2. `setup-test-environment.sh` - Configuration
**Installation et configuration automatique de l'environnement**

```bash
# Configuration complète
./setup-test-environment.sh

# Configuration sans Docker
./setup-test-environment.sh --skip-docker
```

**Fonctionnalités :**
- ✅ Installation automatique : Java, Maven, Node.js, Docker, Cypress
- ✅ Configuration des variables d'environnement
- ✅ Création de la structure de répertoires
- ✅ Validation de l'installation
- ✅ Instructions post-installation

### 3. `test-performance-monitor.sh` - Monitoring
**Collecte et analyse des métriques de performance**

```bash
# Monitoring de 5 minutes
./test-performance-monitor.sh

# Monitoring personnalisé
./test-performance-monitor.sh --duration 600
```

**Métriques collectées :**
- 📊 Système : CPU, mémoire, disque, charge
- 🐳 Docker : Ressources des conteneurs
- 🌐 Réseau : Trafic entrant/sortant
- 🔍 Applications : Statut des services

### 4. `demo-test-suite.sh` - Démonstration
**Simulation de la suite de tests sans exécution réelle**

```bash
# Démonstration complète
./demo-test-suite.sh
```

**Fonctionnalités :**
- ✅ Génération de rapports simulés
- ✅ Démonstration des capacités
- ✅ Instructions d'utilisation
- ✅ Informations système

## 📊 Structure des Rapports

```
reports/
├── latest-report.html                    # 📊 Rapport principal consolidé
├── latest-demo-report.html              # 🎭 Rapport de démonstration
├── backend/
│   ├── unit-tests/index.html           # Tests unitaires (JUnit)
│   ├── integration-tests/index.html    # Tests d'intégration (Spring Boot)
│   └── bdd-tests/cucumber.html         # Tests BDD (Cucumber)
├── frontend/
│   ├── unit-tests/index.html           # Tests unitaires (Jasmine/Karma)
│   └── e2e-tests/index.html            # Tests E2E (Cypress)
└── performance/
    ├── latest-performance-report.html  # 📈 Rapport de performance
    ├── system/                         # Métriques système
    ├── docker/                         # Métriques Docker
    ├── network/                        # Métriques réseau
    └── applications/                   # Statut applications
```

## 🛠️ Installation et Configuration

### Prérequis
- **OS** : Linux (Ubuntu 20.04+ recommandé)
- **Java** : OpenJDK 17+
- **Node.js** : 18+
- **Docker** : 20.10+
- **Maven** : 3.8+

### Installation Automatique
```bash
# 1. Configuration automatique
./setup-test-environment.sh

# 2. Redémarrer le terminal
source ~/.bashrc

# 3. Vérifier l'installation
./run-complete-test-suite.sh --help
```

### Installation Manuelle
```bash
# Java et Maven
sudo apt-get update
sudo apt-get install openjdk-17-jdk maven

# Node.js
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs

# Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# Dépendances du projet
cd backend && mvn clean install -DskipTests
cd ../frontend && npm install
```

## 🧪 Utilisation

### Scénarios d'Utilisation

#### 1. Développement Local
```bash
# Tests rapides (unitaires + intégration)
./run-complete-test-suite.sh --skip-bdd --skip-e2e

# Tests complets avant commit
./run-complete-test-suite.sh --skip-e2e

# Tests E2E uniquement
./run-complete-test-suite.sh --skip-unit --skip-integration --skip-bdd
```

#### 2. Intégration Continue
```bash
# Pipeline CI/CD complet
./run-complete-test-suite.sh

# Pipeline rapide (validation)
./run-complete-test-suite.sh --skip-e2e --skip-performance

# Pipeline de release (complet)
./run-complete-test-suite.sh --performance
```

#### 3. Tests de Performance
```bash
# Monitoring pendant les tests
./test-performance-monitor.sh --duration 300 &
./run-complete-test-suite.sh --performance
```

### Commandes Individuelles

#### Backend
```bash
cd backend

# Tests unitaires
mvn test -P unit-tests

# Tests d'intégration
mvn test -P integration-tests

# Tests BDD
mvn test -P bdd-tests

# Tous les tests
mvn test
```

#### Frontend
```bash
cd frontend

# Tests unitaires
npm test

# Tests avec couverture
npm run test:coverage

# Tests E2E
npm run e2e

# Tests E2E en mode CI
npm run e2e:ci
```

## 📈 Métriques et Qualité

### Objectifs de Couverture
- **Tests unitaires** : > 90%
- **Tests d'intégration** : > 80%
- **Tests BDD** : 100% des features critiques
- **Tests E2E** : 100% des parcours utilisateur

### Temps d'Exécution Cibles
- **Tests unitaires** : < 30 secondes
- **Tests d'intégration** : < 2 minutes
- **Tests BDD** : < 5 minutes
- **Tests E2E** : < 10 minutes
- **Tests complets** : < 20 minutes

## 🔧 Configuration Avancée

### Variables d'Environnement
```bash
# Configuration de test
export SPRING_PROFILES_ACTIVE=test
export CUCUMBER_OPTIONS="--plugin pretty --plugin html:target/cucumber-reports"
export CYPRESS_BASE_URL=http://localhost:4200
export CYPRESS_REPORTS_DIR=reports/frontend/e2e-tests
```

### Profils Maven
| Profil | Description | Tests Inclus |
|--------|-------------|--------------|
| `unit-tests` | Tests unitaires | Services et contrôleurs avec mocks |
| `integration-tests` | Tests d'intégration | API REST et repositories |
| `bdd-tests` | Tests BDD | Scénarios Cucumber |
| `performance-tests` | Tests de performance | Tests de charge et stress |
| `all-tests` | Tous les tests | TDD + BDD (sans performance) |

## 🐛 Dépannage

### Problèmes Courants

#### Tests Backend qui Échouent
```bash
# Vérifier la configuration Maven
mvn clean test -X

# Vérifier les profils
mvn help:active-profiles

# Nettoyer le cache
mvn clean
```

#### Tests Frontend qui Échouent
```bash
# Vérifier les dépendances
npm ls

# Réinstaller les dépendances
rm -rf node_modules package-lock.json
npm install
```

#### Tests E2E qui Échouent
```bash
# Vérifier les services Docker
docker-compose ps

# Redémarrer les services
docker-compose down && docker-compose up -d
```

### Logs et Debug
```bash
# Activation des logs détaillés
mvn test -Dlogging.level.com.medhead=DEBUG
npm test -- --log-level debug

# Analyse des rapports
xdg-open reports/latest-report.html
tail -f reports/test-execution.log
```

## 📚 Documentation

- **[Guide Complet](TEST_PYRAMID_GUIDE.md)** - Documentation détaillée de la pyramide de tests
- **[Tests Backend](backend/TESTS.md)** - Documentation spécifique aux tests backend
- **[Configuration Docker](docker/README.md)** - Guide d'utilisation Docker

## 🎉 Avantages de cette Suite

### ✅ Couverture Complète
- **Pyramide de tests** respectée (beaucoup d'unitaires, peu d'E2E)
- **TDD et BDD** combinés pour une validation complète
- **Tests fonctionnels et non-fonctionnels** (performance)

### ✅ Automatisation
- **Scripts automatisés** pour tous les scénarios
- **Rapports HTML** générés automatiquement
- **Intégration CI/CD** prête

### ✅ Qualité
- **Standards de l'industrie** (JUnit, Cucumber, Cypress)
- **Bonnes pratiques** TDD/BDD
- **Documentation vivante** avec les scénarios Gherkin

### ✅ Monitoring
- **Métriques de performance** en temps réel
- **Surveillance système** pendant les tests
- **Rapports détaillés** avec graphiques

### ✅ Flexibilité
- **Options configurables** pour tous les types de tests
- **Exécution sélective** selon les besoins
- **Environnements multiples** (dev, test, prod)

---

## 🚀 Prochaines Étapes

1. **Exécuter la démonstration** : `./demo-test-suite.sh`
2. **Configurer l'environnement** : `./setup-test-environment.sh`
3. **Lancer les tests complets** : `./run-complete-test-suite.sh`
4. **Consulter les rapports** : `xdg-open reports/latest-report.html`

**Happy Testing! 🧪✨**
