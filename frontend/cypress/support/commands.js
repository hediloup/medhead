// Place your custom Cypress commands here.
// You can add reusable commands for your tests.

// Example: command to select a specialty by its label
Cypress.Commands.add('selectSpeciality', (label) => {
  cy.get('[data-testid="speciality-select"]').select(label);
});