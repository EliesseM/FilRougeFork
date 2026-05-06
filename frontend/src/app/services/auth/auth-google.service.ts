import {inject, Injectable, signal} from '@angular/core';
import {Router} from '@angular/router';
import {OAuthService} from 'angular-oauth2-oidc';
import {authConfig} from '../../auth/auth-config';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError, tap} from 'rxjs/operators';
import {throwError} from 'rxjs';
import {Utilisateur} from "../../models/utilisateur";

@Injectable({
  providedIn: 'root',
})
export class AuthGoogleService {
  private oAuthService = inject(OAuthService);
  private router = inject(Router);
  private http = inject(HttpClient);

  profile = signal<any>(null);
  utilisateur = signal<Utilisateur | null>(
    JSON.parse(localStorage.getItem('utilisateur') ?? 'null')
  );

  private readonly BACKEND_URL = 'http://localhost:8080/utilisateurs/register';

  constructor() {
    this.initConfiguration();
  }

  initConfiguration(): void {
    this.oAuthService.configure(authConfig);
    this.oAuthService.setupAutomaticSilentRefresh();

    this.oAuthService.events.subscribe((event) => {
      if (event.type === 'token_received') {
        const claims = this.oAuthService.getIdentityClaims();
        this.profile.set(claims);
        this.registerOrUpdateUser(claims);
      }
    });

    this.oAuthService.loadDiscoveryDocumentAndTryLogin().then(() => {
      if (this.oAuthService.hasValidAccessToken()) {
        const claims = this.oAuthService.getIdentityClaims();
        this.profile.set(claims);
        if (!this.utilisateur()) {
          this.registerOrUpdateUser(claims);
        }
      }
    });
  }

  login(): void {
    this.oAuthService.initCodeFlow();
  }

  logout(): void {
    this.oAuthService.revokeTokenAndLogout().then(() => {
      this.profile.set(null);
      this.utilisateur.set(null);
      localStorage.removeItem('utilisateur');
      this.router.navigate(['/login']);
    });
  }

  private isRegistering = false;

  registerOrUpdateUser(claims: any): void {
    if (this.isRegistering) return; // ⛔ bloque doublon
    this.isRegistering = true;

    const body: Partial<Utilisateur> = {
      utilisateurId: claims.sub,
      email: claims.email,
      prenom: claims.given_name || claims.family_name,
      nom: claims.family_name || claims.given_name,
    };

    this.http.post<Utilisateur>(this.BACKEND_URL, body).pipe(
      tap((response) => {
        this.utilisateur.set(response);
        localStorage.setItem('utilisateur', JSON.stringify(response));
        this.isRegistering = false;
      }),
      catchError((error) => {
        this.isRegistering = false;
        return throwError(() => error);
      })
    ).subscribe();
  }

  isLoggedIn(): boolean {
    return this.oAuthService.hasValidAccessToken();
  }
}
