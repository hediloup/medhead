# language: fr
Fonctionnalité: Performance et fiabilité de l'API
  En tant qu'administrateur système
  Je veux m'assurer que l'API peut gérer la charge et répondre rapidement
  Afin de garantir la disponibilité du service d'urgence

  Scénario: Test de charge - 100 requêtes simultanées
    Étant donné qu'il existe des hôpitaux avec la spécialité "Cardiology"
    Et qu'un générateur de charge simule 100 requêtes simultanées sur l'endpoint /api/allocate
    Quand les requêtes sont exécutées pendant 1 minute
    Alors 95% des requêtes doivent avoir un temps de réponse inférieur à 2000 millisecondes
    Et aucune erreur de timeout ne doit être observée
    Et le taux d'erreur doit être inférieur à 1%

  Scénario: Test de charge - 50 requêtes par seconde
    Étant donné qu'il existe des hôpitaux avec différentes spécialités
    Et qu'un générateur de charge simule 50 requêtes par seconde sur l'endpoint /api/allocate
    Quand les requêtes sont exécutées pendant 2 minutes
    Alors 90% des requêtes doivent avoir un temps de réponse inférieur à 1000 millisecondes
    Et aucune erreur 5xx ne doit être observée
    Et le système doit maintenir sa stabilité

  Scénario: Test de stress - augmentation progressive de la charge
    Étant donné qu'il existe des hôpitaux avec la spécialité "Cardiology"
    Et que la charge augmente progressivement de 10 à 100 requêtes par seconde
    Quand le test de stress est exécuté pendant 5 minutes
    Alors le système doit maintenir un temps de réponse acceptable
    Et aucune panne ne doit se produire
    Et les erreurs doivent rester dans des limites acceptables

  Scénario: Test de récupération après surcharge
    Étant donné qu'il existe des hôpitaux avec la spécialité "Cardiology"
    Et que le système a été surchargé avec 200 requêtes par seconde
    Quand la charge revient à la normale (10 requêtes par seconde)
    Alors le système doit se rétablir automatiquement
    Et les temps de réponse doivent revenir aux niveaux normaux
    Et aucune donnée ne doit être perdue

  Scénario: Test de disponibilité continue
    Étant donné que l'API est en fonctionnement normal
    Quand je surveille la disponibilité pendant 24 heures
    Alors le taux de disponibilité doit être supérieur à 99.9%
    Et tous les endpoints principaux doivent rester accessibles
    Et les temps de réponse doivent rester stables

  Scénario: Test de performance de l'endpoint de santé
    Étant donné que l'endpoint /api/health est disponible
    Et qu'un générateur de charge simule 1000 requêtes par minute
    Quand les requêtes sont exécutées pendant 10 minutes
    Alors 99% des requêtes doivent avoir un temps de réponse inférieur à 100 millisecondes
    Et toutes les réponses doivent retourner le statut 200

  Scénario: Test de performance avec base de données
    Étant donné qu'il existe 1000 hôpitaux dans la base de données
    Et que 500 patients sont en cours de traitement
    Quand je demande une allocation pour la spécialité "Cardiology"
    Alors le temps de réponse doit rester inférieur à 500 millisecondes
    Et la requête doit retourner le bon hôpital
    Et la base de données doit rester stable

  Scénario: Test de concurrence - allocations simultanées
    Étant donné qu'il existe un hôpital avec 10 lits disponibles
    Et que 15 requêtes d'allocation simultanées sont envoyées
    Quand les requêtes sont traitées
    Alors les 10 premières requêtes doivent réussir
    Et les 5 dernières doivent recevoir une erreur appropriée
    Et aucun lit ne doit être sur-alloué
    Et la cohérence des données doit être préservée

  Scénario: Test de mémoire - traitement de gros volumes
    Étant donné qu'il existe 10000 hôpitaux dans la base de données
    Et que 10000 requêtes d'allocation sont envoyées en parallèle
    Quand les requêtes sont traitées
    Alors l'utilisation de la mémoire doit rester stable
    Et aucun dépassement de mémoire ne doit se produire
    Et le garbage collector doit fonctionner normalement

  Scénario: Test de récupération après redémarrage
    Étant donné que le système est en fonctionnement normal
    Et qu'il y a des requêtes en cours de traitement
    Quand le système est redémarré
    Alors les nouvelles requêtes doivent être traitées correctement
    Et les données en cours doivent être récupérées
    Et la performance doit revenir rapidement au niveau normal
