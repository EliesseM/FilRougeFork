import {StatutInscription} from "../component/inscription/enum/statutInscription.enum";

export interface Inscription {
  inscriptionId: number,
  statutInscription: StatutInscription,
  utilisateurId: number,
  sessionId: number
}
