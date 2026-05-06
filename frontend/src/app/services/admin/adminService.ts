import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environnement} from "../../environnement/environnement";

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

export interface AdminCounts {
  demandes: number;
  total: number;
}

export interface DemandeDeRoleDto {
  demandeDeRoleId: number;
  statutDemandeRole: string;
  utilisateur: {
    utilisateurId: string;
    nom: string;
    prenom: string;
    email: string;
  };
}

@Injectable({providedIn: 'root'})
export class AdminService {
  private base = `${environnement.apiBaseUrl}/demande-role`;

  constructor(private http: HttpClient) {
  }

  getCounts(): Observable<AdminCounts> {
    return this.http.get<AdminCounts>(`${this.base}/pending-counts`);
  }

  getPendingDemandes(page = 0, size = 10): Observable<PageResponse<DemandeDeRoleDto>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<DemandeDeRoleDto>>(`${this.base}/pending`, {params});
  }

  approuverDemande(id: number): Observable<DemandeDeRoleDto> {
    return this.http.patch<DemandeDeRoleDto>(`${this.base}/${id}/approuver`, {});
  }

  refuserDemande(id: number): Observable<DemandeDeRoleDto> {
    return this.http.patch<DemandeDeRoleDto>(`${this.base}/${id}/refuser`, {});
  }
}
