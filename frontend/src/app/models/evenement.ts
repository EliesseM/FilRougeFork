import {StatutEvenement} from "../component/evenement/enum/statutEvenement.enum";

export interface EvenementDto {
  evenementId: number;

  infoDto: {
    titre: string;
    description: string;
    localisation: string;
  };

  statutDto: {
    statutEvenement: StatutEvenement;
  };

  datesDto: {
    dateCreationEvenement: string;
    dateDebutEvenement: string;
    dateFinEvenement: string;
    dureeEvenement: number;
    dureeFormatee?: string;
  };

  utilisateur: {
    utilisateurId: string;
  };
}

export interface PageEvenement {
  content: EvenementDto[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;


}
