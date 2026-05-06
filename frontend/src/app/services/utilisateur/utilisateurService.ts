import {Component} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environnement} from "../../environnement/environnement";

@Component({
  selector: 'app-inscription-service',
  imports: [],
  templateUrl: './utilisateurService.html',
  styleUrl: './utilisateurService.scss',
  standalone: true
})
export class UtilisateurService {

  private apiUrl = `${environnement.apiBaseUrl}/utilisateurs`;

  constructor(private http: HttpClient) {
  }

}
