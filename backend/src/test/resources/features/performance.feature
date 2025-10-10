Feature: Performance et résilience sous charge
  En tant qu’ingénieur QA
  Je veux valider que le service répond en moins de 200 ms
  Même sous 800 requêtes par seconde

  Scenario: Test de performance de l’API d’allocation
    Given un générateur de charge simulant 800 requêtes/s sur l’endpoint "/api/allocate"
    When les réponses sont mesurées sur une durée de 2 minutes
    Then 95% des requêtes doivent avoir un temps de réponse < 200 ms
    And aucun timeout ni 5xx ne doit être observé