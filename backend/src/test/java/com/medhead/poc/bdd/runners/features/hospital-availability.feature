Feature: Vérification de la disponibilité des lits
  En tant que service hospitalier
  Je veux gérer la disponibilité des lits en fonction des spécialités
  Afin que le moteur d’allocation puisse prendre des décisions fiables

  Scenario Outline: Validation de la disponibilité selon la spécialité
    Given un hôpital nommé "<hopital>" ayant "<lits>" lits disponibles en "<specialite>"
    When le système vérifie la disponibilité pour "<specialite>"
    Then le statut doit être "<etat>"

    Examples:
      | hopital        | lits | specialite    | etat        |
      | Fred Brooks    | 2    | Cardiologie   | disponible  |
      | Julia Crusher  | 0    | Cardiologie   | indisponible |
      | Beverly Bashir | 5    | Immunologie   | disponible  |