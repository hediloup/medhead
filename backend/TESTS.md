# Tests MedHead Backend

Ce document décrit la stratégie de tests implémentée pour le projet MedHead Backend, combinant les approches **TDD (Test-Driven Development)** et **BDD (Behavior-Driven Development)**.

## 🏗️ Architecture des Tests

### Structure des Dossiers

```
src/test/
├── java/com/medhead/poc/
│   ├── unit/                          # Tests TDD unitaires
│   │   ├── service/                   # Tests des services
│   │   └── controller/                # Tests des contrôleurs
│   ├── integration/                   # Tests TDD d'intégration
│   │   ├── AllocationIntegrationTest.java
│   │   └── RepositoryIntegrationTest.java
│   ├── bdd/                          # Tests BDD avec Cucumber
│   │   ├── steps/                    # Étapes des scénarios BDD
│   │   ├── runners/                  # Runners Cucumber
│   │   └── hooks/                    # Hooks de configuration
│   ├── TestSuite.java                # Suite de tests complète
│   └── PocApplicationTests.java      # Test de base Spring Boot
└── resources/
    ├── features/                     # Fichiers .feature Cucumber
    └── cucumber.properties          # Configuration Cucumber
```

## 🧪 Types de Tests

### 1. Tests TDD Unitaires

**Objectif :** Tester individuellement chaque composant (service, contrôleur) avec des mocks.

**Caractéristiques :**
- Tests rapides et isolés
- Utilisation de Mockito pour les dépendances
- Couverture de tous les cas de test (succès, erreurs, cas limites)
- Exécution en parallèle

**Tests inclus :**
- `AllocationServiceTest` : Logique métier d'allocation d'hôpitaux
- `AllocationControllerTest` : Endpoints REST et gestion des erreurs
- `DistanceCalculationServiceTest` : Calculs de distance et routes
- `PatientAnonymizationServiceTest` : Anonymisation et conformité RGPD

### 2. Tests TDD d'Intégration

**Objectif :** Tester l'intégration entre les composants avec une base de données réelle.

**Caractéristiques :**
- Tests avec Spring Boot Test
- Base de données H2 en mémoire
- Transactions rollback automatique
- Tests des repositories et de l'API REST complète

**Tests inclus :**
- `AllocationIntegrationTest` : Flux complet d'allocation via API REST
- `RepositoryIntegrationTest` : Tests des repositories JPA

### 3. Tests BDD

**Objectif :** Valider le comportement du système du point de vue des utilisateurs métier.

**Caractéristiques :**
- Scénarios écrits en français (Given-When-Then)
- Couverture des cas d'usage métier
- Documentation vivante du système
- Tests de bout en bout

**Features incluses :**
- `allocation-hospital.feature` : Scénarios d'allocation d'hôpitaux
- `anonymisation-patient.feature` : Scénarios d'anonymisation RGPD
- `calcul-distance.feature` : Scénarios de calcul de distance
- `performance-api.feature` : Scénarios de performance et charge

## 🚀 Exécution des Tests

### Scripts d'Exécution

#### Script Interactif (Recommandé)
```bash
./run-all-tests.sh
```

#### Commandes Maven Directes

**Tests par défaut (TDD unitaires + intégration) :**
```bash
mvn test
```

**Tests TDD unitaires uniquement :**
```bash
mvn test -P unit-tests
```

**Tests TDD d'intégration uniquement :**
```bash
mvn test -P integration-tests
```

**Tests BDD uniquement :**
```bash
mvn test -P bdd-tests
```

**Tests de performance uniquement :**
```bash
mvn test -P performance-tests
```

**Tous les tests (TDD + BDD) :**
```bash
mvn test -P all-tests
```

### Profils Maven Disponibles

| Profil | Description | Tests Inclus |
|--------|-------------|--------------|
| `unit-tests` | Tests TDD unitaires | Services et contrôleurs avec mocks |
| `integration-tests` | Tests TDD d'intégration | API REST et repositories |
| `bdd-tests` | Tests BDD | Scénarios Cucumber |
| `performance-tests` | Tests de performance | Tests de charge et stress |
| `all-tests` | Tous les tests | TDD + BDD (sans performance) |

## 📊 Rapports de Tests

### Localisation des Rapports

- **Tests TDD :** `target/surefire-reports/`
- **Tests BDD :** `target/cucumber-reports/`
- **Tests d'intégration :** `target/failsafe-reports/`

### Formats Disponibles

- **HTML :** Rapports visuels avec graphiques
- **JSON :** Données structurées pour intégration CI/CD
- **XML :** Format standard pour outils d'analyse

### Accès aux Rapports

```bash
# Ouvrir les rapports HTML
open target/surefire-reports/index.html          # Tests TDD
open target/cucumber-reports/cucumber.html       # Tests BDD
open target/failsafe-reports/index.html          # Tests d'intégration
```

## 🔧 Configuration

### Variables d'Environnement

```bash
export SPRING_PROFILES_ACTIVE=test
export CUCUMBER_OPTIONS="--plugin pretty --plugin html:target/cucumber-reports"
```

### Configuration Cucumber

Le fichier `src/test/resources/cucumber.properties` contient :
- Configuration des plugins de rapport
- Paramètres de timeout
- Configuration de la langue (français)
- Options de formatage

## 🎯 Couverture de Tests

### Scénarios Couverts

#### Allocation d'Hôpitaux
- ✅ Allocation réussie avec spécialité disponible
- ✅ Sélection de l'hôpital le plus proche
- ✅ Gestion des erreurs (pas de spécialité, pas de lits)
- ✅ Validation des paramètres d'entrée
- ✅ Endpoints GET et POST

#### Anonymisation des Patients
- ✅ Création de patients anonymisés
- ✅ Anonymisation de patients existants
- ✅ Gestion des permissions d'accès
- ✅ Suppression automatique des données expirées
- ✅ Génération de statistiques anonymisées

#### Calcul de Distance
- ✅ Calcul de distance entre points géographiques
- ✅ Estimation du temps de trajet
- ✅ Calcul de routes optimales avec trafic
- ✅ Gestion des erreurs de calcul

#### Performance de l'API
- ✅ Tests de charge (100 requêtes simultanées)
- ✅ Tests de débit (50 req/s)
- ✅ Tests de stress (charge progressive)
- ✅ Tests de récupération après surcharge
- ✅ Surveillance de la disponibilité

## 🐛 Dépannage

### Problèmes Courants

#### Tests BDD qui échouent
```bash
# Vérifier la configuration Cucumber
cat src/test/resources/cucumber.properties

# Vérifier les steps définis
grep -r "@Étantdonné\|@Quand\|@Alors" src/test/java/com/medhead/poc/bdd/steps/
```

#### Problèmes de base de données
```bash
# Nettoyer la base de données de test
mvn clean test

# Vérifier la configuration H2
grep -i h2 src/main/resources/application.properties
```

#### Problèmes de performance
```bash
# Exécuter les tests de performance séparément
mvn test -P performance-tests

# Vérifier les logs de performance
tail -f target/surefire-reports/com.medhead.poc.bdd.runners.PerformanceBddTest.txt
```

### Logs et Debug

#### Activer les logs détaillés
```bash
mvn test -Dspring.profiles.active=test -Dlogging.level.com.medhead=DEBUG
```

#### Logs spécifiques aux tests BDD
```bash
mvn test -P bdd-tests -Dcucumber.options="--plugin pretty --plugin html:target/cucumber-reports --glue com.medhead.poc.bdd.steps"
```

## 📈 Métriques de Qualité

### Objectifs de Couverture
- **Tests TDD unitaires :** > 90%
- **Tests TDD d'intégration :** > 80%
- **Tests BDD :** 100% des features critiques

### Temps d'Exécution Cibles
- **Tests unitaires :** < 30 secondes
- **Tests d'intégration :** < 2 minutes
- **Tests BDD :** < 5 minutes
- **Tests de performance :** < 10 minutes

## 🔄 Intégration CI/CD

### Pipeline de Tests Recommandé

1. **Phase 1 :** Tests TDD unitaires (validation rapide)
2. **Phase 2 :** Tests TDD d'intégration (validation fonctionnelle)
3. **Phase 3 :** Tests BDD (validation métier)
4. **Phase 4 :** Tests de performance (validation non-fonctionnelle)

### Configuration GitHub Actions

```yaml
- name: Run TDD Unit Tests
  run: mvn test -P unit-tests

- name: Run TDD Integration Tests
  run: mvn test -P integration-tests

- name: Run BDD Tests
  run: mvn test -P bdd-tests

- name: Run Performance Tests
  run: mvn test -P performance-tests
```

## 📚 Ressources

### Documentation
- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)
- [Cucumber Java](https://cucumber.io/docs/cucumber/)
- [JUnit 4](https://junit.org/junit4/)
- [Mockito](https://site.mockito.org/)

### Bonnes Pratiques
- Écrire les tests avant le code (TDD)
- Utiliser des noms de tests descriptifs
- Maintenir une couverture de tests élevée
- Séparer les tests unitaires des tests d'intégration
- Documenter les scénarios BDD en français

---

**Note :** Ce document est maintenu à jour avec l'évolution des tests. Pour toute question ou suggestion, consultez l'équipe de développement.
