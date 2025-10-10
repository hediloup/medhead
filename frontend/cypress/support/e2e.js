// ***********************************************************
// Ce fichier est exécuté automatiquement avant chaque test.
// Vous pouvez y définir des hooks globaux ou importer des commandes.
// ***********************************************************

import './commands';

// Exemple de hook global : avant chaque test, vider le localStorage
beforeEach(() => {
  cy.clearLocalStorage();
});