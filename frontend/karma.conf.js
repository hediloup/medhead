// Karma configuration file, see link for more information
// https://karma-runner.github.io/1.0/config/configuration-file.html

module.exports = function (config) {
  // Configuration pour Docker/CI
  if (process.env.CI) {
    config.set({
      customLaunchers: {
        ChromeHeadless: {
          base: 'ChromeHeadless',
          flags: ['--no-sandbox', '--disable-web-security', '--disable-gpu', '--remote-debugging-port=9222', '--disable-dev-shm-usage', '--disable-extensions']
        }
      }
    });
  }
  
  config.set({
    basePath: '',
    frameworks: ['jasmine', '@angular-devkit/build-angular'],
    plugins: [
      require('karma-jasmine'),
      require('karma-chrome-launcher'),
      require('karma-jasmine-html-reporter'),
      require('karma-coverage'),
      require('@angular-devkit/build-angular/plugins/karma')
    ],
    client: {
      jasmine: {
        // Configuration pour Jasmine
        random: false,
        seed: '4321',
        stopOnSpecFailure: false,
        failFast: false,
        timeout: 30000
      },
      clearContext: false // laisse les logs de Jasmine dans la console après avoir exécuté les tests
    },
    jasmineHtmlReporter: {
      suppressAll: true // supprime les traces de traces
    },
    coverageReporter: {
      dir: require('path').join(__dirname, './coverage/medhead-frontend'),
      subdir: '.',
      reporters: [
        { type: 'html' },
        { type: 'text-summary' },
        { type: 'lcov' },
        { type: 'json' }
      ],
      check: {
        global: {
          statements: 80,
          branches: 80,
          functions: 80,
          lines: 80
        }
      }
    },
    reporters: ['progress', 'kjhtml', 'coverage'],
    port: 9876,
    colors: true,
    logLevel: config.LOG_INFO,
    autoWatch: true,
    browsers: process.env.CI ? ['ChromeHeadless'] : ['Chrome'],
    singleRun: process.env.CI ? true : false,
    restartOnFileChange: true,
    customLaunchers: {
      ChromeHeadlessCI: {
        base: 'ChromeHeadless',
        flags: ['--no-sandbox', '--disable-web-security', '--disable-gpu', '--remote-debugging-port=9222', '--disable-dev-shm-usage', '--disable-extensions']
      }
    },
    // Configuration pour les tests avec couverture
    preprocessors: {
      'src/**/*.ts': ['coverage']
    },
    // Configuration pour les tests E2E
    files: [
      'src/**/*.spec.ts'
    ],
    // Configuration pour les tests avec mocks
    mime: {
      'text/x-typescript': ['ts','tsx']
    }
  });
};
