# Rapport de Conformité et Synthèse de la PoC

## 1. Objet et périmètre
- Démontrer la conformité du projet (PoC MedHead Hospital Allocation) aux exigences fonctionnelles et non-fonctionnelles.
- Justifier les choix technologiques, l’adhérence aux normes/bonnes pratiques, et présenter les résultats de fonctionnement, ainsi que les enseignements tirés.

## 2. Contexte et exigences
- Domaine: Allocation d’hôpital le plus pertinent selon la spécialité et la proximité.
- Exigences clés:
  - Fonctionnelles: API d’allocation, géocodage, validation front, parcours utilisateur.
  - Non-fonctionnelles: Performance (p95 < 200ms à charge cible), fiabilité (<1% d’erreurs), testabilité (TDD/BDD), observabilité, conformité RGPD.

## 3. Architecture et technologies
- Backend: Spring Boot (Java), JPA/Hibernate, PostgreSQL.
- Frontend: Angular, Cypress pour E2E, Jasmine/Karma pour unit tests.
- Tests de charge: k6.
- Conteneurisation/CI: Docker, GitHub Actions (pyramide de tests + rapports).

### Justification des choix
- Spring Boot/JPA: productivité, écosystème mature, intégration testing (JUnit, Mockito), profilage par environnements.
- Angular: structure modulaire, tooling de tests intégré, DX robuste.
- k6: scripting simple, intégration CI/CD, métriques standard (latences p95/p99, erreurs).
- PostgreSQL: fiabilité, support d’index et de vues matérialisées.
- Docker/Compose: reproductibilité et parité environnement.

## 4. Conformité aux normes, principes et bonnes pratiques
- Qualité logicielle: TDD + BDD documentés (voir `backend/TESTS.md`) et pyramidage.
- Séparation des préoccupations: couches contrôleur/service/repository clairement distinctes.
- Observabilité: métriques Micrometer/Prometheus (planifiées), logs structurés, profils `dev/test/prod`.
- Sécurité et RGPD: anonymisation patients (tests dédiés), gestion des erreurs et validations.
- Performance: objectifs explicites, seuils k6, recommandations d’optimisation.
- CI/CD: scripts et rapports HTML agrégés (voir `reports/latest-report.html`).

## 5. Résultats de fonctionnement de la PoC
### 5.1 Frontend – corrections tests
- Échecs initiaux: 11 tests (principalement `HospitalAllocationComponent`, `GeocodingService`, `AppComponent`).
- Actions: mock systématique de `/api/health`, assertions adaptées, normalisation du tagName.
- Résultat attendu: pipeline frontend rétabli, couverture et stabilité accrues.

Référence: `FRONTEND_TESTS_FIX_SUMMARY.md`.

### 5.2 Tests de charge k6
- Scénario rapide validé: p95 ≈ 14.61ms, erreurs 0.00%, ~165 req/s.
- Campagne prévue: montée progressive jusqu’à 800 req/s, seuils `p95<200ms`, `erreurs<1%`.
- Scripts et guide: `quick-test.js`, `test.js`, `LOAD_TEST_GUIDE.md`, `demo-load-test.sh`, `install-k6.sh`.

Référence: `LOAD_TEST_SUMMARY.md`.

### 5.3 Analyse et recommandations de performance
- Constat sous charge 800 req/s: p95 ≈ 532ms, erreurs ≈ 45.5% (non conforme aux seuils), point de rupture ~300-400 req/s.
- Goulots identifiés: requêtes BD sans index, calculs de distances à la volée, absence de cache, tuning JVM insuffisant.
- Plan d’optimisation par phases: index SQL, tuning Hikari/Tomcat/JVM, cache Redis, pré-calculs, vues matérialisées, parallélisation et monitoring.

Références: `PERFORMANCE_ANALYSIS.md`, `PERFORMANCE_RECOMMENDATIONS.md`.

### 5.4 Pyramide de tests backend
- Stratégie: TDD (unitaires + intégration) et BDD (Cucumber), profils Maven dédiés, répertoires de rapports.
- Objectifs de couverture et temps d’exécution définis, exécution par profils et scripts.

Référence: `backend/TESTS.md`.

### 5.5 Rapport agrégé
- `reports/latest-report.html` atteste l’exécution complète des niveaux de tests et fournit les liens d’accès détaillés.

## 6. Évaluation de la conformité
- Fonctionnelle: validée sur parcours principaux (tests unitaires/intégration/BDD + E2E). 
- Performance: conforme à 200 req/s (p95 ~79ms), non conforme à 800 req/s avec l’état actuel; plan d’actions établi pour atteindre la cible via optimisations + scaling.
- Fiabilité: taux d’erreurs 0% sur scénarios conservateurs; dérive au-delà du point de rupture identifié.
- Testabilité et traçabilité: élevées (documents, scripts, rapports). 

## 7. Enseignements tirés
- Importance des mocks réseau systématiques côté front pour la stabilité des tests.
- Nécessité d’indexation et de cache pour soutenir de fortes charges.
- Le dimensionnement (threads, pool BD, GC) est déterminant pour la latence.
- Le scaling horizontal complète les optimisations applicatives pour atteindre 800 req/s.

## 8. Décisions et suites proposées
- Court terme (1-2 jours): créer index SQL clés, ajuster Hikari/Tomcat/JVM, revérifier `p95<200ms` à 400-500 req/s.
- Moyen terme (3-5 jours): introduire Redis Cache et pré-calculs; revalider sous 600-700 req/s.
- Long terme (1-2 semaines): vues matérialisées, parallélisation et monitoring complet; viser 800 req/s avec scaling horizontal.

## 9. Annexes (extraits utiles)
- Index SQL proposés (extrait):
```sql
CREATE INDEX idx_hospital_specialty ON hospitals(specialty);
CREATE INDEX idx_hospital_location ON hospitals(latitude, longitude);
CREATE INDEX idx_hospital_beds ON hospitals(available_beds);
CREATE INDEX idx_hospital_specialty_beds ON hospitals(specialty, available_beds);
```
- Seuils k6 (extrait):
```javascript
thresholds: {
  http_req_duration: ['p(95)<200', 'p(99)<500'],
  http_req_failed: ['rate<0.01']
}
```

---
Dernière mise à jour: automatique. Sources: documents de synthèse en racine du dépôt et répertoires `backend/`, `frontend/`, `reports/`. 