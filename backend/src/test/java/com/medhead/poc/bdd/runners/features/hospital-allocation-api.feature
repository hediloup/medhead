@api @e2e
Feature: Allocation d’un lit d’hôpital selon la spécialité et la localisation
  En tant que système d’intervention d’urgence
  Je veux recommander l’hôpital adéquat le plus proche
  Afin d’attribuer un lit disponible dans la bonne spécialité

  Scenario: Attribution d’un lit en cardiologie pour un patient proche de Fred Brooks
    Given un patient nécessitant des soins en "Cardiologie"
    And la localisation du patient est "51.5009, -0.1253"
    When l’API d’allocation est appelée avec ces paramètres
    Then le code HTTP doit être 200
    And la réponse doit contenir "Hôpital Fred Brooks"