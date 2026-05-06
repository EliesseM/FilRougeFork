import {ApplicationConfig, importProvidersFrom, LOCALE_ID, provideZoneChangeDetection} from "@angular/core";
import {provideHttpClient, withInterceptors} from "@angular/common/http";
import {routes} from "./app.routes";
import {provideRouter} from "@angular/router";
import {authInterceptor} from "./interceptors/auth-interceptor";
import {registerLocaleData} from "@angular/common";
import localeFr from '@angular/common/locales/fr';
import {errorInterceptor} from './interceptors/error-interceptor';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {provideOAuthClient} from "angular-oauth2-oidc";

registerLocaleData(localeFr);

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({eventCoalescing: true}),
    provideRouter(routes),

    provideHttpClient(
      withInterceptors([authInterceptor, errorInterceptor])
    ),

    provideOAuthClient(),


    importProvidersFrom(MatSnackBarModule),
    {provide: LOCALE_ID, useValue: 'fr-FR'}
  ]
};
