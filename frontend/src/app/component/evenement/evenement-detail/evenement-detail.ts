import {Component, inject, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatNativeDateModule} from '@angular/material/core';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {EvenementService} from '../../../services/evenement/evenementService';
import {EvenementDto} from '../../../models/evenement';
import {AuthGoogleService} from '../../../services/auth/auth-google.service';

@Component({
  selector: 'app-evenement-detail',
  standalone: true,
  imports: [
    CommonModule, FormsModule,
    MatInputModule, MatFormFieldModule,
    MatButtonModule, MatIconModule,
    MatDatepickerModule, MatNativeDateModule
  ],
  templateUrl: './evenement-detail.html',
  styleUrls: ['./evenement-detail.scss']
})
export class EvenementDetailComponent implements OnInit {
  private authService = inject(AuthGoogleService);

  evenement = signal<EvenementDto | null>(null);
  editMode = signal(false);

  constructor(private route: ActivatedRoute, private router: Router, private service: EvenementService) {
  }

  private toLocalISOString(date: Date): string {
    const offset = date.getTimezoneOffset() * 60000;
    return new Date(date.getTime() - offset).toISOString().slice(0, -1);
  }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.load(id);
  }

  load(id: number): void {
    this.service.getById(id).subscribe((dto: EvenementDto) => this.evenement.set(dto));
  }

  enableEdit(): void {
    this.editMode.set(true);
  }

  cancelEdit(): void {
    this.editMode.set(false);
    const id = this.evenement()?.evenementId;
    if (id) this.load(id);
  }

  save(): void {
    const ev = this.evenement();
    if (!ev) return;
    this.service.update(ev.evenementId, ev).subscribe(() => {
      this.editMode.set(false);
      this.load(ev.evenementId);
    });
  }

  annuler(): void {
    const ev = this.evenement();
    if (!ev) return;
    const utilisateur = {utilisateurId: this.authService.utilisateur()?.utilisateurId ?? ''};
    this.service.annuler(ev.evenementId, utilisateur).subscribe(() => this.load(ev.evenementId));
  }

  delete(): void {
    const ev = this.evenement();
    if (!ev) return;
    const utilisateur = {utilisateurId: this.authService.utilisateur()?.utilisateurId ?? ''};
    this.service.delete(ev.evenementId, utilisateur).subscribe(() => this.router.navigate(['/evenements']));
  }

  naviguerVersCreationSession(): void {
    const ev = this.evenement();
    if (!ev) return;
    this.router.navigate(['/evenements', ev.evenementId, 'sessions', 'new']);
  }

  formatDate(date?: string): string {
    return date ? new Date(date).toLocaleDateString('fr-FR', {day: '2-digit', month: 'long', year: 'numeric'}) : '';
  }

  formatDuree(debut?: string, fin?: string): string {
    if (!debut || !fin) return '';
    const diffMs = new Date(fin).getTime() - new Date(debut).getTime();
    const diffH = Math.floor(diffMs / 3600000);
    const diffM = Math.floor((diffMs % 3600000) / 60000);
    if (diffH < 24) return `${diffH}h ${diffM}min`;
    const jours = Math.floor(diffH / 24);
    const reste = diffH % 24;
    return reste ? `${jours}j ${reste}h` : `${jours} jour${jours > 1 ? 's' : ''}`;
  }

  toDate(dateStr?: string): Date | null {
    return dateStr ? new Date(dateStr) : null;
  }

  toTimeString(dateStr?: string): string {
    if (!dateStr) return '';
    const d = new Date(dateStr);
    return `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`;
  }

  onDateDebutChange(date: Date | null): void {
    const ev = this.evenement();
    if (!ev || !date || !ev.datesDto) return;
    const existing = ev.datesDto.dateDebutEvenement ? new Date(ev.datesDto.dateDebutEvenement) : new Date();
    date.setHours(existing.getHours(), existing.getMinutes());
    this.evenement.set({...ev, datesDto: {...ev.datesDto, dateDebutEvenement: this.toLocalISOString(date)}});
  }

  onDateFinChange(date: Date | null): void {
    const ev = this.evenement();
    if (!ev || !date || !ev.datesDto) return;
    const existing = ev.datesDto.dateFinEvenement ? new Date(ev.datesDto.dateFinEvenement) : new Date();
    date.setHours(existing.getHours(), existing.getMinutes());
    this.evenement.set({...ev, datesDto: {...ev.datesDto, dateFinEvenement: this.toLocalISOString(date)}});
  }

  onHeureDebutChange(event: Event): void {
    const ev = this.evenement();
    if (!ev || !ev.datesDto) return;
    const [h, m] = (event.target as HTMLInputElement).value.split(':').map(Number);
    const d = ev.datesDto.dateDebutEvenement ? new Date(ev.datesDto.dateDebutEvenement) : new Date();
    d.setHours(h, m);
    this.evenement.set({...ev, datesDto: {...ev.datesDto, dateDebutEvenement: this.toLocalISOString(d)}});
  }

  onHeureFinChange(event: Event): void {
    const ev = this.evenement();
    if (!ev || !ev.datesDto) return;
    const [h, m] = (event.target as HTMLInputElement).value.split(':').map(Number);
    const d = ev.datesDto.dateFinEvenement ? new Date(ev.datesDto.dateFinEvenement) : new Date();
    d.setHours(h, m);
    this.evenement.set({...ev, datesDto: {...ev.datesDto, dateFinEvenement: this.toLocalISOString(d)}});
  }
}
