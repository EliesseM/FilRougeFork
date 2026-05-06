import {StatutSession} from "../component/session/enum/statutSession.enum";

export interface SessionDto {
  sessionId: number;

  infos: {
    titre: string;
    description: string;
    lieu: string;
    animateur: string;
    capaciteMax: number;
  };

  statut: {
    statutSession: StatutSession;
  };

  dates: {
    dateCreationSession: string;
    dateDebutSession: string;
    dateFinSession: string;
    dureeSession: number;
    dureeFormatee: string;
  };

  evenement: {
    evenementId: number;
  };
}
