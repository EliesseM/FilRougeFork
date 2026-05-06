import {Component, ElementRef, HostListener, inject, OnDestroy, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {Subscription} from 'rxjs';
import {NotificationService} from '../../../services/notification/notificationService';
import {Notification as AppNotification} from '../../../models/notification';
import {AuthGoogleService} from '../../../services/auth/auth-google.service';

@Component({
  selector: 'app-notification-bell',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notification-bell.component.html',
  styleUrls: ['./notification-bell.component.scss']
})
export class NotificationBellComponent implements OnInit, OnDestroy {
  private authService = inject(AuthGoogleService);

  notifications = signal<AppNotification[]>([]);
  unreadCount = signal(0);
  isPanelOpen = signal(false);

  private userId = '';
  private subs: Subscription[] = [];
  private subscribedEvenementIds: number[] = [];

  constructor(private notifService: NotificationService, private elRef: ElementRef) {
  }

  ngOnInit(): void {
    this.userId = this.authService.utilisateur()?.utilisateurId ?? '';
    if (!this.userId) {
      console.warn('[NotificationBell] userId vide, notifs non chargées');
      return;
    }
    this.chargerNotifications();
  }

  private chargerNotifications(): void {
    this.notifService.getNotificationsByUtilisateur(this.userId).subscribe({
      next: (page) => {
        const notifications = page.content;
        this.notifications.set(notifications);
        this.updateUnreadCount();
        this.subscribeToEvenements(notifications);
      },
      error: (err) => console.error('[NotificationBell] Échec chargement:', err),
    });
  }

  private subscribeToEvenements(notifications: AppNotification[]): void {
    const evenementIds = [
      ...new Set(
        notifications
          .filter(n => n.evenementId != null)
          .map(n => n.evenementId!)
      )
    ];

    for (const evenementId of evenementIds) {
      if (this.subscribedEvenementIds.includes(evenementId)) continue;
      const subject = this.notifService.subscribeToEvenement(evenementId);
      const sub = subject.subscribe({
        next: (message) => {
          console.log(`[WS] Nouvelle notif événement ${evenementId}:`, message);
          this.chargerNotifications();
        }
      });
      this.subs.push(sub);
      this.subscribedEvenementIds.push(evenementId);
    }
  }

  private updateUnreadCount(): void {
    this.unreadCount.set(this.notifications().filter(n => !n.isLu).length);
  }

  openPanel(): void {
    this.isPanelOpen.set(true);
    if (this.unreadCount() > 0) {
      setTimeout(() => {
        const unreadNotifs = this.notifications().filter(n => !n.isLu);
        unreadNotifs.forEach(n => {
          this.notifService.marquerCommeLu(n.notificationId).subscribe();
        });
        this.notifications.update(notifs => notifs.map(n => ({...n, isLu: true})));
        this.unreadCount.set(0);
      }, 700);
    }
  }

  togglePanel(): void {
    this.isPanelOpen() ? this.closePanel() : this.openPanel();
  }

  closePanel(): void {
    this.isPanelOpen.set(false);
  }

  @HostListener('document:click', ['$event'])
  onOutsideClick(event: MouseEvent): void {
    if (this.isPanelOpen() && !this.elRef.nativeElement.contains(event.target as Node)) {
      this.closePanel();
    }
  }

  formatDate(date: Date): string {
    const d = new Date(date);
    const diff = Math.floor((Date.now() - d.getTime()) / 1000);
    if (diff < 60) return "À l'instant";
    if (diff < 3600) return `Il y a ${Math.floor(diff / 60)} min`;
    if (diff < 86400) return `Il y a ${Math.floor(diff / 3600)} h`;
    return d.toLocaleDateString('fr-FR', {day: 'numeric', month: 'short'});
  }

  trackByNotif(_: number, n: AppNotification): number {
    return n.notificationId;
  }

  ngOnDestroy(): void {
    this.subs.forEach(s => s.unsubscribe());
    this.subscribedEvenementIds.forEach(id =>
      this.notifService.unsubscribeFromEvenement(id)
    );
  }
}
