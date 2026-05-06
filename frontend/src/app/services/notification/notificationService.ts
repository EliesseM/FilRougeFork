import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, Subject, throwError} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {MatSnackBar} from '@angular/material/snack-bar';
import {environnement} from '../../environnement/environnement';
import {Notification as AppNotification} from '../../models/notification';
import {WebSocketService} from "../websocket/webSocketService";

export interface NotificationPage {
  content: AppNotification[];
  totalElements: number;
  totalPages: number;
  number: number;
}

@Injectable({providedIn: 'root'})
export class NotificationService {

  private apiUrl = `${environnement.apiBaseUrl}/notifications`;

  constructor(
    private http: HttpClient,
    private wsService: WebSocketService,
    private snackBar: MatSnackBar
  ) {
  }

  private handleRequest<T>(obs: Observable<T>): Observable<T> {
    return obs.pipe(
      catchError((error) => {
        this.showError(error);
        return throwError(() => error);
      })
    );
  }

  showError(error: any) {
    let message = 'Une erreur est survenue :';

    if (typeof error === 'string') {
      message = error; // ✅ string directe
    } else if (error?.error?.message) {
      message = error.error.message;
    } else if (error?.message) {
      message = error.message;
    }

    this.snackBar.open(message, 'Fermer', {
      duration: 5000,
      panelClass: ['error-snackbar']
    });
  }

  getNotificationsByUtilisateur(utilisateurId: string, page = 0, size = 10): Observable<NotificationPage> {
    return this.handleRequest(
      this.http.get<NotificationPage>(
        `${this.apiUrl}/utilisateur/${utilisateurId}?page=${page}&size=${size}`
      )
    );
  }

  marquerCommeLu(notificationId: number): Observable<void> {
    return this.handleRequest(
      this.http.put<void>(`${this.apiUrl}/lu/${notificationId}`, {})
    );
  }

  subscribeToEvenement(evenementId: number): Subject<string> {
    return this.wsService.subscribe(`/topic/notification/evenement-${evenementId}`);
  }

  subscribeToSession(sessionId: number): Subject<string> {
    return this.wsService.subscribe(`/topic/notification/session-${sessionId}`);
  }

  unsubscribeFromEvenement(evenementId: number): void {
    this.wsService.unsubscribe(`/topic/notification/evenement-${evenementId}`);
  }

  unsubscribeFromSession(sessionId: number): void {
    this.wsService.unsubscribe(`/topic/notification/session-${sessionId}`);
  }
}
