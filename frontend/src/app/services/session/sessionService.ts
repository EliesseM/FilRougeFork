import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

import {SessionDto} from '../../models/session';
import {environnement} from "../../environnement/environnement";

@Injectable({
  providedIn: 'root',
})
export class SessionService {

  private apiUrl = `${environnement.apiBaseUrl}/sessions`;

  constructor(private http: HttpClient) {
  }

  getAll(): Observable<{ content: SessionDto[] }> {
    return this.http.get<{ content: SessionDto[] }>(this.apiUrl);
  }

  getById(id: number): Observable<SessionDto> {
    return this.http.get<SessionDto>(`${this.apiUrl}/${id}`);
  }

  create(session: {
    infos: { titre: string; description: string; lieu: string; animateur: string; capaciteMax: string };
    dates: { dateDebutSession: string; dateFinSession: string };
    evenement: { evenementId: number }
  }): Observable<SessionDto> {
    return this.http.post<SessionDto>(this.apiUrl, session);
  }

  update(id: number, session: SessionDto): Observable<SessionDto> {
    return this.http.put<SessionDto>(`${this.apiUrl}/${id}`, session);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  annuler(id: number): Observable<SessionDto> {
    return this.http.patch<SessionDto>(`${this.apiUrl}/${id}/annuler`, {});
  }
}
