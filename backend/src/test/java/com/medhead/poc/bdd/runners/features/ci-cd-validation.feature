Feature: Validation continue dans le pipeline CI/CD
  En tant qu’équipe DevOps
  Je veux que tous les tests BDD soient exécutés automatiquement à chaque push
  Afin de garantir la qualité et la traçabilité des livraisons

  Scenario: Exécution automatisée des tests BDD
    Given un commit est poussé sur la branche "main"
    When le pipeline CI/CD est déclenché
    Then les étapes "build", "test", "deploy" doivent s’exécuter avec succès
    And un rapport de tests est généré dans /reports/cucumber.json
    And le statut du pipeline doit être "passed"