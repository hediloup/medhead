# Tests Frontend MedHead

Ce document décrit la stratégie de test et les instructions pour exécuter les tests du frontend MedHead.

## Vue d'ensemble

Le frontend MedHead utilise une approche de test multi-niveaux :

- **Tests unitaires** : Testent les composants, services et modèles individuellement
- **Tests E2E** : Testent l'application complète du point de vue de l'utilisateur
- **Tests de performance** : Vérifient les performances et la réactivité de l'application

## Structure des tests

```
frontend/
├── src/
│   ├── app/
│   │   ├── components/
│   │   │   └── *.spec.ts          # Tests unitaires des composants
│   │   ├── services/
│   │   │   └── *.spec.ts          # Tests unitaires des services
│   │   └── models/
│   │       └── *.spec.ts          # Tests des modèles
│   └── ...
├── cypress/
│   ├── e2e/
│   │   ├── frontend-ui.cy.js      # Tests E2E de l'interface utilisateur
│   │   ├── performance.cy.js      # Tests de performance
│   │   ├── api-health.cy.js       # Tests de santé de l'API
│   │   └── form-validation.cy.js  # Tests de validation des formulaires
│   └── ...
├── run-tests.sh                   # Script principal pour exécuter les tests
├── run-tests-ci.sh               # Script pour les tests CI/CD
└── karma.conf.js                 # Configuration Karma pour les tests unitaires
```

## Types de tests

### Tests unitaires

Les tests unitaires utilisent **Jasmine** et **Karma** pour tester :

- **Composants** : Logique, rendu, interactions utilisateur
- **Services** : Appels API, gestion d'erreurs, transformation de données
- **Modèles** : Validation des structures de données

#### Exemples de tests unitaires

```typescript
// Test de composant
describe('HospitalAllocationComponent', () => {
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should validate form correctly', () => {
    component.onSubmit();
    expect(component.allocationForm.invalid).toBeTruthy();
  });
});

// Test de service
describe('AllocationService', () => {
  it('should make POST request to /api/allocate', () => {
    service.allocateHospital(mockRequest).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });
  });
});
```

### Tests E2E

Les tests E2E utilisent **Cypress** pour tester :

- **Interface utilisateur** : Navigation, formulaires, affichage des résultats
- **Intégration API** : Appels de géocodage et d'allocation
- **Gestion d'erreurs** : Messages d'erreur, états de chargement
- **Responsive design** : Affichage sur différents appareils

#### Exemples de tests E2E

```javascript
describe('MedHead Application E2E Tests', () => {
  it('should handle form submission with valid data', () => {
    cy.get('#specialty').select('Cardiology');
    cy.get('#address').type('Paris, France');
    cy.get('button[type="submit"]').click();
    
    cy.contains('Hôpital Saint-Antoine').should('be.visible');
  });
});
```

## Exécution des tests

### Prérequis

- Node.js (version 16 ou supérieure)
- npm ou yarn
- Chrome (pour les tests E2E)

### Installation des dépendances

```bash
npm install
```

### Tests unitaires

```bash
# Exécuter les tests unitaires
npm run test

# Tests unitaires avec couverture
npm run test:coverage

# Tests unitaires en mode watch
npm run test:watch
```

### Tests E2E

```bash
# Ouvrir Cypress en mode interactif
npm run e2e

# Exécuter les tests E2E en mode headless
npm run e2e:headless

# Tests E2E pour CI/CD
npm run e2e:ci
```

### Scripts de test

#### Script principal

```bash
# Tous les tests
./run-tests.sh all

# Tests unitaires seulement
./run-tests.sh unit

# Tests E2E seulement
./run-tests.sh e2e

# Tests avec couverture
./run-tests.sh coverage
```

#### Script CI/CD

```bash
# Exécuter tous les tests en mode CI/CD
./run-tests-ci.sh
```

## Configuration

### Karma (Tests unitaires)

Le fichier `karma.conf.js` configure :

- **Browsers** : Chrome par défaut, ChromeHeadless pour CI
- **Coverage** : Rapport de couverture avec seuils (80%)
- **Timeout** : 30 secondes pour les tests
- **Preprocessors** : TypeScript et couverture

### Cypress (Tests E2E)

Le fichier `cypress.config.js` configure :

- **Base URL** : http://localhost:4200
- **Timeouts** : 10 secondes pour les commandes, 30 secondes pour le chargement
- **Retry** : 2 tentatives en mode run
- **Video** : Enregistrement des vidéos des tests
- **Screenshots** : Captures d'écran en cas d'échec

## Rapports de test

### Couverture de code

Les rapports de couverture sont générés dans le dossier `coverage/` :

- **HTML** : `coverage/index.html` - Rapport interactif
- **LCOV** : `coverage/lcov.info` - Pour les outils CI/CD
- **JSON** : `coverage/coverage-final.json` - Données brutes

### Rapports E2E

Les rapports E2E sont générés dans le dossier `reports/` :

- **Screenshots** : `reports/screenshots/` - Captures d'écran des échecs
- **Videos** : `reports/videos/` - Vidéos des tests

## Seuils de qualité

### Couverture de code

- **Statements** : 80%
- **Branches** : 80%
- **Functions** : 80%
- **Lines** : 80%

### Performance

- **Chargement de page** : < 3 secondes
- **Temps de réponse API** : < 5 secondes
- **Mémoire** : Augmentation < 50MB après 20 requêtes

## Intégration CI/CD

### GitHub Actions

```yaml
name: Frontend Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-node@v2
        with:
          node-version: '16'
      - run: npm ci
      - run: npm run build
      - run: npm run test:coverage
      - run: npm run e2e:ci
```

### Variables d'environnement

```bash
# Pour les tests E2E
CYPRESS_baseUrl=http://localhost:4200
CYPRESS_apiUrl=http://localhost:4200/api
CYPRESS_geocodingUrl=http://localhost:4200/geocoding

# Pour les tests unitaires
CI=true
```

## Débogage des tests

### Tests unitaires

```bash
# Tests avec logs détaillés
npm run test -- --log-level=debug

# Tests d'un fichier spécifique
npm run test -- --include="**/allocation.service.spec.ts"

# Tests avec source maps
npm run test -- --source-map=true
```

### Tests E2E

```bash
# Mode interactif pour débogage
npm run e2e

# Tests avec logs détaillés
npm run e2e:headless -- --browser chrome --headed

# Tests d'un fichier spécifique
npx cypress run --spec "cypress/e2e/frontend-ui.cy.js"
```

## Bonnes pratiques

### Tests unitaires

1. **Isolation** : Chaque test doit être indépendant
2. **Mocks** : Utiliser des mocks pour les dépendances externes
3. **Assertions** : Vérifier le comportement attendu, pas l'implémentation
4. **Nommage** : Noms descriptifs pour les tests

### Tests E2E

1. **Données de test** : Utiliser des données cohérentes et réalistes
2. **Attentes** : Attendre les éléments avant d'interagir avec eux
3. **Nettoyage** : Réinitialiser l'état entre les tests
4. **Stabilité** : Éviter les tests flaky

### Performance

1. **Timeouts** : Définir des timeouts appropriés
2. **Parallélisation** : Exécuter les tests en parallèle quand possible
3. **Optimisation** : Réduire le temps d'exécution des tests
4. **Monitoring** : Surveiller les performances des tests

## Dépannage

### Problèmes courants

1. **Tests lents** : Vérifier les timeouts et les attentes
2. **Tests flaky** : Ajouter des attentes explicites
3. **Erreurs de couverture** : Vérifier la configuration Karma
4. **Échecs E2E** : Vérifier que l'application est démarrée

### Logs utiles

```bash
# Logs des tests unitaires
npm run test -- --log-level=debug

# Logs Cypress
npx cypress run --browser chrome --headed

# Logs de l'application
npm run start -- --verbose
```

## Maintenance

### Mise à jour des tests

1. **Dépendances** : Maintenir les versions à jour
2. **Configuration** : Adapter la configuration aux nouvelles versions
3. **Tests** : Ajouter des tests pour les nouvelles fonctionnalités
4. **Documentation** : Maintenir la documentation à jour

### Métriques

- **Temps d'exécution** : Surveiller la durée des tests
- **Taux de réussite** : Maintenir un taux élevé de réussite
- **Couverture** : Maintenir la couverture de code
- **Stabilité** : Réduire les tests flaky
