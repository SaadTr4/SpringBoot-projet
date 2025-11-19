import { bootstrapApplication } from '@angular/platform-browser';
import { App } from './app/app';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { routes } from './app/app-routing-module';

bootstrapApplication(App, {
  providers: [
    provideHttpClient(), // Remplace HttpClientModule
    provideRouter(routes)
  ]
}).catch(err => console.error(err));
