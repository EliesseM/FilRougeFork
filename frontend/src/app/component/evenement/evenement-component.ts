import {Component, computed, inject, OnInit, signal} from '@angular/core';
import {CommonModule, DatePipe} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatChipsModule} from '@angular/material/chips';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatSelectModule} from '@angular/material/select';
import {MatMenuModule} from '@angular/material/menu';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatDividerModule} from '@angular/material/divider';
import {MatBadgeModule} from '@angular/material/badge';
import {StatutEvenement} from './enum/statutEvenement.enum';
import {EvenementDto} from '../../models/evenement';
import {EvenementService} from '../../services/evenement/evenementService';
import {Router, RouterLink} from '@angular/router';
import {AuthGoogleService} from '../../services/auth/auth-google.service';

@Component({
  selector: 'app-evenement',
  standalone: true,
  imports: [
    CommonModule, FormsModule, DatePipe,
    MatCardModule, MatButtonModule, MatIconModule,
    MatChipsModule, MatInputModule, MatFormFieldModule,
    MatSelectModule, MatMenuModule, MatTooltipModule,
    MatProgressBarModule, MatDividerModule, MatBadgeModule,
    RouterLink,
  ],
  templateUrl: './evenement-component.html',
  styleUrls: ['./evenement-component.scss'],
})
export class EvenementComponent implements OnInit {
  private authService = inject(AuthGoogleService);

  constructor(private evenementService: EvenementService, private router: Router) {
  }

  readonly searchQuery = signal('');
  readonly filterStatut = signal<StatutEvenement | 'Tous'>('Tous');
  readonly viewMode = signal<'grid' | 'list'>('grid');
  readonly loading = signal(false);
  readonly evenements = signal<EvenementDto[]>([]);
  readonly StatutEvenement = StatutEvenement;
  readonly statuts: Array<StatutEvenement | 'Tous'> = [
    'Tous', StatutEvenement.En_attente, StatutEvenement.Confirmer, StatutEvenement.Annuler,
  ];

  ngOnInit(): void {
    this.loadEvenements();
  }

  loadEvenements(): void {
    this.loading.set(true);
    this.evenementService.getAll().subscribe({
      next: (res: { content: EvenementDto[] }) => {
        this.evenements.set(res.content);
        this.loading.set(false);
      },
      error: (err) => {
        console.error(err);
        this.loading.set(false);
      }
    });
  }

  readonly filteredEvenements = computed(() => {
    const query = this.searchQuery().toLowerCase();
    const statut = this.filterStatut();
    return this.evenements().filter(ev => {
      const matchSearch = !query ||
        ev.infoDto.titre.toLowerCase().includes(query) ||
        ev.infoDto.description.toLowerCase().includes(query) ||
        ev.infoDto.localisation.toLowerCase().includes(query);
      const matchStatut = statut === 'Tous' || ev.statutDto.statutEvenement === statut;
      return matchSearch && matchStatut;
    });
  });

  readonly stats = computed(() => ({
    total: this.evenements().length,
    confirmes: this.evenements().filter(e => e.statutDto.statutEvenement === StatutEvenement.Confirmer).length,
    attente: this.evenements().filter(e => e.statutDto.statutEvenement === StatutEvenement.En_attente).length,
    annules: this.evenements().filter(e => e.statutDto.statutEvenement === StatutEvenement.Annuler).length,
  }));

  private get utilisateurRef() {
    return {utilisateurId: this.authService.utilisateur()?.utilisateurId ?? ''};
  }

  onDelete(ev: EvenementDto, event: MouseEvent): void {
    event.stopPropagation();
    this.evenementService.delete(ev.evenementId, this.utilisateurRef).subscribe(() => this.loadEvenements());
  }

  onAnnuler(ev: EvenementDto, event: MouseEvent): void {
    event.stopPropagation();
    this.evenementService.annuler(ev.evenementId, this.utilisateurRef).subscribe(() => this.loadEvenements());
  }

  onEdit(ev: EvenementDto, event: MouseEvent): void {
    event.stopPropagation();
    this.router.navigate(['/evenements', ev.evenementId]);
  }

  onSearch(value: string): void {
    this.searchQuery.set(value);
  }

  onFilterChange(statut: StatutEvenement | 'Tous'): void {
    this.filterStatut.set(statut);
  }

  getInputValue(event: Event): string {
    return (event.target as HTMLInputElement).value;
  }

  trackById(_: number, ev: EvenementDto): number {
    return ev.evenementId;
  }

  formatDuree(heures: number): string {
    if (heures < 24) return `${heures}h`;
    const jours = Math.floor(heures / 24);
    const reste = heures % 24;
    return reste > 0 ? `${jours}j ${reste}h` : `${jours} jour${jours > 1 ? 's' : ''}`;
  }

  getStatutConfig(statut: StatutEvenement): { label: string; icon: string; cssClass: string } {
    const map: Record<StatutEvenement, { label: string; icon: string; cssClass: string }> = {
      [StatutEvenement.Confirmer]: {label: 'Confirmé', icon: 'check_circle', cssClass: 'statut--confirme'},
      [StatutEvenement.En_attente]: {label: 'En attente', icon: 'schedule', cssClass: 'statut--attente'},
      [StatutEvenement.Annuler]: {label: 'Annulé', icon: 'cancel', cssClass: 'statut--annule'},
    };
    return map[statut];
  }
}
