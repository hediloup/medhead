# 🚀 Guide CI/CD MedHead - GitHub Actions

Ce guide présente les pipelines d'intégration et de livraison continue (CI/CD) complets pour le projet MedHead, implémentés avec GitHub Actions.

## 📋 Table des Matières

1. [Vue d'ensemble](#vue-densemble)
2. [Architecture des Pipelines](#architecture-des-pipelines)
3. [Workflows GitHub Actions](#workflows-github-actions)
4. [Configuration et Déploiement](#configuration-et-déploiement)
5. [Monitoring et Alertes](#monitoring-et-alertes)
6. [Bonnes Pratiques](#bonnes-pratiques)
7. [Dépannage](#dépannage)

## 🎯 Vue d'ensemble

Les pipelines CI/CD MedHead implémentent une stratégie complète de déploiement automatisé :

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   🔄 CI         │    │   🚀 CD         │    │   🔒 Security   │
│   Intégration   │───▶│   Livraison     │───▶│   Sécurité      │
│   Continue      │    │   Continue      │    │   & Qualité     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   🧪 Tests      │    │   🌟 Release    │    │   📊 Monitoring │
│   Automatisés   │    │   Automatisée   │    │   & Alertes     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Objectifs des Pipelines

- ✅ **Automatisation complète** : De la validation du code au déploiement en production
- ✅ **Qualité garantie** : Tests exhaustifs à chaque niveau
- ✅ **Sécurité renforcée** : Analyse de sécurité continue
- ✅ **Déploiement fiable** : Stratégie Blue-Green avec rollback automatique
- ✅ **Monitoring intégré** : Surveillance continue des performances

## 🏗️ Architecture des Pipelines

### Structure des Workflows

```
.github/workflows/
├── ci.yml              # 🔄 Intégration Continue
├── cd.yml              # 🚀 Livraison Continue  
├── security.yml        # 🔒 Sécurité & Qualité
└── release.yml         # 🏷️ Release Automatique
```

### Flux de Données

```
Push/PR → CI → Tests → Build → CD → Staging → Production
    ↓        ↓        ↓        ↓        ↓         ↓
  Trigger  Quality  Docker   Deploy   Validate  Monitor
```

## 🔄 Workflows GitHub Actions

### 1. CI - Intégration Continue (`ci.yml`)

**Déclencheurs :**
- Push sur `main` et `develop`
- Pull Request vers `main` et `develop`

**Jobs Exécutés :**

#### 🧪 Tests Backend
```yaml
backend-tests:
  - Tests unitaires (JUnit + Mockito)
  - Tests d'intégration (Spring Boot Test + H2)
  - Tests BDD (Cucumber + Gherkin)
```

#### 🎨 Tests Frontend
```yaml
frontend-tests:
  - Tests unitaires (Jasmine + Karma)
  - Tests E2E (Cypress)
  - Build de production
```

#### 🔍 Analyse Qualité Code
```yaml
code-quality:
  - Analyse SonarQube Backend
  - Analyse SonarQube Frontend
  - Métriques de qualité
```

#### 🐳 Build Images Docker
```yaml
docker-build:
  - Build Backend Image
  - Build Frontend Image
  - Push vers GitHub Container Registry
```

**Résultats :**
- ✅ Tous les tests passent
- ✅ Qualité du code validée
- ✅ Images Docker créées
- ✅ Rapports de tests générés

### 2. CD - Livraison Continue (`cd.yml`)

**Déclencheurs :**
- Push sur `main`
- Tags `v*.*.*`
- Workflow dispatch manuel

**Jobs Exécutés :**

#### ⚡ Tests de Performance
```yaml
performance-tests:
  - Tests de charge
  - Tests de stress
  - Validation des performances
```

#### 🚀 Déploiement Staging
```yaml
deploy-staging:
  - Build des images staging
  - Déploiement automatique
  - Validation des services
```

#### 🔄 Tests de Régression
```yaml
regression-tests:
  - Tests E2E sur staging
  - Validation fonctionnelle
  - Tests de compatibilité
```

#### 🌟 Déploiement Production
```yaml
deploy-production:
  - Build des images production
  - Déploiement Blue-Green
  - Validation et rollback automatique
```

#### 📊 Monitoring Post-Déploiement
```yaml
post-deployment-monitoring:
  - Tests de santé
  - Métriques de performance
  - Notifications
```

### 3. Security - Sécurité & Qualité (`security.yml`)

**Déclencheurs :**
- Push sur `main` et `develop`
- Pull Request
- Planification hebdomadaire
- Workflow dispatch

**Jobs Exécutés :**

#### 🔍 Sécurité Dépendances
```yaml
dependency-security:
  - Audit Maven (OWASP Dependency Check)
  - Audit npm (npm audit)
  - Scan des vulnérabilités
```

#### 🔍 Analyse Code Statique
```yaml
code-analysis:
  - Analyse SonarQube Backend
  - Analyse SonarQube Frontend
  - Métriques de qualité
```

#### 🛡️ Scan Vulnérabilités
```yaml
vulnerability-scan:
  - Scan Snyk Backend
  - Scan Snyk Frontend
  - Rapport de vulnérabilités
```

#### 🔍 Analyse SAST
```yaml
sast-analysis:
  - CodeQL Backend (Java)
  - CodeQL Frontend (JavaScript)
  - Détection de failles de sécurité
```

#### 🔐 Scan Secrets
```yaml
secrets-scan:
  - TruffleHog
  - GitLeaks
  - Détection de secrets exposés
```

#### 📋 Conformité & Standards
```yaml
compliance-check:
  - Checkstyle Java
  - ESLint JavaScript
  - Vérification des licences
```

#### 🌐 Tests DAST
```yaml
dast-testing:
  - OWASP ZAP Backend
  - OWASP ZAP Frontend
  - Tests de sécurité dynamiques
```

### 4. Release - Release Automatique (`release.yml`)

**Déclencheurs :**
- Tags `v*.*.*`
- Workflow dispatch avec version

**Jobs Exécutés :**

#### ✅ Validation Release
```yaml
validate-release:
  - Validation du format de version
  - Mise à jour des fichiers de version
  - Génération du changelog
```

#### 🧪 Tests Pré-Release
```yaml
pre-release-tests:
  - Tests complets Backend
  - Tests complets Frontend
  - Build de production
```

#### 🐳 Build Images Release
```yaml
build-release-images:
  - Build images avec tags de version
  - Push vers le registry
  - Métadonnées de version
```

#### 🏷️ Création Release GitHub
```yaml
create-github-release:
  - Création de la release GitHub
  - Upload des artefacts
  - Documentation de la release
```

#### 🌟 Déploiement Production
```yaml
deploy-production:
  - Déploiement automatique
  - Validation post-déploiement
  - Rollback automatique si échec
```

## 🔧 Configuration et Déploiement

### Prérequis

#### 1. Repository GitHub
- Repository public ou privé avec GitHub Actions activées
- Permissions pour créer des releases et uploader des artefacts

#### 2. Services Externes
- **SonarCloud** : Analyse de qualité du code
- **Snyk** : Scan de vulnérabilités
- **Slack** : Notifications
- **Email** : Notifications par email

#### 3. Infrastructure
- **Serveurs de déploiement** : Staging et Production
- **Base de données** : PostgreSQL
- **Monitoring** : Prometheus + Grafana
- **Load Balancer** : Nginx

### Configuration des Secrets

Voir le fichier [SECRETS_SETUP.md](.github/SECRETS_SETUP.md) pour la configuration complète des secrets.

#### Secrets Principaux
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

### Configuration des Environnements

#### 1. Environnement Staging
```yaml
# Configuration dans GitHub Settings > Environments
name: staging
protection_rules:
  - required_reviewers: 1
  - wait_timer: 0
secrets:
  - STAGING_HOST
  - STAGING_USER
  - STAGING_SSH_KEY
```

#### 2. Environnement Production
```yaml
# Configuration dans GitHub Settings > Environments
name: production
protection_rules:
  - required_reviewers: 2
  - wait_timer: 5
  - prevent_self_review: true
secrets:
  - PRODUCTION_HOST
  - PRODUCTION_USER
  - PRODUCTION_SSH_KEY
```

## 🐳 Configuration Docker

### Images Docker

#### Backend Image
```dockerfile
# Multi-stage build optimisé
FROM maven:3.8.4-openjdk-17 AS build
# ... étapes de build ...

FROM openjdk:17-jdk-slim AS runtime
# ... configuration runtime ...
```

#### Frontend Image
```dockerfile
# Multi-stage build avec Nginx
FROM node:18-alpine AS build
# ... étapes de build Angular ...

FROM nginx:alpine AS runtime
# ... configuration Nginx ...
```

### Docker Compose par Environnement

#### Staging (`docker-compose.staging.yml`)
```yaml
services:
  medhead-backend:
    image: ghcr.io/medhead/medhead-backend:staging
    environment:
      SPRING_PROFILES_ACTIVE: staging
  medhead-frontend:
    image: ghcr.io/medhead/medhead-frontend:staging
```

#### Production (`docker-compose.production.yml`)
```yaml
services:
  medhead-backend:
    image: ghcr.io/medhead/medhead-backend:latest
    environment:
      SPRING_PROFILES_ACTIVE: production
    deploy:
      replicas: 3
      resources:
        limits:
          memory: 2G
          cpus: '1'
```

## 📊 Monitoring et Alertes

### Métriques Collectées

#### 1. Métriques d'Application
- **Performance API** : Temps de réponse, débit
- **Utilisation ressources** : CPU, mémoire, disque
- **Erreurs** : Taux d'erreur, exceptions
- **Base de données** : Requêtes, connexions

#### 2. Métriques d'Infrastructure
- **Serveurs** : CPU, mémoire, réseau
- **Docker** : Ressources des conteneurs
- **Load Balancer** : Requêtes, erreurs

#### 3. Métriques de Déploiement
- **Durée des déploiements** : Temps de build et deploy
- **Taux de succès** : Pourcentage de déploiements réussis
- **Rollbacks** : Nombre et causes des rollbacks

### Dashboards Grafana

#### Dashboard Application
- Temps de réponse API par endpoint
- Taux d'erreur et exceptions
- Utilisation des ressources JVM
- Métriques de base de données

#### Dashboard Infrastructure
- Utilisation CPU/Mémoire des serveurs
- Métriques Docker et conteneurs
- Performance du load balancer
- Santé des services

#### Dashboard CI/CD
- Durée des pipelines
- Taux de succès des déploiements
- Métriques de qualité du code
- Alertes de sécurité

### Alertes Configurées

#### Alertes Critiques
```yaml
# Disponibilité
- Service down: > 1 minute
- Erreur 5xx: > 5% pendant 5 minutes
- Temps de réponse: > 2s pendant 10 minutes

# Performance
- CPU usage: > 80% pendant 10 minutes
- Memory usage: > 90% pendant 5 minutes
- Disk usage: > 85%

# Sécurité
- Nouvelle vulnérabilité critique
- Échec de scan de sécurité
- Tentative d'intrusion
```

#### Canaux de Notification
- **Slack** : #medhead-alerts (critiques)
- **Email** : admin@medhead.com (toutes alertes)
- **SMS** : Administrateurs (alertes critiques uniquement)

## 🎯 Bonnes Pratiques

### 1. Gestion des Branches

#### Stratégie GitFlow
```
main (production)
├── develop (staging)
├── feature/* (nouvelles fonctionnalités)
├── hotfix/* (corrections urgentes)
└── release/* (préparation des releases)
```

#### Workflow de Développement
1. **Feature** : Créer une branche depuis `develop`
2. **Pull Request** : Vers `develop` avec review
3. **Merge** : Après validation des tests CI
4. **Deploy** : Automatique vers staging
5. **Release** : Merge `develop` vers `main`
6. **Production** : Déploiement automatique

### 2. Gestion des Secrets

#### Rotation des Secrets
```bash
# Script de rotation automatique
#!/bin/bash
# Rotation tous les 90 jours
# Notification 7 jours avant expiration
# Mise à jour automatique des workflows
```

#### Audit des Secrets
```bash
# Vérification mensuelle
# Audit des accès
# Suppression des secrets inutilisés
```

### 3. Tests et Qualité

#### Couverture de Tests
- **Backend** : > 90% (unitaires + intégration)
- **Frontend** : > 85% (unitaires)
- **E2E** : 100% des parcours critiques

#### Standards de Qualité
- **SonarQube** : Quality Gate passée
- **Security** : Aucune vulnérabilité critique
- **Performance** : Temps de réponse < 200ms

### 4. Déploiement

#### Stratégie Blue-Green
```yaml
# Déploiement sans interruption
1. Déploiement sur environnement Green
2. Tests de validation
3. Basculement du trafic
4. Nettoyage de l'environnement Blue
```

#### Rollback Automatique
```yaml
# Critères de rollback
- Échec des tests de santé
- Taux d'erreur > 5%
- Temps de réponse > 2s
- Erreurs critiques détectées
```

## 🔧 Dépannage

### Problèmes Courants

#### 1. Échec des Tests CI
```bash
# Diagnostic
- Vérifier les logs détaillés
- Reproduire localement
- Vérifier les dépendances

# Solutions
- Corriger les tests défaillants
- Mettre à jour les dépendances
- Ajuster la configuration
```

#### 2. Échec de Déploiement
```bash
# Diagnostic
- Vérifier la connectivité SSH
- Contrôler les ressources serveur
- Analyser les logs Docker

# Solutions
- Redémarrer les services
- Libérer de l'espace disque
- Vérifier les configurations
```

#### 3. Problèmes de Sécurité
```bash
# Diagnostic
- Analyser les rapports de sécurité
- Vérifier les dépendances
- Contrôler les secrets

# Solutions
- Mettre à jour les dépendances
- Corriger les vulnérabilités
- Rotation des secrets
```

### Commandes de Diagnostic

#### 1. Vérification des Services
```bash
# Santé des services
curl -f https://medhead.com/api/health
curl -f https://medhead.com/health

# Statut Docker
docker-compose ps
docker stats

# Logs des services
docker-compose logs -f medhead-backend
```

#### 2. Monitoring des Métriques
```bash
# Prometheus
curl http://localhost:9090/api/v1/query?query=up

# Grafana
curl -u admin:admin http://localhost:3000/api/health
```

#### 3. Tests de Performance
```bash
# Load testing
ab -n 1000 -c 10 https://medhead.com/api/health

# Monitoring temps réel
htop
docker stats
```

## 📚 Ressources Supplémentaires

### Documentation GitHub Actions
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Workflow Syntax](https://docs.github.com/en/actions/using-workflows/workflow-syntax-for-github-actions)
- [Environments](https://docs.github.com/en/actions/deployment/targeting-different-environments/using-environments-for-deployment)

### Outils et Services
- **SonarCloud** : [sonarcloud.io](https://sonarcloud.io)
- **Snyk** : [snyk.io](https://snyk.io)
- **Prometheus** : [prometheus.io](https://prometheus.io)
- **Grafana** : [grafana.com](https://grafana.com)

### Formation et Bonnes Pratiques
- [DevOps Best Practices](https://docs.microsoft.com/en-us/azure/devops/learn/)
- [CI/CD Best Practices](https://docs.gitlab.com/ee/ci/pipelines/pipeline_efficiency.html)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)

---

## 🎉 Conclusion

Cette implémentation CI/CD complète offre :

- ✅ **Automatisation totale** du processus de développement
- ✅ **Qualité garantie** avec des tests exhaustifs
- ✅ **Sécurité renforcée** avec des analyses continues
- ✅ **Déploiement fiable** avec rollback automatique
- ✅ **Monitoring intégré** pour la surveillance continue

Les pipelines sont conçus pour être robustes, sécurisés et maintenables, garantissant une livraison continue de qualité pour le projet MedHead.

**Happy Deploying! 🚀✨**
