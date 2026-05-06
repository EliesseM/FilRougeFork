package fr.schoolbyhiit.projetfilrouge.mapper;

import fr.schoolbyhiit.projetfilrouge.dto.inscription.InscriptionDto;
import fr.schoolbyhiit.projetfilrouge.entity.Inscription;
import fr.schoolbyhiit.projetfilrouge.enums.StatutInscription;

public class InscriptionMapper {


    // --- DTO -> Entity ---
    public static Inscription toEntity(InscriptionDto dto) {

        Inscription inscription = new Inscription();

        if (dto.getStatutInscription() != null) {
            inscription.setStatutInscription(dto.getStatutInscription());
        } else {
            inscription.setStatutInscription(StatutInscription.Confirmer);
        }

        return inscription;
    }

    // --- Entity -> DTO ---
    public static InscriptionDto toDto(Inscription inscription) {

        InscriptionDto inscriptionDto = new InscriptionDto();

        inscriptionDto.setInscriptionId(inscription.getInscriptionId());

        if (inscription.getUtilisateur() != null) {
            inscriptionDto.setUtilisateurId(inscription.getUtilisateur().getUtilisateurId());
            inscriptionDto.setUtilisateurNom(inscription.getUtilisateur().getNom());
        }

        if (inscription.getSession() != null) {
            inscriptionDto.setSessionId(inscription.getSession().getSessionId());
            inscriptionDto.setSessionTitre(inscription.getSession().getTitre());
        }

        inscriptionDto.setStatutInscription(inscription.getStatutInscription());

        return inscriptionDto;
    }
}