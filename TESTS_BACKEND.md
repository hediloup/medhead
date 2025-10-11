# Guide de test du backend MedHead

## 🚀 Comment démarrer le backend

### Prérequis
- Java 17 ou supérieur
- Maven 3.6+
- Base de données H2 (incluse) ou PostgreSQL

### 1. Démarrer la base de données PostgreSQL

```bash
# Se placer dans le dossier docker
cd docker

# Démarrer PostgreSQL (port 5433)
docker-compose up -d postgres

# Vérifier que la base fonctionne
docker-compose ps
```

### 2. Démarrer l'application Spring Boot

```bash
# Se placer dans le dossier backend
cd backend

# Option 1: Démarrer avec H2 (développement)
mvn spring-boot:run

# Option 2: Démarrer avec PostgreSQL (production)
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

L'application sera accessible sur : `http://localhost:8080`

### 2. Vérifier que l'application démarre correctement

```bash
# Test rapide de santé
curl http://localhost:8080/api/health
# Réponse attendue : "API d'allocation opérationnelle"
```

## 🧪 Méthodes de test

### 1. Tests unitaires avec Maven

```bash
# Exécuter tous les tests
mvn test

# Exécuter uniquement les tests Cucumber (BDD)
mvn test -Dtest=*CucumberTest

# Exécuter avec rapport de couverture
mvn clean test jacoco:report
```

### 2. Tests d'intégration avec Cucumber

Le projet utilise Cucumber pour les tests BDD. Les scénarios se trouvent dans :
- `src/test/resources/features/`

```bash
# Exécuter les tests Cucumber spécifiques
mvn test -Dtest=CucumberTest
```

### 3. Tests manuels avec Postman

J'ai créé une collection Postman complète : `MedHead_API_Collection.postman_collection.json`

#### Installation de la collection :
1. Ouvrir Postman
2. Cliquer sur "Import"
3. Sélectionner le fichier `MedHead_API_Collection.postman_collection.json`
4. La collection "MedHead API - Collection de test" apparaîtra

#### Variables d'environnement à configurer :
- `base_url` : `http://localhost:8080`
- `admin_token` : Token d'authentification admin (à récupérer via login)
- `medic_token` : Token d'authentification personnel médical (à récupérer via login)

### 4. Tests avec cURL

#### Test de santé
```bash
curl -X GET http://localhost:8080/api/health
```

#### Test d'allocation POST
```bash
curl -X POST http://localhost:8080/api/allocate \
  -H "Content-Type: application/json" \
  -d '{
    "specialty": "Cardiologie",
    "latitude": 48.8566,
    "longitude": 2.3522
  }'
```

#### Test d'allocation GET
```bash
curl -X GET "http://localhost:8080/api/allocate?specialty=Cardiologie&latitude=48.8566&longitude=2.3522"
```

## 📋 Endpoints disponibles

### API d'allocation (non sécurisée)
- `GET /api/health` - Vérification de santé
- `POST /api/allocate` - Allocation de lit (JSON)
- `GET /api/allocate` - Allocation de lit (query params)

### API patients (sécurisée)
- `GET /api/patients/health` - Vérification de santé
- `POST /api/auth/login` - Authentification
- `GET /api/patients/statistics` - Statistiques (ADMIN)
- `POST /api/patients/anonymize-all` - Anonymisation (ADMIN)
- `DELETE /api/patients/cleanup-expired` - Nettoyage (ADMIN)
- `GET /api/patients/{uuid}` - Informations patient (MEDICAL_STAFF)
- `GET /api/patients/exists/{uuid}` - Vérification existence (MEDICAL_STAFF)

## 🔐 Authentification

### Utilisateurs par défaut (à configurer dans votre SecurityConfig)
- **Admin** : username=`admin`, password=`admin123`, rôle=`ADMIN`
- **Personnel médical** : username=`medic`, password=`medic123`, rôle=`MEDICAL_STAFF`

### Processus d'authentification
1. POST `/api/auth/login` avec username/password
2. Récupérer le token JWT dans la réponse
3. Inclure le token dans l'header : `Authorization: Bearer <token>`

## 🎯 Scénarios de test

### Tests fonctionnels
1. **Allocation réussie** : Spécialité existante + coordonnées valides
2. **Allocation échouée** : Spécialité inexistante
3. **Validation** : Données manquantes ou invalides
4. **Performance** : Temps de réponse < 5 secondes

### Tests de sécurité
1. **Accès non autorisé** : Endpoints protégés sans token
2. **Token invalide** : Token expiré ou malformé
3. **Mauvais rôle** : Accès avec un rôle insuffisant
4. **CORS** : Requêtes cross-origin depuis le frontend

### Tests de données
1. **RGPD** : Anonymisation des données patients
2. **Rétention** : Suppression des données expirées
3. **Statistiques** : Données anonymisées uniquement

## 📊 Monitoring et logs

### Actuator endpoints (si configuré)
- `GET /actuator/health` - Santé de l'application
- `GET /actuator/metrics` - Métriques de performance
- `GET /actuator/info` - Informations sur l'application

### Logs
Les logs sont disponibles dans la console et peuvent être configurés dans `application.properties` :

```properties
# Logging level
logging.level.com.medhead.poc=DEBUG
logging.level.org.springframework.security=DEBUG

# Log file
logging.file.name=logs/medhead.log
```

## 🐛 Dépannage

### Problèmes courants

1. **Port 8080 occupé**
   ```bash
   # Changer le port dans application.properties
   server.port=8081
   ```

2. **Base de données non accessible**
   ```bash
   # Vérifier que PostgreSQL fonctionne
   docker-compose ps
   
   # Tester la connexion
   docker-compose exec -T postgres psql -U medhead_user -d medhead_db -c "SELECT COUNT(*) FROM hospitals;"
   
   # Si problème, vérifier les logs
   docker-compose logs postgres
   ```

3. **Tests Cucumber qui échouent**
   ```bash
   # Nettoyer et recompiler
   mvn clean compile test-compile
   ```

4. **Erreurs de sécurité**
   - Vérifier que les utilisateurs sont créés dans SecurityConfig
   - S'assurer que les rôles sont correctement mappés

### Commandes de diagnostic

```bash
# Vérifier les dépendances
mvn dependency:tree

# Nettoyer le cache Maven
mvn clean

# Vérifier la configuration
mvn help:effective-pom
```

## 📈 Métriques de qualité

### Couverture de code
```bash
# Générer le rapport de couverture
mvn clean test jacoco:report
# Ouvrir target/site/jacoco/index.html
```

### Performance
- Temps de réponse moyen < 2 secondes
- 95% des requêtes < 5 secondes
- Support de 100 requêtes simultanées

## 🔄 CI/CD

### Tests automatisés
```bash
# Script de test complet
#!/bin/bash
mvn clean compile
mvn test
mvn jacoco:report
mvn spring-boot:run &
sleep 30
curl -f http://localhost:8080/api/health || exit 1
```

### Intégration continue
Le projet est configuré pour fonctionner avec GitHub Actions ou Jenkins pour :
- Tests automatiques à chaque commit
- Génération de rapports de couverture
- Déploiement automatique si tous les tests passent
