# language: fr
Fonctionnalité: Calcul de distance et optimisation de route
  En tant que système d'urgence médicale
  Je veux calculer précisément les distances et optimiser les routes vers les hôpitaux
  Afin de minimiser les temps de transport et améliorer les soins

  Scénario: Calcul de distance entre deux points identiques
    Étant donné deux points avec les mêmes coordonnées 53.3976314, -2.1829641
    Quand je calcule la distance entre ces points
    Alors la distance doit être 0 kilomètre

  Scénario: Calcul de distance entre Manchester et Liverpool
    Étant donné le point de départ Manchester avec les coordonnées 53.4808, -2.2426
    Et le point d'arrivée Liverpool avec les coordonnées 53.4106, -2.9779
    Quand je calcule la distance entre ces points
    Alors la distance doit être comprise entre 40 et 70 kilomètres

  Scénario: Calcul de distance vers un hôpital
    Étant donné un patient aux coordonnées 53.4808, -2.2426
    Et un hôpital "Hôpital Central" aux coordonnées 53.3976314, -2.1829641
    Quand je calcule la distance du patient vers l'hôpital
    Alors la distance doit être positive
    Et la distance doit être inférieure à 100 kilomètres

  Scénario: Estimation du temps de trajet basée sur la distance
    Étant donné une distance de 5 kilomètres
    Quand j'estime le temps de trajet à une vitesse moyenne de 50 km/h
    Alors le temps estimé doit être 6 minutes

  Scénario: Estimation du temps de trajet pour une longue distance
    Étant donné une distance de 50 kilomètres
    Quand j'estime le temps de trajet à une vitesse moyenne de 50 km/h
    Alors le temps estimé doit être 60 minutes

  Scénario: Estimation du temps de trajet pour une distance nulle
    Étant donné une distance de 0 kilomètre
    Quand j'estime le temps de trajet
    Alors le temps estimé doit être 0 minute

  Scénario: Calcul de route optimale avec trafic en temps réel
    Étant donné un point de départ aux coordonnées 53.4808, -2.2426
    Et un point d'arrivée aux coordonnées 53.3976314, -2.1829641
    Quand je calcule la route optimale avec prise en compte du trafic
    Alors la route doit inclure la distance en kilomètres
    Et la route doit inclure le temps de trajet en minutes
    Et la route ne doit pas contenir d'erreur

  Scénario: Échec de calcul de route - erreur API
    Étant donné des coordonnées invalides
    Quand je calcule la route optimale
    Alors la réponse doit indiquer une erreur
    Et un message d'erreur explicite doit être fourni

  Scénario: Calcul de route optimale vers un hôpital spécifique
    Étant donné un patient aux coordonnées 53.4808, -2.2426
    Et l'hôpital "Hôpital Nord" aux coordonnées 53.4808, -2.2426
    Quand je calcule la route optimale vers cet hôpital
    Alors la distance calculée doit être précise
    Et le temps de trajet doit tenir compte du trafic en temps réel
    Et la route doit être optimisée pour les véhicules d'urgence

  Scénario: Comparaison de routes vers plusieurs hôpitaux
    Étant donné un patient aux coordonnées 53.3976314, -2.1829641
    Et plusieurs hôpitaux disponibles:
      | Nom             | Latitude   | Longitude  |
      | Hôpital Central | 53.3976314 | -2.1829641 |
      | Hôpital Nord   | 53.4808    | -2.2426    |
      | Hôpital Sud    | 53.3500    | -2.1000    |
    Quand je calcule les routes vers tous les hôpitaux
    Alors les routes doivent être triées par temps de trajet croissant
    Et l'hôpital avec le temps de trajet le plus court doit être en première position
