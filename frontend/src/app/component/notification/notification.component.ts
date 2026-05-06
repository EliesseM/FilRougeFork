import {Component, computed, inject, OnDestroy, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatIconModule} from '@angular/material/icon';
import {MatCardModule} from '@angular/material/card';
import {MatDividerModule} from '@angular/material/divider';
import {Subscription} from 'rxjs';
import {NotificationService} from '../../services/notification/notificationService';
import {Notification as AppNotification} from '../../models/notification';
import {AuthGoogleService} from '../../services/auth/auth-google.service';

@Component({
  selector: 'app-notification',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatCardModule, MatDividerModule],
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.scss'],
})
export class NotificationComponent implements OnInit, OnDestroy {
  private authService = inject(AuthGoogleService);

  notifications = signal<AppNotification[]>([]);
  totalElements = signal(0);
  private subs: Subscription[] = [];

  unreadCount = computed(() => this.notifications().filter(n => !n.isLu).length);

  constructor(private notifService: NotificationService) {
  }

  ngOnInit(): void {
    const userId = this.authService.utilisateur()?.utilisateurId ?? '';
    this.notifService.getNotificationsByUtilisateur(userId).subscribe({
      next: (page) => {
        this.notifications.set(page.content);
        this.totalElements.set(page.totalElements);
      },
      error: (err) => console.error('Erreur chargement notifs:', err),
    });
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
    this.subs.forEach((s) => s.unsubscribe());
  }
}
