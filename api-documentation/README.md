# 📚 Documentation API MedHead

Ce dossier contient la documentation complète des APIs Backend de MedHead.

## 📁 Structure du Dossier

```
api-documentation/
├── README.md                    # Ce fichier
├── swagger.yaml                 # Documentation OpenAPI 3.0
├── postman-collection.json     # Collection Postman
├── examples/                   # Exemples d'utilisation
│   ├── curl-examples.md        # Exemples cURL
│   ├── javascript-examples.md # Exemples JavaScript
│   └── python-examples.md      # Exemples Python
└── schemas/                    # Schémas de données
    ├── allocation-request.json
    ├── allocation-response.json
    └── patient-schema.json
```

## 🚀 Accès Rapide

### Documentation Interactive
- **Swagger UI** : http://localhost:8080/swagger-ui.html
- **OpenAPI JSON** : http://localhost:8080/v3/api-docs

### Endpoints Principaux
- **Allocation** : `POST /api/allocate`
- **Santé** : `GET /api/health`
- **Patients** : `GET /api/patients/statistics`

## 📖 Documentation OpenAPI

Le fichier `swagger.yaml` contient la documentation complète au format OpenAPI 3.0 avec :

### 🏥 Endpoints d'Allocation
- **POST /api/allocate** - Allocation de lits d'hôpitaux
- **GET /api/allocate** - Version GET pour les tests
- **GET /api/health** - Vérification de santé
- **GET /api/test** - Test de diagnostic

### 👥 Endpoints Patients (Authentifiés)
- **GET /api/patients/statistics** - Statistiques anonymisées
- **POST /api/patients/anonymize-all** - Anonymisation GDPR
- **DELETE /api/patients/cleanup-expired** - Nettoyage des données
- **GET /api/patients/{uuid}** - Informations patient
- **GET /api/patients/exists/{uuid}** - Vérification d'existence

### 🔐 Sécurité
- **Endpoints publics** : `/api/allocate`, `/api/health`
- **Authentification JWT** : Endpoints patients
- **Rôles** : `ADMIN`, `MEDICAL_STAFF`

## 🛠️ Utilisation

### 1. Visualisation Swagger UI
```bash
# Démarrer l'application
cd /home/hedi/projects/medhead/docker
docker-compose up -d

# Accéder à Swagger UI
open http://localhost:8080/swagger-ui.html
```

### 2. Test avec cURL
```bash
# Test d'allocation
curl -X POST http://localhost:8080/api/allocate \
  -H "Content-Type: application/json" \
  -d '{
    "specialty": "Cardiology",
    "latitude": 53.3976314,
    "longitude": -2.1829641
  }'

# Test de santé
curl http://localhost:8080/api/health
```

### 3. Import dans Postman
1. Ouvrir Postman
2. Importer le fichier `postman-collection.json`
3. Configurer l'environnement avec l'URL de base

## 📊 Modèles de Données

### AllocationRequest
```json
{
  "specialty": "Cardiology",
  "latitude": 53.3976314,
  "longitude": -2.1829641
}
```

### AllocationResponse
```json
{
  "hospital_name": "Stepping Hill Hospital",
  "hospital_id": 10,
  "distance_km": 3.29,
  "specialty": "Cardiology",
  "available_beds": 31,
  "estimated_time_minutes": 4,
  "hospital_latitude": 53.3969,
  "hospital_longitude": -2.1333
}
```

## 🔄 Événements

Chaque allocation déclenche un **BedReservedEvent** avec :
- ID d'événement unique
- Données patient anonymisées
- Informations hôpital
- Distance et temps estimé
- Statut de confirmation

## 📈 Monitoring

### Logs Docker
```bash
# Voir les logs en temps réel
docker logs -f medhead-backend

# Filtrer les événements
docker logs medhead-backend | grep "BED_RESERVED"
```

### Métriques
- **Temps de réponse** : < 200ms
- **Taux d'erreur** : < 1%
- **Disponibilité** : 99.9%

## 🧪 Tests

### Tests de Charge (K6)
```bash
# Exécuter le test de stress
k6 run test-stress-k6.js
```

### Tests d'Intégration
```bash
# Tests backend
cd backend
mvn test

# Tests frontend
cd frontend
npm test
```

## 🔧 Configuration

### Variables d'Environnement
```bash
# Base de données
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/medhead_db
SPRING_DATASOURCE_USERNAME=medhead_user
SPRING_DATASOURCE_PASSWORD=medhead_password

# API Keys
GOOGLE_MAPS_API_KEY=your_api_key_here

# Sécurité
JWT_SECRET=your_jwt_secret
JWT_EXPIRATION=86400
```

### Ports
- **Backend API** : 8080
- **Frontend** : 4200
- **PostgreSQL** : 5433
- **pgAdmin** : 8082

## 📞 Support

- **Documentation** : Ce dossier
- **Issues** : GitHub Issues
- **Email** : dev@medhead.com
- **Slack** : #medhead-dev

## 🔄 Mise à Jour

Pour mettre à jour la documentation :

1. Modifier `swagger.yaml`
2. Régénérer les exemples
3. Tester avec Postman
4. Valider avec Swagger UI

```bash
# Validation du fichier Swagger
swagger-codegen validate -i swagger.yaml
```

---

**Version** : 1.0.0  
**Dernière mise à jour** : 2025-10-21  
**Maintenu par** : Équipe MedHead Development
