# Configuration Google Maps API

## Vue d'ensemble

Le service de calcul de distance a été optimisé pour utiliser l'API Google Maps Directions avec prise en compte du trafic en temps réel. Cette optimisation permet de :

- Calculer des distances routières précises (au lieu de distances à vol d'oiseau)
- Optimiser par temps de trajet plutôt que par distance
- Tenir compte du trafic en temps réel
- Considérer les limitations de vitesse, intersections, travaux, etc.

## Configuration

### 1. Obtenir une clé API Google Maps

1. Allez sur [Google Cloud Console](https://console.cloud.google.com/)
2. Créez un nouveau projet ou sélectionnez un projet existant
3. Activez l'API "Directions API" pour votre projet
4. Créez des identifiants (clé API)
5. Restreignez la clé API aux Directions API et à votre domaine/IP

### 2. Configuration de l'environnement

#### Variables d'environnement (recommandé pour la production)

```bash
export GOOGLE_MAPS_API_KEY="votre_cle_api_ici"
```

#### Fichier de propriétés (pour le développement)

Ajoutez dans `application-dev.properties` :

```properties
google.maps.api.key=votre_cle_api_ici
```

### 3. Configuration des profils

Le système utilise automatiquement le bon profil selon l'environnement :

- **Développement** : Utilise `application-dev.properties`
- **Production** : Utilise `application-prod.properties`

## Fonctionnement

### Mode avec API Google Maps (recommandé)

Quand la clé API est configurée :

1. **Appel API** : Le service appelle l'API Google Maps Directions avec :
   - `mode=driving` : Mode de transport automobile
   - `departure_time=now` : Départ maintenant
   - `traffic_model=best_guess` : Meilleure estimation du trafic

2. **Données retournées** :
   - Distance routière en kilomètres
   - Durée sans trafic en minutes
   - Durée avec trafic en minutes (si disponible)
   - Indicateur de disponibilité des données de trafic

3. **Optimisation** : L'hôpital avec le temps de trajet le plus court est sélectionné

### Mode de fallback (sans API)

Si la clé API n'est pas configurée ou en cas d'erreur :

1. **Calcul Haversine** : Utilise la formule Haversine pour la distance à vol d'oiseau
2. **Estimation de temps** : Calcule le temps basé sur une vitesse moyenne de 50 km/h
3. **Optimisation par distance** : Sélectionne l'hôpital le plus proche géographiquement

## Exemple d'utilisation

### Requête API Google Maps

```bash
curl -G "https://maps.googleapis.com/maps/api/directions/json" \
  --data-urlencode "origin=48.8566,2.3522" \
  --data-urlencode "destination=48.8416,2.2681" \
  --data-urlencode "mode=driving" \
  --data-urlencode "departure_time=now" \
  --data-urlencode "traffic_model=best_guess" \
  --data-urlencode "key=YOUR_API_KEY"
```

### Réponse typique

```json
{
  "routes": [{
    "legs": [{
      "distance": {
        "value": 8432,
        "text": "8.4 km"
      },
      "duration": {
        "value": 1200,
        "text": "20 mins"
      },
      "duration_in_traffic": {
        "value": 1800,
        "text": "30 mins"
      }
    }]
  }],
  "status": "OK"
}
```

## Monitoring et logs

Le système log automatiquement :

- Les appels à l'API Google Maps (niveau DEBUG)
- Les erreurs d'API avec codes de statut
- L'utilisation du mode fallback
- Les temps de réponse et distances calculées

## Coûts

L'API Google Maps Directions est facturée par requête. Consultez la [documentation officielle](https://developers.google.com/maps/documentation/directions/usage-and-billing) pour les tarifs actuels.

## Sécurité

- Ne commitez jamais votre clé API dans le code
- Utilisez les variables d'environnement en production
- Restreignez votre clé API aux APIs et domaines nécessaires
- Surveillez l'utilisation de votre quota API
