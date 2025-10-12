// Configuration globale pour les tests
import 'zone.js/testing';
import { getTestBed } from '@angular/core/testing';
import { BrowserDynamicTestingModule, platformBrowserDynamicTesting } from '@angular/platform-browser-dynamic/testing';

// Configuration Jasmine
declare global {
  namespace jasmine {
    interface Matchers<T> {
      toBeValidCoordinates(): boolean;
    }
  }
}

// Configuration des tests Angular
getTestBed().initTestEnvironment(
  BrowserDynamicTestingModule,
  platformBrowserDynamicTesting()
);

// Matcher personnalisé pour les coordonnées
beforeEach(() => {
  jasmine.addMatchers({
    toBeValidCoordinates: () => ({
      compare: (actual: any) => {
        const pass = actual && 
                    typeof actual.lat === 'number' && 
                    typeof actual.lon === 'number' &&
                    !isNaN(actual.lat) && 
                    !isNaN(actual.lon);
        
        return {
          pass,
          message: pass 
            ? 'Expected coordinates to be invalid'
            : `Expected coordinates to be valid, got ${JSON.stringify(actual)}`
        };
      }
    })
  });
});
