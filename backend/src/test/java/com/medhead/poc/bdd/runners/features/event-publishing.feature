Feature: Publication d’événement après allocation de lit
  En tant que service d’orchestration
  Je veux publier un événement après une attribution réussie
  Afin d’assurer la cohérence entre microservices

  Scenario: Publication d’un événement “BED_RESERVED”
    Given une demande de lit validée pour "fred_brooks_001"
    When le système confirme la réservation
    Then un message avec type "BED_RESERVED" est publié sur le topic "hospital.events"
    And le message contient :
      | champ       | valeur           |
      | hospital_id | fred_brooks_001  |
      | speciality  | Cardiologie      |
      | timestamp   | non nul          |