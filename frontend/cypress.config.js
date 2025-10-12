const { defineConfig } = require('cypress');

module.exports = defineConfig({
  e2e: {
    baseUrl: 'http://localhost:4200',
    specPattern: 'cypress/e2e/**/*.cy.js',
    screenshotsFolder: 'reports/screenshots',
    videosFolder: 'reports/videos',
    supportFile: 'cypress/support/e2e.js',
    viewportWidth: 1280,
    viewportHeight: 720,
    video: true,
    screenshotOnRunFailure: true,
    defaultCommandTimeout: 15000,
    requestTimeout: 15000,
    responseTimeout: 15000,
    pageLoadTimeout: 60000,
    retries: {
      runMode: 3,
      openMode: 0
    },
    waitForAnimations: true,
    animationDistanceThreshold: 20,
    env: {
      apiUrl: 'http://localhost:4200/api',
      geocodingUrl: 'http://localhost:4200/geocoding'
    }
  },
  component: {
    devServer: {
      framework: 'angular',
      bundler: 'webpack',
    },
    specPattern: 'src/**/*.cy.ts',
    supportFile: 'cypress/support/component.ts'
  }
});