const { defineConfig } = require('cypress');

module.exports = defineConfig({
  e2e: {
    baseUrl: 'http://localhost:4200',
    specPattern: 'cypress/e2e/**/*.cy.js',
    screenshotsFolder: 'reports/screenshots',
    videosFolder: 'reports/videos',
    supportFile: 'cypress/support/e2e.js'
  }
});