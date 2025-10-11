# 🎯 Démonstration - Interface MedHead

Cette démonstration montre comment utiliser l'interface graphique Angular pour l'allocation d'Lits d'Hôpital.

## 📋 Fonctionnalités implémentées

### ✅ Services créés

1. **GeocodingService** (`src/app/services/geocoding.service.ts`)
   - Utilise l'API Nominatim (OpenStreetMap) pour convertir les adresses en coordonnées
   - Méthode `geocodeAddressAsync()` pour géocoder une adresse
   - Gestion d'erreurs intégrée

2. **AllocationService** (`src/app/services/allocation.service.ts`)
   - Consomme l'API backend `/api/allocate`
   - Méthode `allocateHospital()` pour demander une allocation
   - Méthode `checkHealth()` pour vérifier l'état de l'API
   - Gestion complète des erreurs HTTP

### ✅ Modèles de données

1. **AllocationRequest** (`src/app/models/allocation-request.ts`)
   ```typescript
   {
     specialty: string;
     latitude: number;
     longitude: number;
   }
   ```

2. **AllocationResponse** (`src/app/models/allocation-response.ts`)
   ```typescript
   {
     hospital_name: string;
     hospital_id: number;
     distance_km: number;
     specialty: string;
     available_beds: number;
     estimated_time_minutes: number;
   }
   ```

3. **GeocodingResponse** (`src/app/models/geocoding-response.ts`)
   ```typescript
   {
     lat: number;
     lon: number;
     display_name: string;
     address?: { city?, country?, postcode? };
   }
   ```

### ✅ Composant principal

**HospitalAllocationComponent** (`src/app/components/hospital-allocation.component.ts`)

#### Fonctionnalités :
- **Formulaire réactif** avec validation
- **16 spécialités médicales** disponibles
- **Géocodage automatique** des adresses
- **Indicateurs de chargement** visuels
- **Gestion d'erreurs** complète
- **Affichage des résultats** structuré

#### Interface utilisateur :
- Sélection de spécialité via dropdown
- Champ de saisie d'adresse avec validation
- Boutons d'action (Rechercher, Nouvelle recherche)
- Messages d'erreur/succès contextuels
- Résultats détaillés de l'hôpital recommandé

## 🔄 Flux de fonctionnement

### 1. Saisie des données
```
Utilisateur sélectionne : "Cardiology"
Utilisateur saisit : "123 Rue de la Paix, Paris, France"
```

### 2. Géocodage automatique
```
Adresse → API Nominatim → { lat: 48.8566, lon: 2.3522 }
```

### 3. Demande d'allocation
```
POST /api/allocate
{
  "specialty": "Cardiology",
  "latitude": 48.8566,
  "longitude": 2.3522
}
```

### 4. Résultat affiché
```
✅ Hôpital Recommandé
Nom: Hôpital de la Pitié-Salpêtrière
Spécialité: Cardiology
Distance: 2.5 km
Temps estimé: 8 minutes
Lits disponibles: 3
```

## 🎨 Interface utilisateur

### Design moderne et responsive
- **Couleurs** : Palette médicale (bleu, vert, blanc)
- **Typographie** : Police système moderne
- **Layout** : Cards avec ombres et bordures arrondies
- **Animations** : Transitions fluides et spinners de chargement
- **Responsive** : Adapté mobile et desktop

### États de l'interface
1. **État initial** : Formulaire vide avec boutons désactivés
2. **Validation** : Messages d'erreur en temps réel
3. **Géocodage** : Spinner "Recherche de l'adresse..."
4. **Allocation** : Spinner "Recherche d'un hôpital..."
5. **Résultat** : Affichage structuré des données
6. **Erreur** : Messages d'erreur contextuels

## 🔧 Configuration technique

### URLs configurées
```typescript
// Backend API
API_BASE_URL = 'http://localhost:8080/api'

// Service de géocodage
NOMINATIM_BASE_URL = 'https://nominatim.openstreetmap.org'
```

### Validation des formulaires
- **Spécialité** : Obligatoire, sélection dans la liste
- **Adresse** : Obligatoire, minimum 5 caractères

### Gestion d'erreurs
- **400** : Données invalides
- **404** : Aucun hôpital disponible
- **500** : Erreur serveur
- **0** : Problème de connexion

## 📱 Spécialités médicales disponibles

```typescript
medicalSpecialties = [
  'Cardiology', 'Neurology', 'Orthopedics', 'Emergency Medicine',
  'Pediatrics', 'Oncology', 'Radiology', 'Anesthesiology',
  'Dermatology', 'Psychiatry', 'Gynecology', 'Urology',
  'Ophthalmology', 'ENT', 'Internal Medicine', 'General Surgery'
];
```

## 🚀 Instructions de démarrage

### 1. Prérequis
- Backend MedHead démarré sur `http://localhost:8080`
- Node.js 18+ installé
- npm installé

### 2. Installation
```bash
cd frontend
npm install
```

### 3. Démarrage
```bash
npm start
```

### 4. Accès
Ouvrir `http://localhost:4200` dans le navigateur

## 🧪 Tests de fonctionnement

### Test 1 : Allocation réussie
1. Sélectionner "Cardiology"
2. Saisir "Place de la Bastille, Paris, France"
3. Cliquer "Rechercher un hôpital"
4. Vérifier l'affichage du résultat

### Test 2 : Gestion d'erreur
1. Sélectionner une spécialité
2. Saisir une adresse invalide
3. Vérifier le message d'erreur

### Test 3 : Validation
1. Laisser les champs vides
2. Cliquer sur le bouton
3. Vérifier les messages de validation

## 📊 Données d'exemple

### Requête typique
```json
{
  "specialty": "Cardiology",
  "latitude": 48.8566,
  "longitude": 2.3522
}
```

### Réponse typique
```json
{
  "hospital_name": "Hôpital de la Pitié-Salpêtrière",
  "hospital_id": 123,
  "distance_km": 2.5,
  "specialty": "Cardiology",
  "available_beds": 3,
  "estimated_time_minutes": 8
}
```

## 🔒 Sécurité et conformité

- **CORS** : Configuré pour permettre les appels frontend
- **Validation** : Côté client et serveur
- **Anonymisation** : Gérée par le backend
- **HTTPS** : Recommandé pour la production

## 📈 Performance

- **Géocodage** : Cache des résultats recommandé
- **API calls** : Optimisation des requêtes
- **UI** : Lazy loading et optimisations Angular
- **Bundle size** : Minimisation automatique

Cette interface fournit une expérience utilisateur complète et professionnelle pour l'allocation d'Lits d'Hôpital, avec toutes les fonctionnalités demandées implémentées et testées.
