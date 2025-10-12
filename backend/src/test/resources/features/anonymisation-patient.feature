# language: fr
Fonctionnalité: Anonymisation et protection des données patients
  En tant que système médical conforme RGPD
  Je veux anonymiser automatiquement les données des patients
  Afin de protéger leur vie privée et respecter la réglementation

  Scénario: Création d'un patient anonymisé
    Étant donné qu'un nouveau patient nécessite une allocation
    Quand je crée un patient avec la spécialité "Cardiology" et les coordonnées 53.3976314, -2.1829641
    Alors le patient doit avoir un UUID unique
    Et le nom doit être anonymisé avec le format "PATIENT_XXXXXXXX"
    Et le patient doit être marqué comme anonymisé
    Et la date de création doit être définie
    Et la date d'expiration des données doit être fixée à 7 ans

  Scénario: Anonymisation d'un patient existant
    Étant donné qu'il existe un patient non anonymisé dans le système
    Quand j'anonymise ce patient
    Alors le nom du patient doit être remplacé par un identifiant anonyme
    Et le patient doit être marqué comme anonymisé
    Et les données sensibles doivent être supprimées

  Scénario: Vérification de l'accès aux données patient
    Étant donné qu'il existe un patient anonymisé et non expiré
    Quand je vérifie les permissions d'accès à ce patient
    Alors l'accès doit être autorisé

  Scénario: Refus d'accès à un patient non anonymisé
    Étant donné qu'il existe un patient non anonymisé
    Quand je vérifie les permissions d'accès à ce patient
    Alors l'accès doit être refusé

  Scénario: Refus d'accès à un patient expiré
    Étant donné qu'il existe un patient dont les données ont expiré (plus de 7 ans)
    Quand je vérifie les permissions d'accès à ce patient
    Alors l'accès doit être refusé

  Scénario: Suppression automatique des données expirées
    Étant donné qu'il existe des patients avec des données expirées
    Quand le système exécute le nettoyage automatique des données
    Alors tous les patients expirés doivent être supprimés définitivement
    Et un rapport de suppression doit être généré

  Scénario: Anonymisation en lot de patients non anonymisés
    Étant donné qu'il existe plusieurs patients non anonymisés dans le système
    Quand j'exécute l'anonymisation en lot
    Alors tous les patients non anonymisés doivent être anonymisés
    Et le nombre de patients traités doit être rapporté

  Scénario: Génération de statistiques anonymisées
    Étant donné qu'il existe des patients anonymisés dans le système
    Quand je demande les statistiques anonymisées
    Alors je dois recevoir des comptages par spécialité
    Et je dois recevoir des comptages par groupe d'âge
    Et aucune donnée personnelle ne doit être exposée
    Et la date de génération des statistiques doit être incluse

  Scénario: Nettoyage des patients avec données manquantes
    Étant donné qu'il existe des patients avec des données d'anonymisation manquantes
    Quand j'exécute le nettoyage des données manquantes
    Alors tous les patients avec des données manquantes doivent être anonymisés
    Et le nombre de patients traités doit être rapporté
