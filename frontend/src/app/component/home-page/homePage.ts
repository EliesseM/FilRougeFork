import {Component, inject, OnDestroy, OnInit, signal} from '@angular/core';
import {CommonModule, DatePipe} from '@angular/common';
import {RouterLink} from '@angular/router';
import {Subscription} from 'rxjs';

import {NotificationService} from '../../services/notification/notificationService';
import {EvenementService} from '../../services/evenement/evenementService';
import {SessionService} from '../../services/session/sessionService';
import {AuthGoogleService} from '../../services/auth/auth-google.service';
import {EvenementDto} from '../../models/evenement';
import {Notification as AppNotification} from '../../models/notification';
import {StatutEvenement} from '../evenement/enum/statutEvenement.enum';

interface StatutConfig {
  label: string;
  cssClass: string;
  icon: string;
}

@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [CommonModule, DatePipe, RouterLink],
  templateUrl: './homePage.html',
  styleUrls: ['./homePage.scss'],
})
export class HomePage implements OnInit, OnDestroy {

  private notificationService = inject(NotificationService);
  private evenementService = inject(EvenementService);
  private sessionService = inject(SessionService);
  private authService = inject(AuthGoogleService);

  totalEvenements = signal<number>(0);
  totalSessions = signal<number>(0);
  totalInscrits = signal<number>(0);

  // notifs global
  notifications: AppNotification[] = [];
  notifLoading = signal(false);

  upcomingEvenements: EvenementDto[] = [];
  evLoading = signal(false);

  private subs = new Subscription();

  private readonly statutConfigs: Record<string, StatutConfig> = {
    [StatutEvenement.Confirmer]: {label: 'Confirmé', cssClass: 'statut--confirme', icon: 'check_circle'},
    [StatutEvenement.En_attente]: {label: 'En attente', cssClass: 'statut--attente', icon: 'schedule'},
    [StatutEvenement.Annuler]: {label: 'Annulé', cssClass: 'statut--annule', icon: 'cancel'},
  };

  ngOnInit(): void {
    this.loadEvenements();
    this.loadSessions();
    this.loadNotifications();
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }

  private loadEvenements(): void {
    this.evLoading.set(true);

    this.subs.add(
      this.evenementService.getAll().subscribe({
        next: (page) => {
          const now = new Date();

          this.totalEvenements.set(page.totalElements);

          this.upcomingEvenements = page.content
            .filter(ev => new Date(ev.datesDto.dateDebutEvenement) >= now)
            .sort((a, b) =>
              new Date(a.datesDto.dateDebutEvenement).getTime() -
              new Date(b.datesDto.dateDebutEvenement).getTime()
            )
            .slice(0, 5);

          this.evLoading.set(false);
        },
        error: () => this.evLoading.set(false),
      })
    );
  }

  private loadSessions(): void {
    this.subs.add(
      this.sessionService.getAll().subscribe({
        next: (page) => this.totalSessions.set(page.content.length),
        error: () => {
        }
      })
    );
  }

  private loadNotifications(): void {
    const userId = this.authService.utilisateur()?.utilisateurId;

    if (!userId) {
      // Utilisateur non connecté, rien à charger
      return;
    }

    this.notifLoading.set(true);

    this.subs.add(
      this.notificationService.getNotificationsByUtilisateur(userId, 0, 10).subscribe({
        next: (page) => {
          this.notifications = page.content;
          this.notifLoading.set(false);
        },
        error: () => this.notifLoading.set(false),
      })
    );
  }

  markAsRead(notificationId: number): void {
    this.subs.add(
      this.notificationService.marquerCommeLu(notificationId).subscribe({
        next: () => {
          const notif = this.notifications.find(n => n.notificationId === notificationId);
          if (notif) notif.isLu = true;
        },
      })
    );
  }

  getStatutConfig(statut: StatutEvenement): StatutConfig {
    return this.statutConfigs[statut] ?? {
      label: statut,
      cssClass: 'statut--attente',
      icon: 'help_outline',
    };
  }

  getNotifType(notif: AppNotification): 'info' | 'success' | 'warning' | 'danger' {
    const t = notif.titre?.toLowerCase() ?? '';
    if (t.includes('annul') || t.includes('urgent') || t.includes('erreur')) return 'danger';
    if (t.includes('confirm') || t.includes('succès') || t.includes('validé')) return 'success';
    if (t.includes('attente') || t.includes('maintenance') || t.includes('attention')) return 'warning';
    return 'info';
  }

  getTypeLabel(type: string): string {
    const labels: Record<string, string> = {
      info: 'Info', success: 'Succès', warning: 'Attention', danger: 'Urgent',
    };
    return labels[type] ?? type;
  }

  // Compte les notifs non lues possiblement utile ? A voir avec le E et le B
  get unreadCount(): number {
    return this.notifications.filter(n => !n.isLu).length;
  }
}
