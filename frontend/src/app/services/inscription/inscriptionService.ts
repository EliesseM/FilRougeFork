import {Component} from '@angular/core';
import {environnement} from "../../environnement/environnement";
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-inscription-service',
  imports: [],
  templateUrl: './inscriptionService.html',
  styleUrl: './inscriptionService.scss',
  standalone: true
})
export class InscriptionService {

  private apiUrl = `${environnement.apiBaseUrl}/inscriptions`;

  constructor(private http: HttpClient) {
  }

}
