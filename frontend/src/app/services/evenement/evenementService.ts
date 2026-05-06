import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {EvenementDto, PageEvenement} from "../../models/evenement";
import {environnement} from "../../environnement/environnement";

interface UtilisateurRef {
  utilisateurId: string;
}

@Injectable({
  providedIn: 'root'
})
export class EvenementService {

  private apiUrl = `${environnement.apiBaseUrl}/evenements`;

  constructor(private http: HttpClient) {
  }

  getAll(): Observable<PageEvenement> {
    return this.http.get<PageEvenement>(this.apiUrl);
  }

  getById(id: number): Observable<EvenementDto> {
    return this.http.get<EvenementDto>(`${this.apiUrl}/${id}`);
  }

  create(ev: {
    infoDto: { titre: string; description: string; localisation: string };
    datesDto: { dateDebutEvenement: string; dateFinEvenement: string };
    utilisateur: { utilisateurId: string }
  }): Observable<EvenementDto> {
    return this.http.post<EvenementDto>(this.apiUrl, ev);
  }

  update(id: number, ev: EvenementDto): Observable<EvenementDto> {
    return this.http.put<EvenementDto>(`${this.apiUrl}/${id}`, ev);
  }

  delete(id: number, utilisateur: UtilisateurRef): Observable<void> {
    return this.http.request<void>('delete', `${this.apiUrl}/${id}`, {
      body: utilisateur
    });
  }

  annuler(id: number, utilisateur: UtilisateurRef): Observable<EvenementDto> {
    return this.http.patch<EvenementDto>(`${this.apiUrl}/${id}/annuler`, utilisateur);
  }
}
