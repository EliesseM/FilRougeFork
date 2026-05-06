import {Component, inject, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule, NgForm} from '@angular/forms';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MAT_DATE_LOCALE, MatNativeDateModule} from '@angular/material/core';
import {SessionService} from '../../../services/session/sessionService';

@Component({
  selector: 'app-session-creation',
  standalone: true,
  imports: [
    CommonModule, FormsModule, RouterLink,
    MatInputModule, MatFormFieldModule,
    MatButtonModule, MatIconModule,
    MatDatepickerModule, MatNativeDateModule,
  ],
  providers: [{provide: MAT_DATE_LOCALE, useValue: 'fr-FR'}],
  templateUrl: './session-creation.html',
  styleUrls: ['./session-creation.scss'],
})
export class SessionCreation implements OnInit {
  private route = inject(ActivatedRoute);

  submitted = false;

  session = {
    infos: {titre: '', description: '', lieu: '', animateur: '', capaciteMax: ''},
    dates: {dateDebutSession: '', dateFinSession: ''},
    evenement: {evenementId: 0}   // ici le 0 est écrasé par l'id réel venant de l'URL
  };

  constructor(private router: Router, private service: SessionService) {
  }

  ngOnInit(): void {
    this.session.evenement.evenementId = Number(this.route.snapshot.paramMap.get('evenementId'));
  }

  submit(form: NgForm): void {
    this.submitted = true;
    if (form.invalid || !this.session.dates.dateDebutSession || !this.session.dates.dateFinSession) return;
    this.service.create(this.session).subscribe(() => {
      this.router.navigate(['/evenements', this.session.evenement.evenementId]);
    });
  }

  toDate(dateStr: string): Date | null {
    return dateStr ? new Date(dateStr) : null;
  }

  toTimeString(dateStr: string): string {
    if (!dateStr) return '';
    const d = new Date(dateStr);
    return `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`;
  }

  onDateDebutChange(date: Date | null): void {
    if (!date) return;
    const existing = this.session.dates.dateDebutSession ? new Date(this.session.dates.dateDebutSession) : new Date();
    date.setHours(existing.getHours(), existing.getMinutes());
    this.session.dates.dateDebutSession = date.toISOString();
  }

  onDateFinChange(date: Date | null): void {
    if (!date) return;
    const existing = this.session.dates.dateFinSession ? new Date(this.session.dates.dateFinSession) : new Date();
    date.setHours(existing.getHours(), existing.getMinutes());
    this.session.dates.dateFinSession = date.toISOString();
  }

  onHeureDebutChange(event: Event): void {
    const [h, m] = (event.target as HTMLInputElement).value.split(':').map(Number);
    const d = this.session.dates.dateDebutSession ? new Date(this.session.dates.dateDebutSession) : new Date();
    d.setHours(h, m);
    this.session.dates.dateDebutSession = d.toISOString();
  }

  onHeureFinChange(event: Event): void {
    const [h, m] = (event.target as HTMLInputElement).value.split(':').map(Number);
    const d = this.session.dates.dateFinSession ? new Date(this.session.dates.dateFinSession) : new Date();
    d.setHours(h, m);
    this.session.dates.dateFinSession = d.toISOString();
  }

  formatDuree(debut: string, fin: string): string {
    const diffMs = new Date(fin).getTime() - new Date(debut).getTime();
    if (diffMs <= 0) return '';
    const diffH = Math.floor(diffMs / 3600000);
    const diffM = Math.floor((diffMs % 3600000) / 60000);
    if (diffH < 24) return `${diffH}h ${diffM}min`;
    const jours = Math.floor(diffH / 24);
    const reste = diffH % 24;
    return reste ? `${jours}j ${reste}h` : `${jours} jour${jours > 1 ? 's' : ''}`;
  }
}
