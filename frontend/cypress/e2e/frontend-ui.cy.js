describe('UI – Attribution de lits', () => {
  beforeEach(() => {
    cy.visit('/');
  });

  it('Sélection d’une spécialité et obtention de la recommandation', () => {
    // Sélectionne la spécialité dans une liste déroulante
    cy.get('[data-testid="speciality-select"]').select('Cardiologie');
    // Saisit la géolocalisation
    cy.get('[data-testid="geo-input"]').clear().type('51.5009,-0.1253');
    // Clique sur le bouton de recherche
    cy.get('[data-testid="search-button"]').click();
    // Vérifie que la carte affiche bien l’hôpital recommandé
    cy.contains('Hôpital Fred Brooks').should('be.visible');
  });
});