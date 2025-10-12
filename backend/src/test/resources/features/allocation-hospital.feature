# language: fr
Fonctionnalité: Allocation d'hôpitaux pour patients d'urgence
  En tant que système d'urgence médicale
  Je veux pouvoir allouer automatiquement un hôpital approprié à un patient
  Afin d'optimiser les soins et réduire les temps d'attente

  Scénario: Allocation réussie d'un hôpital avec spécialité disponible
    Étant donné qu'il existe un hôpital "Hôpital Central" avec la spécialité "Cardiology" et 5 lits disponibles
    Et que le patient se trouve aux coordonnées 53.3976314, -2.1829641
    Quand je demande une allocation pour la spécialité "Cardiology"
    Alors l'hôpital "Hôpital Central" doit être recommandé
    Et la distance doit être calculée correctement
    Et le temps d'arrivée estimé doit être fourni
    Et le nombre de lits disponibles après allocation doit être 4

  Scénario: Sélection de l'hôpital le plus proche parmi plusieurs options
    Étant donné qu'il existe plusieurs hôpitaux avec la spécialité "Cardiology":
      | Nom                    | Latitude   | Longitude  | Lits |
      | Hôpital Central        | 53.3976314 | -2.1829641 | 5    |
      | Hôpital Nord          | 53.4808    | -2.2426    | 3    |
      | Hôpital Sud           | 53.3500    | -2.1000    | 4    |
    Et que le patient se trouve aux coordonnées 53.3976314, -2.1829641
    Quand je demande une allocation pour la spécialité "Cardiology"
    Alors l'hôpital le plus proche doit être sélectionné
    Et la réponse doit contenir les informations de route

  Scénario: Échec d'allocation - aucune spécialité disponible
    Étant donné qu'il n'existe aucun hôpital avec la spécialité "Pediatrics"
    Et que le patient se trouve aux coordonnées 53.3976314, -2.1829641
    Quand je demande une allocation pour la spécialité "Pediatrics"
    Alors je dois recevoir une erreur "Aucun hôpital disponible"
    Et le code de statut HTTP doit être 404

  Scénario: Échec d'allocation - aucun lit disponible
    Étant donné qu'il existe un hôpital "Hôpital Complet" avec la spécialité "Cardiology" et 0 lit disponible
    Et que le patient se trouve aux coordonnées 53.3976314, -2.1829641
    Quand je demande une allocation pour la spécialité "Cardiology"
    Alors je dois recevoir une erreur "Aucun hôpital disponible"
    Et le code de statut HTTP doit être 404

  Scénario: Validation des paramètres d'entrée - spécialité manquante
    Étant donné que le patient se trouve aux coordonnées 53.3976314, -2.1829641
    Quand je demande une allocation avec une spécialité vide ""
    Alors je dois recevoir une erreur de validation
    Et le code de statut HTTP doit être 400

  Scénario: Validation des paramètres d'entrée - coordonnées manquantes
    Étant donné qu'il existe un hôpital avec la spécialité "Cardiology"
    Quand je demande une allocation pour la spécialité "Cardiology" avec des coordonnées nulles
    Alors je dois recevoir une erreur de validation
    Et le code de statut HTTP doit être 400

  Scénario: Test de l'endpoint GET
    Étant donné qu'il existe un hôpital "Hôpital Central" avec la spécialité "Cardiology"
    Quand j'appelle l'endpoint GET /api/allocate avec les paramètres:
      | Paramètre | Valeur      |
      | specialty | Cardiology  |
      | latitude  | 53.3976314  |
      | longitude | -2.1829641  |
    Alors la réponse doit être identique à l'endpoint POST
    Et l'hôpital "Hôpital Central" doit être recommandé

  Scénario: Vérification du statut de santé de l'API
    Quand j'appelle l'endpoint /api/health
    Alors je dois recevoir le message "Allocation API operational"
    Et le code de statut HTTP doit être 200
