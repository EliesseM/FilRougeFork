import {Component, computed, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule} from '@angular/router';
import {MatIconModule} from '@angular/material/icon';
import {MatMenuModule} from '@angular/material/menu';
import {MatButtonModule} from '@angular/material/button';
import {MatDividerModule} from '@angular/material/divider';

import {AdminService} from '../../services/admin/adminService';
import {AuthGoogleService} from '../../services/auth/auth-google.service';
import {NotificationBellComponent} from '../notification/notification-bell/notification-bell.component';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatIconModule,
    MatMenuModule,
    MatButtonModule,
    MatDividerModule,
    NotificationBellComponent,
  ],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
})
export class NavbarComponent implements OnInit {
  pendingTotal = 0;

  readonly profile = this.authService.profile;
  readonly utilisateur = this.authService.utilisateur;

  readonly isLoggedIn = computed(() => !!this.profile());
  readonly isAdmin = computed(() => this.utilisateur()?.role === 'ROLE_ADMIN');

  readonly displayName = computed(() => {
    const u = this.utilisateur();
    if (u) return `${u.prenom ?? ''} ${u.nom ?? ''}`.trim();
    const p = this.profile();
    return p?.name ?? p?.given_name ?? 'Utilisateur';
  });

  readonly email = computed(() => {
    return this.utilisateur()?.email ?? this.profile()?.email ?? '';
  });

  readonly photoUrl = computed(() => this.profile()?.picture ?? null);

  constructor(
    private adminService: AdminService,
    private authService: AuthGoogleService,
  ) {
  }

  ngOnInit(): void {
    if (this.isLoggedIn() && this.isAdmin()) {
      this.adminService.getCounts().subscribe(counts => {
        this.pendingTotal = counts.total;
      });
    }
  }

  signIn(): void {
    this.authService.login();
  }

  signOut(): void {
    this.authService.logout();
  }
}
