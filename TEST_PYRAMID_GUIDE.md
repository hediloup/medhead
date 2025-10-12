# 🏥 Guide Complet de la Pyramide de Tests MedHead

Ce guide présente la suite complète de tests implémentée pour le projet MedHead, organisée selon la pyramide de tests (Tests Unitaires → Tests d'Intégration → Tests E2E).

## 📋 Table des Matières

1. [Vue d'ensemble](#vue-densemble)
2. [Architecture des Tests](#architecture-des-tests)
3. [Scripts de Test](#scripts-de-test)
4. [Configuration et Installation](#configuration-et-installation)
5. [Exécution des Tests](#exécution-des-tests)
6. [Rapports et Monitoring](#rapports-et-monitoring)
7. [Bonnes Pratiques](#bonnes-pratiques)
8. [Dépannage](#dépannage)

## 🎯 Vue d'ensemble

La pyramide de tests MedHead implémente une approche complète de validation logicielle :

```
        🌐 Tests E2E (Cypress)
       ┌─────────────────────┐
      │  Interface utilisateur │  ← Peu nombreux, lents, coûteux
      │  Scénarios complets    │
      └─────────────────────┘
     ┌─────────────────────────┐
    │    Tests d'Intégration    │  ← Nombre modéré, moyens
    │  API REST + Base données  │
    └─────────────────────────┘
   ┌─────────────────────────────┐
  │      Tests Unitaires         │  ← Nombreux, rapides, peu coûteux
  │  Services + Contrôleurs      │
  └─────────────────────────────┘
```

### Types de Tests Implémentés

| Niveau | Technologie | Objectif | Fréquence |
|--------|-------------|----------|-----------|
| **Unitaires** | JUnit + Mockito (Backend)<br/>Jasmine + Karma (Frontend) | Validation des composants individuels | À chaque commit |
| **Intégration** | Spring Boot Test + H2 | Validation des interactions entre composants | À chaque build |
| **BDD** | Cucumber + Gherkin | Validation des comportements métier | À chaque release |
| **E2E** | Cypress | Validation de l'expérience utilisateur | Quotidienne |

## 🏗️ Architecture des Tests

### Structure des Répertoires

```
medhead/
├── run-complete-test-suite.sh      # 🚀 Script principal de test
├── setup-test-environment.sh       # ⚙️ Configuration de l'environnement
├── test-performance-monitor.sh     # 📊 Monitoring des performances
├── TEST_PYRAMID_GUIDE.md          # 📖 Ce guide
├── reports/                        # 📊 Rapports de test
│   ├── backend/
│   │   ├── unit-tests/
│   │   ├── integration-tests/
│   │   └── bdd-tests/
│   ├── frontend/
│   │   ├── unit-tests/
│   │   └── e2e-tests/
│   └── performance/
├── backend/
│   └── src/test/
│       ├── java/com/medhead/poc/
│       │   ├── unit/               # Tests unitaires TDD
│       │   ├── integration/        # Tests d'intégration TDD
│       │   └── bdd/               # Tests BDD Cucumber
│       └── resources/
│           └── features/           # Scénarios Gherkin
└── frontend/
    ├── src/                        # Tests unitaires Angular
    └── cypress/
        └── e2e/                    # Tests E2E Cypress
```

## 🚀 Scripts de Test

### 1. Script Principal : `run-complete-test-suite.sh`

Le script principal orchestre l'exécution complète de la pyramide de tests.

#### Utilisation

```bash
# Exécution complète de tous les tests
./run-complete-test-suite.sh

# Exécution avec options
./run-complete-test-suite.sh --skip-e2e --performance

# Aide
./run-complete-test-suite.sh --help
```

#### Options Disponibles

| Option | Description |
|--------|-------------|
| `--skip-unit` | Ignorer les tests unitaires |
| `--skip-integration` | Ignorer les tests d'intégration |
| `--skip-bdd` | Ignorer les tests BDD |
| `--skip-e2e` | Ignorer les tests E2E |
| `--performance` | Inclure les tests de performance |
| `--no-reports` | Ne pas générer de rapports |
| `--no-cleanup` | Ne pas nettoyer après les tests |

#### Fonctionnalités

- ✅ **Vérification des prérequis** : Java, Node.js, Docker, Cypress
- ✅ **Exécution séquentielle** : Tests unitaires → Intégration → BDD → E2E
- ✅ **Gestion d'erreurs** : Arrêt en cas d'échec avec rapport détaillé
- ✅ **Génération de rapports** : HTML consolidé avec liens vers tous les rapports
- ✅ **Nettoyage automatique** : Arrêt des services Docker après les tests
- ✅ **Logs colorés** : Affichage clair des étapes et résultats

### 2. Script de Configuration : `setup-test-environment.sh`

Configure l'environnement complet pour les tests.

#### Utilisation

```bash
# Configuration complète
./setup-test-environment.sh

# Configuration sans Docker
./setup-test-environment.sh --skip-docker

# Aide
./setup-test-environment.sh --help
```

#### Fonctionnalités

- ✅ **Installation automatique** : Java, Maven, Node.js, Docker, Cypress
- ✅ **Configuration des variables** : Variables d'environnement et profils
- ✅ **Création des répertoires** : Structure complète des rapports
- ✅ **Validation** : Vérification de l'installation
- ✅ **Instructions post-installation** : Guide des prochaines étapes

### 3. Script de Monitoring : `test-performance-monitor.sh`

Collecte et analyse les métriques de performance pendant les tests.

#### Utilisation

```bash
# Monitoring de 5 minutes
./test-performance-monitor.sh

# Monitoring personnalisé
./test-performance-monitor.sh --duration 600

# Monitoring sans métriques Docker
./test-performance-monitor.sh --skip-docker

# Aide
./test-performance-monitor.sh --help
```

#### Métriques Collectées

- 📊 **Système** : CPU, mémoire, disque, charge système
- 🐳 **Docker** : Ressources des conteneurs (CPU, mémoire, réseau)
- 🌐 **Réseau** : Trafic entrant/sortant, paquets
- 🔍 **Applications** : Statut des services MedHead

## ⚙️ Configuration et Installation

### Prérequis Système

- **OS** : Linux (Ubuntu 20.04+ recommandé)
- **Java** : OpenJDK 17+
- **Node.js** : 18+
- **Docker** : 20.10+
- **Maven** : 3.8+
- **Git** : 2.30+

### Installation Automatique

```bash
# 1. Cloner le projet
git clone <repository-url>
cd medhead

# 2. Configuration automatique
./setup-test-environment.sh

# 3. Redémarrer le terminal
source ~/.bashrc

# 4. Vérifier l'installation
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
cd ../docker && docker-compose up -d
```

## 🧪 Exécution des Tests

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

## 📊 Rapports et Monitoring

### Structure des Rapports

```
reports/
├── latest-report.html                    # 📊 Rapport consolidé
├── backend/
│   ├── unit-tests/
│   │   ├── index.html                   # Rapport Surefire
│   │   └── TEST-*.xml                   # Fichiers XML
│   ├── integration-tests/
│   │   ├── index.html                   # Rapport Failsafe
│   │   └── TEST-*.xml                   # Fichiers XML
│   └── bdd-tests/
│       ├── cucumber.html                # Rapport Cucumber
│       └── cucumber.json                # Données JSON
├── frontend/
│   ├── unit-tests/
│   │   ├── index.html                   # Rapport Karma
│   │   └── coverage/                    # Rapport de couverture
│   └── e2e-tests/
│       ├── index.html                   # Rapport Cypress
│       └── results-*.xml                # Fichiers JUnit
└── performance/
    ├── latest-performance-report.html   # 📈 Rapport performance
    ├── system/                          # Métriques système
    ├── docker/                          # Métriques Docker
    ├── network/                         # Métriques réseau
    └── applications/                    # Statut applications
```

### Accès aux Rapports

```bash
# Ouvrir le rapport consolidé
xdg-open reports/latest-report.html

# Ouvrir le rapport de performance
xdg-open reports/performance/latest-performance-report.html

# Consulter les logs
tail -f reports/test-execution.log
```

### Métriques Disponibles

#### Tests Unitaires
- **Couverture de code** : Lignes, branches, fonctions
- **Temps d'exécution** : Par test et global
- **Taux de succès** : Pourcentage de tests passés

#### Tests d'Intégration
- **Performance API** : Temps de réponse moyen
- **Base de données** : Requêtes et transactions
- **Résilience** : Gestion des erreurs

#### Tests BDD
- **Scénarios métier** : Couverture des features
- **Documentation vivante** : Scénarios exécutables
- **Acceptance criteria** : Validation des exigences

#### Tests E2E
- **Expérience utilisateur** : Parcours complets
- **Compatibilité navigateur** : Chrome, Firefox, Safari
- **Performance frontend** : Temps de chargement

## 📈 Bonnes Pratiques

### 1. Organisation des Tests

#### Structure AAA (Arrange-Act-Assert)

```java
@Test
public void shouldAllocateNearestHospital() {
    // Arrange
    Patient patient = createPatientWithLocation(48.8566, 2.3522);
    Hospital nearestHospital = createHospitalWithLocation(48.8566, 2.3522);
    
    // Act
    AllocationResult result = allocationService.allocateHospital(patient);
    
    // Assert
    assertThat(result.getHospital()).isEqualTo(nearestHospital);
    assertThat(result.getDistance()).isLessThan(5.0);
}
```

#### Noms Descriptifs

```java
// ✅ Bon
@Test
public void shouldReturnErrorWhenNoSpecialtyAvailable() { }

// ❌ Mauvais
@Test
public void test1() { }
```

### 2. Gestion des Données de Test

#### Factories de Test

```java
public class TestDataFactory {
    public static Patient createPatient(String name, double lat, double lon) {
        return Patient.builder()
            .name(name)
            .location(Location.of(lat, lon))
            .build();
    }
}
```

#### Base de Données de Test

```java
@Test
@Transactional
@Rollback
public void shouldPersistPatient() {
    // Test avec base H2 en mémoire
    Patient patient = createTestPatient();
    patientRepository.save(patient);
    
    assertThat(patientRepository.findById(patient.getId())).isPresent();
}
```

### 3. Tests BDD Efficaces

#### Scénarios Clairs

```gherkin
Fonctionnalité: Allocation d'hôpitaux
  En tant que médecin urgentiste
  Je veux allouer le meilleur hôpital à un patient
  Afin de garantir les soins les plus appropriés

  Scénario: Allocation réussie avec spécialité disponible
    Étant donné qu'un patient a besoin d'une spécialité "cardiologie"
    Et qu'il existe des hôpitaux avec cette spécialité
    Quand je demande l'allocation d'un hôpital
    Alors je reçois l'hôpital le plus proche
    Et le temps de trajet estimé est fourni
```

#### Steps Réutilisables

```java
@Étantdonné("qu'un patient a besoin d'une spécialité {string}")
public void patient_avec_specialite(String specialite) {
    // Implémentation réutilisable
}
```

### 4. Tests E2E Robustes

#### Sélecteurs Stables

```javascript
// ✅ Bon - Sélecteur stable
cy.get('[data-testid="hospital-search-button"]')

// ❌ Mauvais - Sélecteur fragile
cy.get('.btn.btn-primary.col-md-3')
```

#### Attentes Explicites

```javascript
// ✅ Bon - Attente explicite
cy.get('[data-testid="results"]').should('be.visible')
cy.get('[data-testid="loading"]').should('not.exist')

// ❌ Mauvais - Attente implicite
cy.wait(5000)
```

## 🔧 Dépannage

### Problèmes Courants

#### 1. Tests Backend qui Échouent

```bash
# Vérifier la configuration Maven
mvn clean test -X

# Vérifier les profils
mvn help:active-profiles

# Vérifier la base de données H2
mvn test -Dspring.profiles.active=test -Dlogging.level.org.h2=DEBUG
```

**Solutions :**
- Vérifier que `application-test.properties` est présent
- S'assurer que H2 est dans les dépendances
- Nettoyer le cache Maven : `mvn clean`

#### 2. Tests Frontend qui Échouent

```bash
# Vérifier les dépendances
npm ls

# Réinstaller les dépendances
rm -rf node_modules package-lock.json
npm install

# Vérifier la configuration Karma
npm run test -- --help
```

**Solutions :**
- Mettre à jour les dépendances : `npm update`
- Vérifier la version de Node.js : `node --version`
- Nettoyer le cache npm : `npm cache clean --force`

#### 3. Tests E2E qui Échouent

```bash
# Vérifier les services Docker
docker-compose ps

# Vérifier les logs
docker-compose logs

# Redémarrer les services
docker-compose down && docker-compose up -d
```

**Solutions :**
- Vérifier que les services sont UP avant les tests E2E
- Augmenter les timeouts dans `cypress.config.js`
- Vérifier la connectivité réseau

#### 4. Problèmes de Performance

```bash
# Monitoring en temps réel
./test-performance-monitor.sh --duration 60

# Vérifier les ressources système
htop
docker stats

# Analyser les logs de performance
tail -f reports/performance/performance-monitor-*.log
```

**Solutions :**
- Augmenter les ressources Docker
- Optimiser les requêtes de base de données
- Réduire la parallélisation des tests

### Logs et Debug

#### Activation des Logs Détaillés

```bash
# Backend
mvn test -Dlogging.level.com.medhead=DEBUG

# Frontend
npm test -- --log-level debug

# Cypress
npm run e2e -- --config video=true,reporter=spec
```

#### Analyse des Rapports

```bash
# Ouvrir les rapports HTML
xdg-open reports/latest-report.html

# Analyser les métriques de performance
cat reports/performance/system/system-metrics-*.csv

# Vérifier les tests échoués
grep -r "FAILED" reports/
```

## 📚 Ressources Supplémentaires

### Documentation Officielle

- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)
- [Cucumber Java](https://cucumber.io/docs/cucumber/)
- [Cypress Testing](https://docs.cypress.io/)
- [JUnit 4](https://junit.org/junit4/)
- [Mockito](https://site.mockito.org/)

### Outils Recommandés

- **IDE** : IntelliJ IDEA, VS Code
- **Base de données** : DBeaver, pgAdmin
- **Monitoring** : Grafana, Prometheus
- **CI/CD** : GitHub Actions, GitLab CI

### Formation et Bonnes Pratiques

- [Test-Driven Development](https://martinfowler.com/bliki/TestDrivenDevelopment.html)
- [Behavior-Driven Development](https://cucumber.io/docs/bdd/)
- [Testing Pyramid](https://martinfowler.com/articles/practical-test-pyramid.html)

---

## 🎉 Conclusion

Cette suite de tests complète offre une couverture exhaustive du projet MedHead selon les meilleures pratiques de la pyramide de tests. Elle garantit la qualité, la fiabilité et la maintenabilité du code tout en fournissant une documentation vivante du système.

Pour toute question ou suggestion d'amélioration, consultez l'équipe de développement ou créez une issue dans le repository.

**Happy Testing! 🧪✨**
