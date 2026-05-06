package fr.schoolbyhiit.projetfilrouge.service.dates;

import fr.schoolbyhiit.projetfilrouge.dto.evenement.EvenementDateDto;
import fr.schoolbyhiit.projetfilrouge.dto.evenement.EvenementDto;
import fr.schoolbyhiit.projetfilrouge.entity.Evenement;
import fr.schoolbyhiit.projetfilrouge.enums.StatutEvenement;
import fr.schoolbyhiit.projetfilrouge.exception.AppException;
import fr.schoolbyhiit.projetfilrouge.exception.CodeErreur;
import fr.schoolbyhiit.projetfilrouge.repository.EvenementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ValidationDatesEvenement {

    private final EvenementRepository evenementRepository;

    public EvenementDateDto validateDates(EvenementDateDto datesDto, Evenement existing) {
        if (datesDto == null) {
            throw new AppException(CodeErreur.EVENT_DATA_INCOMPLETE, "Les dates de l'événement sont obligatoires.");
        }

        if (datesDto.getDateCreationEvenement() == null && existing != null) {
            datesDto.setDateCreationEvenement(existing.getDateCreationEvenement());
        } else if (datesDto.getDateCreationEvenement() == null) {
            datesDto.setDateCreationEvenement(LocalDateTime.now());
        }

        if (datesDto.getDateDebutEvenement() == null || datesDto.getDateFinEvenement() == null) {
            throw new AppException(CodeErreur.EVENT_DATES_INVALID, "Les dates de début et de fin de l'événement sont obligatoires.");
        }

        if (datesDto.getDateDebutEvenement().isAfter(datesDto.getDateFinEvenement())) {
            throw new AppException(CodeErreur.EVENT_DATES_INVALID, "La date de début doit être antérieure à la date de fin.");
        }

        return datesDto;
    }

    public boolean chevauchementEvenement(EvenementDto newEvenementDto, Long evenementId) {
        LocalDateTime newStart = newEvenementDto.getDatesDto().getDateDebutEvenement();
        LocalDateTime newEnd = newEvenementDto.getDatesDto().getDateFinEvenement();

        String localisation = newEvenementDto.getInfoDto().getLocalisation();
        String titre = newEvenementDto.getInfoDto().getTitre();

        String utilisateurId = newEvenementDto.getUtilisateur().getUtilisateurId();

        List<StatutEvenement> statuts = Arrays.asList(StatutEvenement.Confirmer, StatutEvenement.En_attente);
        List<Evenement> existingEvents = evenementRepository
                .findByLocalisationAndTitreAndUtilisateur_UtilisateurIdAndStatutEvenementIn(
                        localisation,
                        titre,
                        utilisateurId,
                        statuts
                );

        for (Evenement existing : existingEvents) {
            if (existing.getEvenementId().equals(evenementId)) {
                continue;
            }

            LocalDateTime existingStart = existing.getDateDebutEvenement();
            LocalDateTime existingEnd = existing.getDateFinEvenement();

            if ((newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart)) ||
                    (newStart.isEqual(existingStart) || newEnd.isEqual(existingEnd)) ||
                    (newStart.isBefore(existingStart) && newEnd.isAfter(existingEnd))) {
                return true;
            }
        }

        return false;
    }
}
