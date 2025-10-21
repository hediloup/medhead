# 🐚 Exemples cURL - API MedHead

Ce fichier contient des exemples d'utilisation de l'API MedHead avec cURL.

## 🚀 Démarrage Rapide

### Vérifier que l'API est opérationnelle
```bash
curl http://localhost:8080/api/health
```
**Réponse attendue** : `Allocation API operational`

## 🏥 Allocation de Lits d'Hôpitaux

### 1. Allocation POST (Recommandé)
```bash
curl -X POST http://localhost:8080/api/allocate \
  -H "Content-Type: application/json" \
  -d '{
    "specialty": "Cardiology",
    "latitude": 53.3976314,
    "longitude": -2.1829641
  }'
```

**Réponse attendue** :
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

### 2. Allocation GET (Pour tests)
```bash
curl "http://localhost:8080/api/allocate?specialty=Cardiology&latitude=53.3976314&longitude=-2.1829641"
```

### 3. Test avec différentes spécialités
```bash
# Dermatologie
curl -X POST http://localhost:8080/api/allocate \
  -H "Content-Type: application/json" \
  -d '{
    "specialty": "Dermatology",
    "latitude": 51.5074,
    "longitude": -0.1278
  }'

# Neurologie
curl -X POST http://localhost:8080/api/allocate \
  -H "Content-Type: application/json" \
  -d '{
    "specialty": "Neurology",
    "latitude": 52.4862,
    "longitude": -1.8904
  }'

# Chirurgie
curl -X POST http://localhost:8080/api/allocate \
  -H "Content-Type: application/json" \
  -d '{
    "specialty": "Surgery",
    "latitude": 53.8008,
    "longitude": -1.5491
  }'
```

## 🔍 Tests de Diagnostic

### Test automatique
```bash
curl http://localhost:8080/api/test
```

### Debug des hôpitaux
```bash
curl http://localhost:8080/api/debug/hospitals
```

## 👥 Gestion des Patients (Authentification requise)

### 1. Obtenir les statistiques patients
```bash
# Remplacez YOUR_JWT_TOKEN par votre token JWT
curl -X GET http://localhost:8080/api/patients/statistics \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 2. Anonymiser tous les patients
```bash
curl -X POST http://localhost:8080/api/patients/anonymize-all \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 3. Nettoyer les données expirées
```bash
curl -X DELETE http://localhost:8080/api/patients/cleanup-expired \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 4. Vérifier l'existence d'un patient
```bash
curl -X GET http://localhost:8080/api/patients/exists/123e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 5. Obtenir un patient par UUID
```bash
curl -X GET http://localhost:8080/api/patients/123e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 🧪 Tests de Charge

### Test simple avec K6
```bash
# Créer un fichier test.k6.js
cat > test.k6.js << 'EOF'
import http from 'k6/http';
import { check } from 'k6';

export default function () {
  const payload = JSON.stringify({
    specialty: 'Cardiology',
    latitude: 53.3976314,
    longitude: -2.1829641
  });
  
  const response = http.post('http://localhost:8080/api/allocate', payload, {
    headers: { 'Content-Type': 'application/json' }
  });
  
  check(response, {
    'Status is 200': (r) => r.status === 200,
    'Response time < 200ms': (r) => r.timings.duration < 200
  });
}
EOF

# Exécuter le test
k6 run test.k6.js
```

## 📊 Monitoring et Logs

### Voir les logs en temps réel
```bash
# Logs Docker
docker logs -f medhead-backend

# Filtrer les événements BedReserved
docker logs medhead-backend | grep "BED_RESERVED"
```

### Vérifier les métriques
```bash
# Santé de l'application
curl http://localhost:8080/actuator/health

# Informations de l'application
curl http://localhost:8080/actuator/info

# Métriques (nécessite authentification ADMIN)
curl http://localhost:8080/actuator/metrics \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN"
```

## 🚨 Gestion des Erreurs

### Test des codes d'erreur
```bash
# 400 - Paramètres invalides
curl -X POST http://localhost:8080/api/allocate \
  -H "Content-Type: application/json" \
  -d '{
    "specialty": "",
    "latitude": "invalid",
    "longitude": "invalid"
  }'

# 404 - Spécialité non trouvée
curl -X POST http://localhost:8080/api/allocate \
  -H "Content-Type: application/json" \
  -d '{
    "specialty": "NonExistentSpecialty",
    "latitude": 53.3976314,
    "longitude": -2.1829641
  }'
```

## 🔧 Configuration Avancée

### Headers personnalisés
```bash
# Avec User-Agent personnalisé
curl -X POST http://localhost:8080/api/allocate \
  -H "Content-Type: application/json" \
  -H "User-Agent: MedHead-Client/1.0" \
  -d '{
    "specialty": "Cardiology",
    "latitude": 53.3976314,
    "longitude": -2.1829641
  }'

# Avec timeout
curl -X POST http://localhost:8080/api/allocate \
  -H "Content-Type: application/json" \
  --max-time 30 \
  -d '{
    "specialty": "Cardiology",
    "latitude": 53.3976314,
    "longitude": -2.1829641
  }'
```

### Sauvegarder les réponses
```bash
# Sauvegarder dans un fichier
curl -X POST http://localhost:8080/api/allocate \
  -H "Content-Type: application/json" \
  -d '{
    "specialty": "Cardiology",
    "latitude": 53.3976314,
    "longitude": -2.1829641
  }' \
  -o allocation_response.json

# Afficher les headers de réponse
curl -X POST http://localhost:8080/api/allocate \
  -H "Content-Type: application/json" \
  -d '{
    "specialty": "Cardiology",
    "latitude": 53.3976314,
    "longitude": -2.1829641
  }' \
  -i
```

## 📈 Tests de Performance

### Test de charge simple
```bash
# Test avec 10 requêtes simultanées
for i in {1..10}; do
  curl -X POST http://localhost:8080/api/allocate \
    -H "Content-Type: application/json" \
    -d '{
      "specialty": "Cardiology",
      "latitude": 53.3976314,
      "longitude": -2.1829641
    }' &
done
wait
```

### Mesure du temps de réponse
```bash
# Mesurer le temps de réponse
time curl -X POST http://localhost:8080/api/allocate \
  -H "Content-Type: application/json" \
  -d '{
    "specialty": "Cardiology",
    "latitude": 53.3976314,
    "longitude": -2.1829641
  }'
```

## 🔐 Authentification

### Obtenir un token JWT (exemple)
```bash
# Note: Ceci dépend de votre implémentation d'authentification
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

### Utiliser le token
```bash
# Remplacer YOUR_JWT_TOKEN par le token obtenu
export JWT_TOKEN="YOUR_JWT_TOKEN"

curl -X GET http://localhost:8080/api/patients/statistics \
  -H "Authorization: Bearer $JWT_TOKEN"
```

## 📝 Scripts Utiles

### Script de test complet
```bash
#!/bin/bash
# test-api.sh

echo "🏥 Test de l'API MedHead"
echo "========================"

# Test de santé
echo "1. Test de santé..."
curl -s http://localhost:8080/api/health
echo -e "\n"

# Test d'allocation
echo "2. Test d'allocation..."
curl -s -X POST http://localhost:8080/api/allocate \
  -H "Content-Type: application/json" \
  -d '{
    "specialty": "Cardiology",
    "latitude": 53.3976314,
    "longitude": -2.1829641
  }' | jq .
echo -e "\n"

# Test de diagnostic
echo "3. Test de diagnostic..."
curl -s http://localhost:8080/api/test
echo -e "\n"

echo "✅ Tests terminés"
```

### Script de monitoring
```bash
#!/bin/bash
# monitor-api.sh

echo "📊 Monitoring de l'API MedHead"
echo "============================="

while true; do
  echo "$(date): Test de santé..."
  if curl -s http://localhost:8080/api/health > /dev/null; then
    echo "✅ API opérationnelle"
  else
    echo "❌ API non disponible"
  fi
  sleep 30
done
```

---

**Note** : Assurez-vous que l'application MedHead est démarrée avant d'exécuter ces exemples :
```bash
cd /home/hedi/projects/medhead/docker
docker-compose up -d
```
