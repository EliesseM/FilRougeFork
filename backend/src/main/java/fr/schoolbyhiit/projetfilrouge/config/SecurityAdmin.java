package fr.schoolbyhiit.projetfilrouge.config;

import fr.schoolbyhiit.projetfilrouge.entity.Utilisateur;
import fr.schoolbyhiit.projetfilrouge.exception.AppException;
import fr.schoolbyhiit.projetfilrouge.exception.CodeErreur;

public class SecurityAdmin {

    public static boolean estAdmin(Utilisateur utilisateur) {
        return utilisateur != null && utilisateur.getRole() != null && utilisateur.getRole().contains("ADMIN");
    }


    public static void verifierDroitsModification(Utilisateur utilisateur, Utilisateur createurEvenement) {
        if (!estAdmin(utilisateur) && !utilisateur.getUtilisateurId().equals(createurEvenement.getUtilisateurId())) {
            throw new AppException(CodeErreur.NOT_ADMIN_UNAUTHORIZED, "Seuls l'organisateur ou un administrateur peuvent effectuer cette action.");
        }
    }

}
