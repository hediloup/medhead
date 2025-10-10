// Placez ici vos commandes personnalisées Cypress.
// Vous pouvez ajouter des commandes réutilisables pour vos tests.

// Exemple : commande pour sélectionner une spécialité par son label
Cypress.Commands.add('selectSpeciality', (label) => {
  cy.get('[data-testid="speciality-select"]').select(label);
});