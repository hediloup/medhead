// ***********************************************************
// This file is executed automatically before each test.
// You can define global hooks or import commands here.
// ***********************************************************

import './commands';

// Example of global hook: before each test, clear localStorage
beforeEach(() => {
  cy.clearLocalStorage();
});