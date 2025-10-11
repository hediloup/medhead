# ✅ Solution : Docker PostgreSQL fonctionne maintenant !

## 🎯 Problème résolu

**Erreur initiale** : `address already in use` sur le port 5432
**Cause** : Un autre service PostgreSQL utilisait déjà le port 5432
**Solution** : Changement du port vers 5433

## 🔧 Modifications apportées

### 1. Configuration Docker mise à jour
- **Fichier** : `docker/docker-compose.yml`
- **Changement** : Port `5432:5432` → `5433:5432`
- **Résultat** : PostgreSQL accessible sur localhost:5433

### 2. Configuration Spring Boot créée
- **Fichier** : `backend/src/main/resources/application-prod.properties`
- **Configuration** : Connexion PostgreSQL sur port 5433
- **Profil** : `prod` pour utiliser PostgreSQL

### 3. Documentation mise à jour
- **Fichier** : `docker/README.md`
- **Mise à jour** : Port 5432 → 5433 dans toutes les références

## 🚀 Comment utiliser maintenant

### Démarrer PostgreSQL
```bash
cd docker
docker-compose up -d postgres
```

### Vérifier que ça fonctionne
```bash
# Vérifier le statut
docker-compose ps

# Tester la connexion
docker-compose exec -T postgres psql -U medhead_user -d medhead_db -c "SELECT COUNT(*) FROM hospitals;"
# Résultat attendu : 34 hôpitaux
```

### Démarrer l'application avec PostgreSQL
```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## 📊 Données disponibles

La base de données contient maintenant :
- ✅ **34 hôpitaux** du Royaume-Uni
- ✅ **33 spécialités médicales**
- ✅ **Coordonnées GPS** précises
- ✅ **Données réalistes** pour les tests

## 🔗 Connexions

### PostgreSQL
- **Host** : localhost
- **Port** : 5433
- **Database** : medhead_db
- **Username** : medhead_user
- **Password** : medhead_password

### Application Spring Boot
- **URL** : http://localhost:8080
- **Profil** : `prod` pour PostgreSQL
- **Profil** : `dev` (par défaut) pour H2

## 🧪 Tests disponibles

### Test rapide avec cURL
```bash
# Test de santé
curl http://localhost:8080/api/health

# Test d'allocation (Londres)
curl -X POST http://localhost:8080/api/allocate \
  -H "Content-Type: application/json" \
  -d '{
    "specialty": "Cardiology",
    "latitude": 51.5074,
    "longitude": -0.1278
  }'
```

### Collection Postman
- **Fichier** : `MedHead_API_Collection.postman_collection.json`
- **Variable** : `base_url` = `http://localhost:8080`
- **Prêt à utiliser** avec tous les endpoints

## 🛠️ Commandes utiles

### Gestion Docker
```bash
# Démarrer PostgreSQL
docker-compose up -d postgres

# Arrêter PostgreSQL
docker-compose down

# Voir les logs
docker-compose logs postgres

# Redémarrer complètement
docker-compose down -v && docker-compose up -d
```

### Base de données
```bash
# Connexion directe
docker-compose exec -T postgres psql -U medhead_user -d medhead_db

# Lister les hôpitaux
docker-compose exec -T postgres psql -U medhead_user -d medhead_db -c "SELECT name, city, available_beds FROM hospitals LIMIT 10;"

# Lister les spécialités
docker-compose exec -T postgres psql -U medhead_user -d medhead_db -c "SELECT name FROM specialities ORDER BY name;"
```

## 🎉 Résultat

✅ **PostgreSQL fonctionne** sur le port 5433  
✅ **Données chargées** (34 hôpitaux, 33 spécialités)  
✅ **Configuration Spring Boot** prête  
✅ **Collection Postman** complète  
✅ **Documentation** mise à jour  

Vous pouvez maintenant tester votre API avec des données réelles !
