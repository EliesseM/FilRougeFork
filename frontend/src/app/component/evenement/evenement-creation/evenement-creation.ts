import {Component, inject} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule, NgForm} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatNativeDateModule} from '@angular/material/core';
import {EvenementService} from '../../../services/evenement/evenementService';
import {AuthGoogleService} from '../../../services/auth/auth-google.service';

@Component({
  selector: 'app-evenement-creation',
  standalone: true,
  imports: [
    CommonModule, FormsModule, RouterLink,
    MatInputModule, MatFormFieldModule,
    MatButtonModule, MatIconModule,
    MatDatepickerModule, MatNativeDateModule,
  ],
  templateUrl: './evenement-creation.html',
  styleUrls: ['./evenement-creation.scss'],
})
export class EvenementCreation {
  private authService = inject(AuthGoogleService);

  submitted = false;

  evenement = {
    infoDto: {titre: '', description: '', localisation: ''},
    datesDto: {dateDebutEvenement: '', dateFinEvenement: ''},
    utilisateur: {utilisateurId: ''}
  };

  constructor(private router: Router, private service: EvenementService) {
    const utilisateur = this.authService.utilisateur();
    if (utilisateur) {
      this.evenement.utilisateur.utilisateurId = utilisateur.utilisateurId;
    }
  }

  private toLocalISOString(date: Date): string {
    const offset = date.getTimezoneOffset() * 60000;
    return new Date(date.getTime() - offset).toISOString().slice(0, -1);
  }

  submit(form: NgForm): void {
    this.submitted = true;
    if (form.invalid) return;
    if (!this.evenement.datesDto.dateDebutEvenement || !this.evenement.datesDto.dateFinEvenement) return;
    this.service.create(this.evenement).subscribe(() => this.router.navigate(['/evenements']));
  }

  formatDuree(debut?: string, fin?: string): string {
    if (!debut || !fin) return '';
    const diffMs = new Date(fin).getTime() - new Date(debut).getTime();
    if (diffMs <= 0) return '';
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
    if (!date) return;
    const existing = this.evenement.datesDto.dateDebutEvenement
      ? new Date(this.evenement.datesDto.dateDebutEvenement) : new Date();
    date.setHours(existing.getHours(), existing.getMinutes());
    this.evenement.datesDto.dateDebutEvenement = this.toLocalISOString(date);
  }

  onDateFinChange(date: Date | null): void {
    if (!date) return;
    const existing = this.evenement.datesDto.dateFinEvenement
      ? new Date(this.evenement.datesDto.dateFinEvenement) : new Date();
    date.setHours(existing.getHours(), existing.getMinutes());
    this.evenement.datesDto.dateFinEvenement = this.toLocalISOString(date);
  }

  onHeureDebutChange(event: Event): void {
    const [h, m] = (event.target as HTMLInputElement).value.split(':').map(Number);
    const d = this.evenement.datesDto.dateDebutEvenement
      ? new Date(this.evenement.datesDto.dateDebutEvenement) : new Date();
    d.setHours(h, m);
    this.evenement.datesDto.dateDebutEvenement = this.toLocalISOString(d);
  }

  onHeureFinChange(event: Event): void {
    const [h, m] = (event.target as HTMLInputElement).value.split(':').map(Number);
    const d = this.evenement.datesDto.dateFinEvenement
      ? new Date(this.evenement.datesDto.dateFinEvenement) : new Date();
    d.setHours(h, m);
    this.evenement.datesDto.dateFinEvenement = this.toLocalISOString(d);
  }
}
