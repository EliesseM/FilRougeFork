import {Component} from '@angular/core';
import {environnement} from "../../environnement/environnement";
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-demande-de-role-service',
  imports: [],
  templateUrl: './demandeDeRoleService.html',
  styleUrl: './demandeDeRoleService.scss',
  standalone: true
})
export class DemandeDeRoleService {

  private apiUrl = `${environnement.apiBaseUrl}/demande-role`;

  constructor(private http: HttpClient) {
  }

}
