# Docker Configuration - MedHead

Ce dossier contient la configuration Docker pour lancer une base de données PostgreSQL avec des données réelles d'hôpitaux du Royaume-Uni pour l'API REST MedHead.

## 🏥 Données incluses

La base de données PostgreSQL contient des données réelles d'hôpitaux du Royaume-Uni avec :
- **34 hôpitaux** répartis dans tout le Royaume-Uni (Angleterre, Écosse, Pays de Galles, Irlande du Nord)
- **33 spécialités médicales** basées sur les standards NHS
- **Coordonnées GPS** précises pour chaque hôpital
- **Adresses complètes** et informations détaillées
- **Nombre de lits disponibles** par hôpital
- **Villes principales** : Londres, Manchester, Birmingham, Leeds, Liverpool, Newcastle, Bristol, Sheffield, Nottingham, Leicester, Cardiff, Edinburgh, Glasgow, Belfast

## 🚀 Démarrage rapide

### Prérequis
- Docker et Docker Compose installés
- Ports 5432 et 8081 disponibles

### Lancement de la base de données

```bash
# Depuis le dossier docker
cd docker

# Démarrer PostgreSQL et pgAdmin
docker-compose up -d

# Vérifier que les services sont démarrés
docker-compose ps
```

### Arrêt des services

```bash
# Arrêter les services
docker-compose down

# Arrêter et supprimer les volumes (ATTENTION: supprime les données)
docker-compose down -v
```

## 📊 Accès aux services

### PostgreSQL
- **Host** : localhost
- **Port** : 5432
- **Database** : medhead_db
- **Username** : medhead_user
- **Password** : medhead_password

### pgAdmin (interface web d'administration)
- **URL** : http://localhost:8081
- **Email** : admin@medhead.com
- **Password** : admin123

Pour connecter pgAdmin à PostgreSQL :
1. Ouvrir pgAdmin
2. Clic droit sur "Servers" → "Create" → "Server"
3. Onglet "General" : nom = "MedHead PostgreSQL"
4. Onglet "Connection" :
   - Host : `postgres` (nom du service Docker)
   - Port : 5432
   - Database : medhead_db
   - Username : medhead_user
   - Password : medhead_password

## 🔧 Configuration de l'application

### Pour utiliser PostgreSQL avec l'API

1. **Démarrer PostgreSQL** :
```bash
cd docker
docker-compose up -d postgres
```

2. **Lancer l'application avec le profil de production** :
```bash
cd ../backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

L'API sera disponible sur : http://localhost:8082

### Test de l'API

Une fois l'application démarrée, vous pouvez tester l'API :

**POST** `/api/allocate`
```bash
curl -X POST http://localhost:8082/api/allocate \
  -H "Content-Type: application/json" \
  -d '{
    "specialty": "Cardiology",
    "latitude": 51.5074,
    "longitude": -0.1278
  }'
```

**GET** `/api/allocate`
```bash
curl "http://localhost:8082/api/allocate?specialty=Cardiology&latitude=51.5074&longitude=-0.1278"
```

### Profils disponibles

- **dev** (par défaut) : H2 en mémoire avec données de test
- **prod** : PostgreSQL avec données réelles

## 📁 Structure des fichiers

```
docker/
├── docker-compose.yml          # Configuration Docker Compose
├── postgres.conf              # Configuration PostgreSQL optimisée
├── init-scripts/
│   └── 01-init-database.sql   # Script d'initialisation avec données réelles
└── README.md                  # Ce fichier
```

## 🗄️ Base de données

### Table specialities

| Colonne | Type | Description |
|---------|------|-------------|
| id | BIGSERIAL | Identifiant unique |
| name | VARCHAR(255) | Nom de la spécialité |
| description | TEXT | Description de la spécialité |
| created_at | TIMESTAMP | Date de création |

### Table hospitals

| Colonne | Type | Description |
|---------|------|-------------|
| id | BIGSERIAL | Identifiant unique |
| name | VARCHAR(255) | Nom de l'hôpital |
| latitude | DOUBLE PRECISION | Latitude GPS |
| longitude | DOUBLE PRECISION | Longitude GPS |
| city | VARCHAR(255) | Ville de l'hôpital |
| address | TEXT | Adresse complète |
| available_beds | INTEGER | Nombre de lits disponibles |
| created_at | TIMESTAMP | Date de création |
| updated_at | TIMESTAMP | Date de mise à jour |

### Table hospital_specialities (table de liaison)

| Colonne | Type | Description |
|---------|------|-------------|
| hospital_id | BIGINT | Référence vers hospitals.id |
| speciality_id | BIGINT | Référence vers specialities.id |

### Index créés

- `idx_hospitals_location` : Optimise les requêtes géospatiales
- `idx_hospitals_city` : Index sur la ville
- `idx_hospital_specialities_hospital` : Index sur hospital_id
- `idx_hospital_specialities_speciality` : Index sur speciality_id

## 🔍 Requêtes utiles

### Lister toutes les spécialités
```sql
SELECT id, name, description 
FROM specialities 
ORDER BY name;
```

### Lister tous les hôpitaux avec leurs spécialités
```sql
SELECT h.id, h.name, h.city, h.available_beds, 
       STRING_AGG(s.name, ', ') as specialities
FROM hospitals h
LEFT JOIN hospital_specialities hs ON h.id = hs.hospital_id
LEFT JOIN specialities s ON hs.speciality_id = s.id
GROUP BY h.id, h.name, h.city, h.available_beds
ORDER BY h.name;
```

### Trouver les hôpitaux par spécialité
```sql
SELECT h.name, h.city, h.available_beds, s.name as speciality
FROM hospitals h
JOIN hospital_specialities hs ON h.id = hs.hospital_id
JOIN specialities s ON hs.speciality_id = s.id
WHERE s.name = 'Cardiology' 
AND h.available_beds > 0;
```

### Hôpitaux près d'une position (exemple: Londres)
```sql
SELECT h.name, h.city,
       (6371 * acos(cos(radians(51.5074)) * cos(radians(h.latitude)) * 
        cos(radians(h.longitude) - radians(-0.1278)) + 
        sin(radians(51.5074)) * sin(radians(h.latitude)))) AS distance_km
FROM hospitals h
JOIN hospital_specialities hs ON h.id = hs.hospital_id
JOIN specialities s ON hs.speciality_id = s.id
WHERE s.name = 'Cardiology' 
AND h.available_beds > 0
ORDER BY distance_km 
LIMIT 5;
```

### Spécialités disponibles dans une ville
```sql
SELECT DISTINCT s.name
FROM specialities s
JOIN hospital_specialities hs ON s.id = hs.speciality_id
JOIN hospitals h ON hs.hospital_id = h.id
WHERE h.city = 'London'
ORDER BY s.name;
```

## 🛠️ Maintenance

### Sauvegarder la base de données
```bash
docker-compose exec postgres pg_dump -U medhead_user medhead_db > backup.sql
```

### Restaurer la base de données
```bash
docker-compose exec -T postgres psql -U medhead_user medhead_db < backup.sql
```

### Consulter les logs
```bash
# Logs PostgreSQL
docker-compose logs postgres

# Logs pgAdmin
docker-compose logs pgadmin

# Logs en temps réel
docker-compose logs -f postgres
```

## 🐛 Dépannage

### Le port 5432 est déjà utilisé
```bash
# Trouver le processus qui utilise le port
sudo netstat -tulpn | grep :5432

# Ou modifier le port dans docker-compose.yml
ports:
  - "5433:5432"  # Utiliser le port 5433 au lieu de 5432
```

### La base de données ne démarre pas
```bash
# Vérifier les logs
docker-compose logs postgres

# Supprimer les volumes et redémarrer
docker-compose down -v
docker-compose up -d
```

### Réinitialiser complètement
```bash
# Arrêter et supprimer tout
docker-compose down -v
docker system prune -f

# Redémarrer
docker-compose up -d
```

## 📝 Notes importantes

- Les données sont persistantes grâce aux volumes Docker
- Le script d'initialisation ne s'exécute qu'au premier démarrage
- La configuration PostgreSQL est optimisée pour le développement
- H2 reste disponible pour les tests (profil `dev`)

## 🔒 Sécurité

⚠️ **Attention** : Cette configuration est destinée au développement uniquement. Pour la production, modifiez :
- Les mots de passe par défaut
- La configuration de sécurité PostgreSQL
- Les paramètres de connexion
- Activez SSL/TLS
