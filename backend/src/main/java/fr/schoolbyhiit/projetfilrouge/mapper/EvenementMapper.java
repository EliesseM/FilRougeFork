package fr.schoolbyhiit.projetfilrouge.mapper;

import fr.schoolbyhiit.projetfilrouge.config.DureeConfig;
import fr.schoolbyhiit.projetfilrouge.dto.evenement.EvenementDateDto;
import fr.schoolbyhiit.projetfilrouge.dto.evenement.EvenementDto;
import fr.schoolbyhiit.projetfilrouge.dto.evenement.EvenementInfoDto;
import fr.schoolbyhiit.projetfilrouge.dto.evenement.EvenementStatutDto;
import fr.schoolbyhiit.projetfilrouge.entity.Evenement;
import fr.schoolbyhiit.projetfilrouge.enums.StatutEvenement;
import fr.schoolbyhiit.projetfilrouge.exception.AppException;
import fr.schoolbyhiit.projetfilrouge.exception.CodeErreur;

public class EvenementMapper {

    public static Evenement toEntity(EvenementDto dto) {
        if (dto == null) {
            return null;
        }

        if (dto.getUtilisateur() == null) {
            throw new AppException(CodeErreur.EVENT_USER_REQUIRED, "L'utilisateur est obligatoire.");
        }
        if (dto.getInfoDto() == null) {
            throw new AppException(CodeErreur.EVENT_INFO_REQUIRED, "Les informations de l'événement sont obligatoires.");
        }

        Evenement evenement = new Evenement();
        evenement.setEvenementId(dto.getEvenementId());
        evenement.setUtilisateur(dto.getUtilisateur());

        evenement.setTitre(dto.getInfoDto().getTitre());
        evenement.setDescription(dto.getInfoDto().getDescription());
        evenement.setLocalisation(dto.getInfoDto().getLocalisation());


        evenement.setDateCreationEvenement(dto.getDatesDto().getDateCreationEvenement());
        evenement.setDateDebutEvenement(dto.getDatesDto().getDateDebutEvenement());
        evenement.setDateFinEvenement(dto.getDatesDto().getDateFinEvenement());


        if (dto.getStatutDto() != null && dto.getStatutDto().getStatutEvenement() != null) {
            evenement.setStatutEvenement(dto.getStatutDto().getStatutEvenement());
        } else {
            evenement.setStatutEvenement(StatutEvenement.En_attente);
        }

        return evenement;
    }

    public static EvenementDto toDto(Evenement evenement) {
        if (evenement == null) {
            return null;
        }

        EvenementDto dto = new EvenementDto();
        dto.setEvenementId(evenement.getEvenementId());
        dto.setUtilisateur(evenement.getUtilisateur());

        EvenementInfoDto infoDto = new EvenementInfoDto();
        infoDto.setTitre(evenement.getTitre());
        infoDto.setDescription(evenement.getDescription());
        infoDto.setLocalisation(evenement.getLocalisation());
        dto.setInfoDto(infoDto);

        EvenementDateDto dateDto = new EvenementDateDto();
        dateDto.setDateCreationEvenement(evenement.getDateCreationEvenement());

        // durée logique
        dateDto.setDureeEvenement(evenement.getDureeEvenement());

        //durée formatée
        dateDto.setDureeFormatee(
                DureeConfig.formaterDuree(evenement.getDureeEvenement())
        );

        dateDto.setDateDebutEvenement(evenement.getDateDebutEvenement());
        dateDto.setDateFinEvenement(evenement.getDateFinEvenement());


        dto.setDatesDto(dateDto);

        EvenementStatutDto statutDto = new EvenementStatutDto();
        statutDto.setStatutEvenement(evenement.getStatutEvenement() != null ?
                evenement.getStatutEvenement() : StatutEvenement.En_attente);
        dto.setStatutDto(statutDto);

        return dto;
    }
}

