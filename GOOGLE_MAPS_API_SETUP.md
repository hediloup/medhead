# Configuration Google Maps API - Résolution ApiTargetBlockedMapError

## Problème identifié
L'erreur `ApiTargetBlockedMapError` indique que la clé API Google Maps a des restrictions qui empêchent son utilisation.

## Solutions

### Solution 1 : Configurer la clé API dans Google Cloud Console (Recommandée)

#### Étape 1 : Accéder à Google Cloud Console
1. Aller sur [Google Cloud Console](https://console.cloud.google.com/)
2. Sélectionner votre projet
3. Aller dans "APIs & Services" > "Library"

#### Étape 2 : Activer les APIs nécessaires
Activer ces APIs :
- ✅ **Maps JavaScript API**
- ✅ **Directions API** 
- ✅ **Distance Matrix API**

#### Étape 3 : Configurer les restrictions de la clé API
1. Aller dans "APIs & Services" > "Credentials"
2. Cliquer sur votre clé API existante
3. Dans "Application restrictions" :
   - Choisir "HTTP referrers (web sites)"
   - Ajouter ces domaines :
     ```
     http://localhost:*
     http://127.0.0.1:*
     http://localhost:4200
     http://localhost:80
     https://yourdomain.com/*
     ```

#### Étape 4 : Vérifier les quotas
- Aller dans "APIs & Services" > "Quotas"
- Vérifier que les quotas ne sont pas dépassés

### Solution 2 : Créer une nouvelle clé API (Alternative)

#### Étape 1 : Créer une nouvelle clé API
1. Dans Google Cloud Console > "APIs & Services" > "Credentials"
2. Cliquer "Create Credentials" > "API Key"
3. Copier la nouvelle clé

#### Étape 2 : Configurer la nouvelle clé
1. Cliquer sur la nouvelle clé
2. Activer les APIs nécessaires
3. Configurer les restrictions HTTP referrers
4. Remplacer la clé dans `environment.ts`

### Solution 3 : Utiliser une clé API de test (Développement uniquement)

Pour le développement local uniquement, vous pouvez :
1. Créer une clé API sans restrictions
2. L'utiliser uniquement pour le développement
3. **IMPORTANT** : Ne jamais utiliser cette clé en production

## Test de la configuration

### Vérifier que l'API fonctionne
```javascript
// Dans la console du navigateur
console.log('Testing Google Maps API...');
if (window.google && window.google.maps) {
  console.log('✅ Google Maps API loaded successfully');
} else {
  console.log('❌ Google Maps API not available');
}
```

### Tester l'API Directions
```javascript
// Test simple de l'API Directions
const service = new google.maps.DirectionsService();
const request = {
  origin: 'Manchester, UK',
  destination: 'London, UK',
  travelMode: google.maps.TravelMode.DRIVING
};

service.route(request, (result, status) => {
  if (status === 'OK') {
    console.log('✅ Directions API working:', result);
  } else {
    console.log('❌ Directions API error:', status);
  }
});
```

## Messages d'erreur courants

| Erreur | Cause | Solution |
|--------|-------|----------|
| `ApiTargetBlockedMapError` | Restrictions de domaine | Configurer HTTP referrers |
| `REQUEST_DENIED` | API non activée | Activer Maps JavaScript API |
| `OVER_QUERY_LIMIT` | Quota dépassé | Vérifier les quotas |
| `INVALID_REQUEST` | Requête invalide | Vérifier les paramètres |

## Configuration pour la production

### Variables d'environnement
```bash
# Pour Docker
GOOGLE_MAPS_API_KEY=your_production_key_here

# Pour Angular
export GOOGLE_MAPS_API_KEY=your_production_key_here
```

### Sécurité
- ✅ Toujours restreindre les clés API par domaine
- ✅ Utiliser des clés différentes pour dev/prod
- ✅ Surveiller l'utilisation dans Google Cloud Console
- ✅ Configurer des alertes de quota

## Vérification finale

Après configuration, l'application devrait :
1. ✅ Charger l'API Google Maps sans erreur
2. ✅ Afficher "Données temps réel Google Maps" 
3. ✅ Montrer les vraies distances et temps de trajet
4. ✅ Fonctionner en fallback si l'API échoue

## Support

Si le problème persiste :
1. Vérifier les logs dans Google Cloud Console
2. Tester avec une clé API sans restrictions
3. Contacter le support Google Cloud si nécessaire
