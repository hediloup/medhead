import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppModule } from './app/app.module';

// Supprimer les erreurs d'extensions de navigateur de la console
const originalConsoleError = console.error;
console.error = (...args: any[]) => {
  const message = args[0]?.toString() || '';
  // Ignorer les erreurs d'extensions de navigateur
  if (!message.includes('runtime.lastError') && 
      !message.includes('message port closed') &&
      !message.includes('Extension context invalidated')) {
    originalConsoleError.apply(console, args);
  }
};

platformBrowserDynamic().bootstrapModule(AppModule)
  .catch(err => console.error(err));
