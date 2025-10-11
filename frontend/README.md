# MedHead Frontend - Interface d'Allocation d'Lits d'Hôpital

Cette application Angular fournit une interface graphique pour consommer l'API `/api/allocate` du backend MedHead. Elle permet de sélectionner une spécialité médicale et de saisir une localisation pour obtenir une recommandation d'hôpital.

## 🚀 Fonctionnalités

- **Géocodage automatique** : Convertit les adresses en coordonnées latitude/longitude via l'API Nominatim (OpenStreetMap)
- **Sélection de spécialité** : Interface intuitive pour choisir parmi 16 spécialités médicales
- **Recherche d'hôpital** : Trouve l'hôpital le plus proche avec des lits disponibles
- **Affichage des résultats** : Présente toutes les informations importantes (nom, distance, temps estimé, lits disponibles)
- **Design responsive** : Interface adaptée aux appareils mobiles et desktop
- **Gestion d'erreurs** : Messages d'erreur clairs et informatifs

## 🛠️ Technologies utilisées

- **Angular 16** : Framework frontend
- **TypeScript** : Langage de développement
- **Reactive Forms** : Gestion des formulaires
- **HttpClient** : Communication avec l'API backend
- **CSS3** : Styles modernes avec animations
- **Nominatim API** : Service de géocodage OpenStreetMap

## 📋 Prérequis

- Node.js (version 18 ou supérieure)
- npm (version 9 ou supérieure)
- Backend MedHead en cours d'exécution sur `http://localhost:8080`

## 🔧 Installation et démarrage

1. **Installer les dépendances** :
   ```bash
   cd frontend
   npm install
   ```

2. **Démarrer l'application** :
   ```bash
   npm start
   ```

3. **Accéder à l'application** :
   Ouvrez votre navigateur à l'adresse : `http://localhost:4200`

## 🏗️ Structure du projet

```
frontend/
├── src/
│   ├── app/
│   │   ├── components/
│   │   │   ├── hospital-allocation.component.ts
│   │   │   ├── hospital-allocation.component.html
│   │   │   └── hospital-allocation.component.css
│   │   ├── services/
│   │   │   ├── allocation.service.ts
│   │   │   └── geocoding.service.ts
│   │   ├── models/
│   │   │   ├── allocation-request.ts
│   │   │   ├── allocation-response.ts
│   │   │   └── geocoding-response.ts
│   │   ├── app.component.ts
│   │   └── app.module.ts
│   ├── styles.css
│   └── index.html
├── package.json
├── angular.json
└── tsconfig.json
```

## 🔄 Flux de fonctionnement

1. **Saisie des données** : L'utilisateur sélectionne une spécialité et saisit son adresse
2. **Géocodage** : L'adresse est convertie en coordonnées via l'API Nominatim
3. **Demande d'allocation** : Les coordonnées et la spécialité sont envoyées à l'API backend
4. **Résultat** : L'hôpital recommandé est affiché avec toutes les informations pertinentes

## 🌐 API utilisées

### API Backend (MedHead)
- **POST /api/allocate** : Demande d'allocation d'hôpital
- **GET /api/health** : Vérification de l'état de l'API

### API Externe
- **Nominatim OpenStreetMap** : Géocodage d'adresses
  - URL : `https://nominatim.openstreetmap.org/search`
  - Usage : Conversion d'adresses en coordonnées latitude/longitude

## 📱 Spécialités médicales disponibles

- Cardiology
- Neurology
- Orthopedics
- Emergency Medicine
- Pediatrics
- Oncology
- Radiology
- Anesthesiology
- Dermatology
- Psychiatry
- Gynecology
- Urology
- Ophthalmology
- ENT
- Internal Medicine
- General Surgery

## 🎨 Interface utilisateur

L'interface propose :
- **Formulaire intuitif** avec validation en temps réel
- **Indicateurs de chargement** pendant les opérations
- **Messages d'erreur/succès** clairs et informatifs
- **Affichage des résultats** structuré et lisible
- **Design responsive** adapté à tous les écrans

## 🔧 Configuration

### URL de l'API Backend
L'URL de l'API backend est configurée dans `allocation.service.ts` :
```typescript
private readonly API_BASE_URL = 'http://localhost:8080/api';
```

Pour changer l'URL, modifiez cette constante.

### Service de géocodage
Le service utilise l'API Nominatim d'OpenStreetMap. Aucune clé API n'est requise, mais il est recommandé de respecter les conditions d'utilisation.

## 🚨 Gestion d'erreurs

L'application gère plusieurs types d'erreurs :
- **Erreurs de validation** : Champs requis manquants
- **Erreurs de géocodage** : Adresse introuvable
- **Erreurs réseau** : Problèmes de connexion
- **Erreurs API** : Problèmes côté serveur

## 📊 Données affichées

Pour chaque hôpital recommandé :
- Nom de l'hôpital
- Spécialité demandée
- Distance en kilomètres
- Temps de trajet estimé
- Nombre de lits disponibles
- Identifiant de l'hôpital

## 🧪 Tests

Pour exécuter les tests Cypress existants :
```bash
npm run cy:open  # Interface graphique
npm run cy:run   # Tests en ligne de commande
```

## 📝 Scripts disponibles

- `npm start` : Démarre le serveur de développement
- `npm build` : Compile l'application pour la production
- `npm test` : Exécute les tests unitaires
- `npm run cy:open` : Ouvre l'interface Cypress
- `npm run cy:run` : Exécute les tests Cypress

## 🤝 Contribution

Pour contribuer au projet :
1. Suivez les conventions de code Angular
2. Ajoutez des tests pour les nouvelles fonctionnalités
3. Documentez les changements importants
4. Respectez les bonnes pratiques de sécurité

## 📄 Licence

Ce projet fait partie du système MedHead et suit les mêmes conditions de licence.
