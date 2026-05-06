package fr.schoolbyhiit.projetfilrouge.mapper;

import fr.schoolbyhiit.projetfilrouge.dto.utilisateur.UtilisateurDto;
import fr.schoolbyhiit.projetfilrouge.entity.Utilisateur;

public class UtilisateurMapper {

    public static Utilisateur toEntity(UtilisateurDto utilisateurDto) {
        Utilisateur utilisateur = new Utilisateur();

        utilisateur.setUtilisateurId(utilisateurDto.getUtilisateurId());
        utilisateur.setNom(utilisateurDto.getNom());
        utilisateur.setPrenom(utilisateurDto.getPrenom());

        utilisateur.setEmail(utilisateurDto.getEmail());
        utilisateur.setRole(utilisateurDto.getRole());

        utilisateur.setDateCreationUtilisateurs(utilisateurDto.getDateCreationUtilisateurs());
        utilisateur.setPhotoProfil(utilisateurDto.getPhotoProfil());

        return utilisateur;
    }

    public static UtilisateurDto toDto(Utilisateur utilisateur) {
        UtilisateurDto utilisateurDto = new UtilisateurDto();

        utilisateurDto.setUtilisateurId(utilisateur.getUtilisateurId());
        utilisateurDto.setNom(utilisateur.getNom());
        utilisateurDto.setPrenom(utilisateur.getPrenom());

        utilisateurDto.setEmail(utilisateur.getEmail());
        utilisateurDto.setRole(utilisateur.getRole());

        utilisateurDto.setDateCreationUtilisateurs((utilisateur.getDateCreationUtilisateurs()));
        utilisateurDto.setPhotoProfil(utilisateur.getPhotoProfil());

        return utilisateurDto;
    }
}
