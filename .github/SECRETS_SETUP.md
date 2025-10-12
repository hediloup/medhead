# 🔐 Configuration des Secrets GitHub

Ce document explique comment configurer tous les secrets nécessaires pour les workflows GitHub Actions de MedHead.

## 📋 Secrets Requis

### 🔧 Secrets d'Infrastructure

#### Serveurs de Déploiement
```
STAGING_HOST=staging.medhead.com
STAGING_USER=ubuntu
STAGING_SSH_KEY=<clé SSH privée pour staging>

PRODUCTION_HOST=prod.medhead.com
PRODUCTION_USER=ubuntu
PRODUCTION_SSH_KEY=<clé SSH privée pour production>
```

#### Base de Données
```
POSTGRES_PASSWORD=<mot de passe fort pour PostgreSQL>
GRAFANA_PASSWORD=<mot de passe fort pour Grafana>
GRAFANA_DB_PASSWORD=<mot de passe pour la base Grafana>
```

#### SSL/TLS
```
SSL_KEYSTORE_PASSWORD=<mot de passe pour le keystore Java>
```

### 🔒 Secrets de Sécurité

#### Authentification JWT
```
JWT_SECRET=<clé secrète forte pour JWT (256 bits)>
```

#### API Keys et Tokens
```
SONAR_HOST_URL=https://sonarcloud.io
SONAR_TOKEN=<token SonarCloud>
SNYK_TOKEN=<token Snyk>
GITLEAKS_LICENSE=<licence GitLeaks>
```

### 📊 Secrets de Monitoring

#### Webhooks
```
MONITORING_WEBHOOK=https://monitoring.medhead.com/webhook
SLACK_WEBHOOK=https://hooks.slack.com/services/...
```

### 📧 Secrets de Notification

#### Email
```
EMAIL_USERNAME=ci-cd@medhead.com
EMAIL_PASSWORD=<mot de passe d'application Gmail>
NOTIFICATION_EMAIL=admin@medhead.com
```

## 🛠️ Configuration des Secrets

### 1. Accès aux Secrets GitHub

1. Allez sur votre repository GitHub
2. Cliquez sur **Settings** (Paramètres)
3. Dans le menu de gauche, cliquez sur **Secrets and variables** > **Actions**
4. Cliquez sur **New repository secret**

### 2. Ajout des Secrets

Pour chaque secret, suivez ces étapes :

1. **Name** : Nom du secret (ex: `POSTGRES_PASSWORD`)
2. **Secret** : Valeur du secret
3. Cliquez sur **Add secret**

### 3. Secrets par Environnement

#### Staging Environment
```bash
# Ajouter dans Settings > Environments > staging
STAGING_HOST
STAGING_USER  
STAGING_SSH_KEY
```

#### Production Environment
```bash
# Ajouter dans Settings > Environments > production
PRODUCTION_HOST
PRODUCTION_USER
PRODUCTION_SSH_KEY
```

## 🔑 Génération des Clés et Mots de Passe

### 1. Mot de Passe PostgreSQL
```bash
# Générer un mot de passe fort
openssl rand -base64 32
```

### 2. Clé JWT Secret
```bash
# Générer une clé JWT de 256 bits
openssl rand -base64 32
```

### 3. Clés SSH
```bash
# Générer une paire de clés SSH
ssh-keygen -t rsa -b 4096 -C "medhead-ci@github.com" -f ~/.ssh/medhead_ci

# Copier la clé publique sur le serveur
ssh-copy-id -i ~/.ssh/medhead_ci.pub ubuntu@staging.medhead.com
```

### 4. Keystore Java
```bash
# Créer un keystore pour HTTPS
keytool -genkeypair -alias medhead -keyalg RSA -keysize 2048 \
  -storetype PKCS12 -keystore medhead.p12 -validity 365
```

## 📝 Template de Configuration

### Variables d'Environnement (.env)
```bash
# Infrastructure
STAGING_HOST=staging.medhead.com
STAGING_USER=ubuntu
PRODUCTION_HOST=prod.medhead.com
PRODUCTION_USER=ubuntu

# Base de données
POSTGRES_PASSWORD=your_strong_password_here
GRAFANA_PASSWORD=your_grafana_password_here
GRAFANA_DB_PASSWORD=your_grafana_db_password_here

# Sécurité
JWT_SECRET=your_jwt_secret_here
SSL_KEYSTORE_PASSWORD=your_keystore_password_here

# APIs
SONAR_HOST_URL=https://sonarcloud.io
SONAR_TOKEN=your_sonar_token_here
SNYK_TOKEN=your_snyk_token_here
GITLEAKS_LICENSE=your_gitleaks_license_here

# Monitoring
MONITORING_WEBHOOK=https://monitoring.medhead.com/webhook
SLACK_WEBHOOK=https://hooks.slack.com/services/YOUR/SLACK/WEBHOOK

# Notifications
EMAIL_USERNAME=ci-cd@medhead.com
EMAIL_PASSWORD=your_gmail_app_password_here
NOTIFICATION_EMAIL=admin@medhead.com
```

## 🔒 Bonnes Pratiques de Sécurité

### 1. Rotation des Secrets
- **Mots de passe** : Rotation tous les 90 jours
- **Clés SSH** : Rotation tous les 6 mois
- **Tokens API** : Rotation selon la politique du fournisseur

### 2. Accès aux Secrets
- Limiter l'accès aux membres de l'équipe nécessaires
- Utiliser les environnements GitHub pour les secrets sensibles
- Ne jamais commiter les secrets dans le code

### 3. Audit des Secrets
- Réviser régulièrement les secrets configurés
- Supprimer les secrets inutilisés
- Monitorer l'utilisation des secrets

### 4. Backup des Secrets
- Sauvegarder les secrets dans un gestionnaire de mots de passe
- Documenter la procédure de récupération
- Maintenir une liste de tous les secrets configurés

## 🚨 Dépannage

### Problèmes Courants

#### 1. Secret Non Trouvé
```
Error: Secret not found: POSTGRES_PASSWORD
```
**Solution** : Vérifier que le secret est correctement configuré dans GitHub

#### 2. Permission Refusée SSH
```
Permission denied (publickey)
```
**Solution** : Vérifier que la clé SSH publique est sur le serveur

#### 3. Échec de Connexion Base de Données
```
Connection refused
```
**Solution** : Vérifier le mot de passe et l'URL de connexion

### Commandes de Vérification

#### 1. Test de Connexion SSH
```bash
ssh -i ~/.ssh/medhead_ci ubuntu@staging.medhead.com
```

#### 2. Test de Connexion Base de Données
```bash
psql -h staging.medhead.com -U medhead_user -d medhead_db
```

#### 3. Test des Webhooks
```bash
curl -X POST $SLACK_WEBHOOK -d '{"text":"Test message"}'
```

## 📚 Ressources Supplémentaires

### Documentation GitHub
- [Secrets GitHub Actions](https://docs.github.com/en/actions/security-guides/encrypted-secrets)
- [Environments GitHub](https://docs.github.com/en/actions/deployment/targeting-different-environments/using-environments-for-deployment)

### Outils Recommandés
- **Gestionnaire de mots de passe** : 1Password, Bitwarden
- **Générateur de mots de passe** : pwgen, openssl
- **Gestionnaire de secrets** : HashiCorp Vault, AWS Secrets Manager

---

## ⚠️ Important

**Ne jamais partager ou commiter ces secrets !**

- Utilisez toujours les secrets GitHub pour les valeurs sensibles
- Configurez les environnements pour limiter l'accès
- Maintenez une documentation sécurisée des secrets
- Effectuez des audits réguliers de sécurité
