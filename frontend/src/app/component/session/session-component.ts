import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatDividerModule } from '@angular/material/divider';
import { MatBadgeModule } from '@angular/material/badge';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { Router, RouterLink } from '@angular/router';

import {SessionDto} from '../../models/session';
import { StatutSession } from './enum/statutSession.enum';
import { SessionService } from '../../services/session/sessionService';

@Component({
  selector: 'app-session',
  standalone: true,
  imports: [
    CommonModule, FormsModule, DatePipe,
    MatCardModule, MatButtonModule, MatIconModule,
    MatChipsModule, MatInputModule, MatFormFieldModule,
    MatSelectModule, MatMenuModule, MatTooltipModule,
    MatProgressBarModule, MatDividerModule, MatBadgeModule,
    MatProgressSpinner, RouterLink,
  ],
  templateUrl: './session-component.html',
  styleUrls: ['./session-component.scss'],
})
export class SessionComponent implements OnInit {

  constructor(
    private sessionService: SessionService,
    private router: Router
  ) {}

  readonly searchQuery  = signal('');
  readonly filterStatut = signal<StatutSession | 'Tous'>('Tous');
  readonly viewMode     = signal<'grid' | 'list'>('grid');
  readonly loading      = signal(false);
  readonly sessions     = signal<SessionDto[]>([]);

  readonly StatutSession = StatutSession;

  readonly statuts: Array<StatutSession | 'Tous'> = [
    'Tous',
    StatutSession.EnAttente,
    StatutSession.Confirmer,
    StatutSession.Annuler,
  ];

  ngOnInit(): void {
    this.loadSessions();
  }

  loadSessions(): void {
    this.loading.set(true);

    this.sessionService.getAll().subscribe({
      next: (res: { content: SessionDto[] }) => {
        this.sessions.set(res.content);
        this.loading.set(false); // ✅ IMPORTANT
      },
      error: (err) => {
        console.error(err);
        this.loading.set(false);
      }
    });
  }

  readonly filteredSessions = computed(() => {
    const query  = this.searchQuery().toLowerCase();
    const statut = this.filterStatut();

    return this.sessions().filter(s => {
      const matchSearch =
        !query ||
        s.infos.titre.toLowerCase().includes(query) ||
        s.infos.description.toLowerCase().includes(query) ||
        s.infos.lieu.toLowerCase().includes(query) ||
        s.infos.animateur.toLowerCase().includes(query);

      const matchStatut =
        statut === 'Tous' || s.statut.statutSession === statut;

      return matchSearch && matchStatut;
    });
  });

  readonly stats = computed(() => ({
    total: this.sessions().length,
    confirmes: this.sessions().filter(s => s.statut.statutSession === StatutSession.Confirmer).length,
    attente: this.sessions().filter(s => s.statut.statutSession === StatutSession.EnAttente).length,
    annules: this.sessions().filter(s => s.statut.statutSession === StatutSession.Annuler).length,
  }));

  onDelete(s: SessionDto, event: MouseEvent): void {
    event.stopPropagation();
    this.sessionService.delete(s.sessionId)
      .subscribe(() => this.loadSessions());
  }

  onAnnuler(s: SessionDto, event: MouseEvent): void {
    event.stopPropagation();
    this.sessionService.annuler(s.sessionId)
      .subscribe(() => this.loadSessions());
  }

  onEdit(s: SessionDto, event: MouseEvent): void {
    event.stopPropagation();
    this.router.navigate(['/sessions', s.sessionId]).then(r => r);
  }

  onSearch(value: string): void {
    this.searchQuery.set(value);
  }

  onFilterChange(statut: StatutSession | 'Tous'): void {
    this.filterStatut.set(statut);
  }

  getInputValue(event: Event): string {
    return (event.target as HTMLInputElement).value;
  }

  trackById(_: number, s: SessionDto): number {
    return s.sessionId;
  }

  formatDuree(heures: number): string {
    if (heures < 24) return `${heures}h`;
    const jours = Math.floor(heures / 24);
    const reste = heures % 24;
    return reste > 0 ? `${jours}j ${reste}h` : `${jours} jour${jours > 1 ? 's' : ''}`;
  }

  getStatutConfig(statut: StatutSession): { label: string; icon: string; cssClass: string } {
    const map: Record<StatutSession, { label: string; icon: string; cssClass: string }> = {
      [StatutSession.Confirmer]: { label: 'Confirmée',  icon: 'check_circle', cssClass: 'statut--confirme' },
      [StatutSession.EnAttente]: { label: 'En attente', icon: 'schedule',     cssClass: 'statut--attente'  },
      [StatutSession.Annuler]:   { label: 'Annulée',    icon: 'cancel',       cssClass: 'statut--annule'   },
    };
    return map[statut];
  }
}
