Feature: Sécurité et conformité RGPD
  En tant qu’architecte logiciel
  Je veux m’assurer que les données patient sont anonymisées et sécurisées
  Afin de respecter le RGPD et les principes de l’architecture

  Scenario: Anonymisation des données avant envoi
    Given un objet Patient contenant "nom", "date_naissance", "pathologie"
    When la requête d’allocation est envoyée
    Then le champ "nom" doit être remplacé par un identifiant anonyme
    And aucune donnée personnelle identifiable n’est transmise à l’API