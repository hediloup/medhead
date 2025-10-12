# 🚀 Pipelines CI/CD MedHead - GitHub Actions

## 🎯 Vue d'ensemble

Ce repository contient une implémentation complète de pipelines CI/CD pour le projet MedHead, utilisant GitHub Actions pour automatiser l'intégration continue, la livraison continue, et la sécurité.

## 📁 Structure des Pipelines

```
.github/workflows/
├── ci.yml              # 🔄 Intégration Continue
├── cd.yml              # 🚀 Livraison Continue  
├── security.yml        # 🔒 Sécurité & Qualité
└── release.yml         # 🏷️ Release Automatique

docker/
├── docker-compose.yml           # 🐳 Configuration locale
├── docker-compose.staging.yml  # 🚀 Configuration staging
├── docker-compose.production.yml # 🌟 Configuration production
├── nginx.staging.conf          # 🌐 Nginx staging
└── nginx.production.conf       # 🌐 Nginx production

.github/
└── SECRETS_SETUP.md           # 🔐 Configuration des secrets
```

## 🔄 Workflows Disponibles

### 1. CI - Intégration Continue (`ci.yml`)

**Déclencheurs :**
- Push sur `main` et `develop`
- Pull Request vers `main` et `develop`

**Fonctionnalités :**
- ✅ Tests unitaires Backend (JUnit + Mockito)
- ✅ Tests d'intégration Backend (Spring Boot Test + H2)
- ✅ Tests BDD Backend (Cucumber + Gherkin)
- ✅ Tests unitaires Frontend (Jasmine + Karma)
- ✅ Tests E2E Frontend (Cypress)
- ✅ Analyse qualité code (SonarQube)
- ✅ Build images Docker
- ✅ Génération de rapports consolidés

### 2. CD - Livraison Continue (`cd.yml`)

**Déclencheurs :**
- Push sur `main`
- Tags `v*.*.*`
- Workflow dispatch manuel

**Fonctionnalités :**
- ✅ Tests de performance
- ✅ Déploiement automatique staging
- ✅ Tests de régression sur staging
- ✅ Déploiement Blue-Green production
- ✅ Monitoring post-déploiement
- ✅ Notifications Slack et email

### 3. Security - Sécurité & Qualité (`security.yml`)

**Déclencheurs :**
- Push sur `main` et `develop`
- Pull Request
- Planification hebdomadaire
- Workflow dispatch

**Fonctionnalités :**
- ✅ Audit sécurité dépendances (OWASP + npm audit)
- ✅ Analyse code statique (SonarQube)
- ✅ Scan vulnérabilités (Snyk)
- ✅ Analyse SAST (CodeQL)
- ✅ Scan secrets (TruffleHog + GitLeaks)
- ✅ Conformité standards (Checkstyle + ESLint)
- ✅ Tests DAST (OWASP ZAP)

### 4. Release - Release Automatique (`release.yml`)

**Déclencheurs :**
- Tags `v*.*.*`
- Workflow dispatch avec version

**Fonctionnalités :**
- ✅ Validation format de version
- ✅ Mise à jour fichiers de version
- ✅ Génération changelog automatique
- ✅ Tests pré-release complets
- ✅ Build images avec tags de version
- ✅ Création release GitHub
- ✅ Déploiement production automatique

## 🛠️ Configuration Rapide

### 1. Prérequis

- Repository GitHub avec Actions activées
- Accès aux services externes (SonarCloud, Snyk, Slack)
- Serveurs de déploiement (staging + production)

### 2. Configuration des Secrets

Voir [SECRETS_SETUP.md](.github/SECRETS_SETUP.md) pour la configuration complète.

**Secrets principaux à configurer :**
```bash
# Infrastructure
STAGING_HOST, STAGING_USER, STAGING_SSH_KEY
PRODUCTION_HOST, PRODUCTION_USER, PRODUCTION_SSH_KEY

# Sécurité
POSTGRES_PASSWORD, JWT_SECRET, SSL_KEYSTORE_PASSWORD

# APIs
SONAR_TOKEN, SNYK_TOKEN, SLACK_WEBHOOK

# Notifications
EMAIL_USERNAME, EMAIL_PASSWORD, NOTIFICATION_EMAIL
```

### 3. Configuration des Environnements

Dans GitHub Settings > Environments :

**Staging :**
- Protection rules : 1 reviewer requis
- Secrets : STAGING_*

**Production :**
- Protection rules : 2 reviewers requis + wait timer 5min
- Secrets : PRODUCTION_*

## 🚀 Utilisation

### Déclenchement Automatique

#### Push sur `develop`
```bash
git push origin develop
# → Déclenche CI + Security
# → Déploiement automatique staging
```

#### Push sur `main`
```bash
git push origin main
# → Déclenche CI + CD + Security
# → Déploiement staging → production
```

#### Création de Release
```bash
git tag v1.2.3
git push origin v1.2.3
# → Déclenche Release workflow
# → Création release GitHub + déploiement production
```

### Déclenchement Manuel

#### Via GitHub UI
1. Aller dans **Actions**
2. Sélectionner le workflow souhaité
3. Cliquer sur **Run workflow**
4. Choisir les paramètres

#### Via CLI GitHub
```bash
# Déclenchement CI
gh workflow run ci.yml

# Déclenchement CD avec environnement
gh workflow run cd.yml --field environment=staging

# Déclenchement Release avec version
gh workflow run release.yml --field version=1.2.3
```

## 📊 Monitoring et Rapports

### Rapports Générés

#### Tests et Qualité
- **Rapports CI** : `/reports/ci-summary/`
- **Rapports Sécurité** : `/reports/security-summary/`
- **Rapports Performance** : `/reports/performance/`

#### Déploiement
- **Rapports Staging** : Validation des déploiements
- **Rapports Production** : Monitoring post-déploiement
- **Rapports Release** : Documentation des versions

### Dashboards Disponibles

#### Grafana (si configuré)
- **Application** : Métriques API, erreurs, performance
- **Infrastructure** : CPU, mémoire, réseau
- **CI/CD** : Durée pipelines, taux succès

#### SonarCloud
- **Qualité Code** : Bugs, vulnérabilités, code smells
- **Couverture** : Tests unitaires et intégration
- **Duplication** : Code dupliqué détecté

### Notifications

#### Slack
- **#medhead-ci** : Notifications CI/CD
- **#medhead-deployments** : Déploiements
- **#medhead-security** : Alertes sécurité

#### Email
- **Notifications critiques** : admin@medhead.com
- **Rapports hebdomadaires** : équipe@medhead.com

## 🔧 Personnalisation

### Modification des Workflows

#### Ajout de Tests
```yaml
# Dans ci.yml
- name: 🧪 Nouveaux Tests
  run: |
    echo "Exécution des nouveaux tests..."
    npm run test:custom
```

#### Modification des Environnements
```yaml
# Dans cd.yml
environment: ${{ github.event.inputs.environment || 'staging' }}
```

#### Ajout de Notifications
```yaml
# Ajout d'un canal Discord
- name: 📢 Notification Discord
  uses: Ilshidur/action-discord@master
  with:
    webhook: ${{ secrets.DISCORD_WEBHOOK }}
```

### Configuration Docker

#### Modification des Images
```dockerfile
# Dans Dockerfile
FROM custom-base-image:latest
# ... modifications ...
```

#### Ajout de Services
```yaml
# Dans docker-compose.production.yml
services:
  new-service:
    image: custom-service:latest
    # ... configuration ...
```

## 🐛 Dépannage

### Problèmes Courants

#### 1. Échec des Tests
```bash
# Diagnostic
- Vérifier les logs détaillés dans GitHub Actions
- Reproduire localement avec les mêmes paramètres
- Vérifier les versions des dépendances

# Solutions
- Corriger les tests défaillants
- Mettre à jour les dépendances
- Ajuster les timeouts si nécessaire
```

#### 2. Échec de Déploiement
```bash
# Diagnostic
- Vérifier la connectivité SSH vers les serveurs
- Contrôler les ressources disponibles
- Analyser les logs Docker sur le serveur

# Solutions
- Redémarrer les services Docker
- Libérer de l'espace disque
- Vérifier les configurations réseau
```

#### 3. Problèmes de Sécurité
```bash
# Diagnostic
- Analyser les rapports de sécurité générés
- Vérifier les nouvelles vulnérabilités
- Contrôler l'exposition des secrets

# Solutions
- Mettre à jour les dépendances vulnérables
- Corriger les failles de sécurité détectées
- Rotation des secrets compromis
```

### Commandes de Diagnostic

#### Vérification des Services
```bash
# Santé des services
curl -f https://staging.medhead.com/api/health
curl -f https://medhead.com/api/health

# Statut Docker
docker-compose -f docker-compose.staging.yml ps
docker-compose -f docker-compose.production.yml ps
```

#### Logs des Workflows
```bash
# Téléchargement des logs
gh run download <run-id>

# Consultation des logs en temps réel
gh run watch <run-id>
```

## 📚 Documentation Complète

### Guides Détaillés
- **[CI_CD_GUIDE.md](CI_CD_GUIDE.md)** - Guide complet des pipelines CI/CD
- **[SECRETS_SETUP.md](.github/SECRETS_SETUP.md)** - Configuration des secrets
- **[TEST_PYRAMID_GUIDE.md](TEST_PYRAMID_GUIDE.md)** - Guide de la pyramide de tests

### Ressources Externes
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [SonarCloud Documentation](https://docs.sonarcloud.io/)
- [Snyk Documentation](https://docs.snyk.io/)

## 🎯 Avantages de cette Implémentation

### ✅ Automatisation Complète
- **Développement** : Tests automatiques à chaque commit
- **Déploiement** : Mise en production automatique et sécurisée
- **Monitoring** : Surveillance continue des performances

### ✅ Qualité Garantie
- **Tests exhaustifs** : Pyramide complète (unitaires → E2E)
- **Analyse de sécurité** : Scan continu des vulnérabilités
- **Standards de code** : Respect des bonnes pratiques

### ✅ Fiabilité Opérationnelle
- **Déploiement Blue-Green** : Zéro downtime
- **Rollback automatique** : Récupération rapide en cas de problème
- **Monitoring intégré** : Détection proactive des incidents

### ✅ Sécurité Renforcée
- **Scan continu** : Dépendances, code, secrets
- **Compliance** : Respect des standards de sécurité
- **Audit trail** : Traçabilité complète des changements

## 🚀 Prochaines Étapes

1. **Configuration initiale** : Suivre le guide [SECRETS_SETUP.md](.github/SECRETS_SETUP.md)
2. **Premier déploiement** : Push sur `develop` pour tester staging
3. **Release initiale** : Créer un tag `v1.0.0` pour la première release
4. **Monitoring** : Configurer Grafana et les alertes
5. **Optimisation** : Ajuster les configurations selon les besoins

---

## 🎉 Conclusion

Cette implémentation CI/CD offre une solution complète et professionnelle pour le projet MedHead, garantissant :

- 🔄 **Intégration continue** avec tests automatisés
- 🚀 **Livraison continue** avec déploiement sécurisé
- 🔒 **Sécurité renforcée** avec analyses continues
- 📊 **Monitoring intégré** pour la surveillance proactive

**Ready to Deploy! 🚀✨**
