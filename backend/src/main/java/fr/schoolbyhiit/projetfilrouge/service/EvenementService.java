package fr.schoolbyhiit.projetfilrouge.service;

import fr.schoolbyhiit.projetfilrouge.config.SecurityAdmin;
import fr.schoolbyhiit.projetfilrouge.dto.evenement.EvenementDateDto;
import fr.schoolbyhiit.projetfilrouge.dto.evenement.EvenementDto;
import fr.schoolbyhiit.projetfilrouge.dto.evenement.EvenementStatutDto;
import fr.schoolbyhiit.projetfilrouge.entity.Evenement;
import fr.schoolbyhiit.projetfilrouge.entity.Utilisateur;
import fr.schoolbyhiit.projetfilrouge.enums.StatutEvenement;
import fr.schoolbyhiit.projetfilrouge.exception.AppException;
import fr.schoolbyhiit.projetfilrouge.exception.CodeErreur;
import fr.schoolbyhiit.projetfilrouge.mapper.EvenementMapper;
import fr.schoolbyhiit.projetfilrouge.repository.EvenementRepository;
import fr.schoolbyhiit.projetfilrouge.repository.UtilisateurRepository;
import fr.schoolbyhiit.projetfilrouge.service.dates.ValidationDatesEvenement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvenementService {

    private final EvenementRepository evenementRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ValidationDatesEvenement validationDatesEvenement;
    private final NotificationService notificationService;

    public Long calculDureeEvenement(EvenementDateDto datesDto) {
        return ChronoUnit.HOURS.between(
                datesDto.getDateDebutEvenement(),
                datesDto.getDateFinEvenement()
        );
    }

    private void validerDateDebutEvenement(EvenementDateDto datesDto) {
        if (datesDto.getDateDebutEvenement().isBefore(LocalDateTime.now())) {
            throw new AppException(
                    CodeErreur.EVENT_DATE_ANTERIEURE_CREATION,
                    "La date de début de l'événement ne peut pas être antérieure à la date de création."
            );
        }
    }

    private void validerMiseAJourEvenement(Evenement existing) {
        if (existing.getDateDebutEvenement().isBefore(LocalDateTime.now())) {
            throw new AppException(
                    CodeErreur.EVENT_ALREADY_STARTED,
                    "L'événement a déjà commencé et ne peut plus être modifié."
            );
        }
        if (existing.getStatutEvenement() == StatutEvenement.Annuler) {
            throw new AppException(
                    CodeErreur.EVENT_CANCELLED,
                    "Un événement annulé ne peut pas être modifié."
            );
        }
    }

    public EvenementDto createEvenement(EvenementDto evenementDto) {
        String utilisateurId = evenementDto.getUtilisateur().getUtilisateurId();
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new AppException(CodeErreur.USER_NOT_FOUND, "Utilisateur non trouvé avec l'ID : " + utilisateurId));

        evenementDto.setUtilisateur(utilisateur);

        EvenementStatutDto statutDto = new EvenementStatutDto();
        statutDto.setStatutEvenement(StatutEvenement.En_attente);
        evenementDto.setStatutDto(statutDto);

        // Vérification de la date de début
        validerDateDebutEvenement(evenementDto.getDatesDto());

        if (validationDatesEvenement.chevauchementEvenement(evenementDto, null)) {
            throw new AppException(CodeErreur.EVENT_OVERLAP,
                    "Un événement existe déjà avec le même titre et la même localisation pendant cette période.");
        }

        EvenementDateDto datesDto = validationDatesEvenement.validateDates(evenementDto.getDatesDto(), null);

        evenementDto.setDatesDto(datesDto);

        Evenement evenement = EvenementMapper.toEntity(evenementDto);

        evenement.setDureeEvenement(calculDureeEvenement(datesDto));

        Evenement savedEvenement = evenementRepository.save(evenement);

        return EvenementMapper.toDto(savedEvenement);
    }

    public EvenementDto updateEvenement(Long id, EvenementDto dto) {
        Evenement existing = evenementRepository.findById(id)
                .orElseThrow(() -> new AppException(CodeErreur.EVENT_NOT_FOUND, "Événement non trouvé avec l'ID : " + id));

        // Vérification des règles métier
        validerMiseAJourEvenement(existing);

        if (validationDatesEvenement.chevauchementEvenement(dto, id)) {
            throw new AppException(CodeErreur.EVENT_OVERLAP,
                    "Un événement existe déjà avec le même titre et la même localisation pendant cette période.");
        }

        EvenementDateDto datesDto = validationDatesEvenement.validateDates(dto.getDatesDto(), existing);

        if (dto.getUtilisateur() != null) {
            existing.setUtilisateur(dto.getUtilisateur());
        }
        if (dto.getInfoDto() != null) {
            existing.setTitre(dto.getInfoDto().getTitre());
            existing.setDescription(dto.getInfoDto().getDescription());
            existing.setLocalisation(dto.getInfoDto().getLocalisation());
        }
        if (dto.getStatutDto() != null && dto.getStatutDto().getStatutEvenement() != null) {
            existing.setStatutEvenement(dto.getStatutDto().getStatutEvenement());
        }

        existing.setDateDebutEvenement(datesDto.getDateDebutEvenement());
        existing.setDateFinEvenement(datesDto.getDateFinEvenement());
        existing.setDureeEvenement(calculDureeEvenement(datesDto));

        Evenement saved = evenementRepository.save(existing);
        notificationService.notifyEvenementUpdate(saved, "Un événement a été modifié : " + saved.getTitre());

        return EvenementMapper.toDto(saved);
    }

    public EvenementDto annulerEvenement(Long id, Utilisateur utilisateur) {
        Evenement evenement = evenementRepository.findById(id)
                .orElseThrow(() -> new AppException(CodeErreur.EVENT_NOT_FOUND, "Événement non trouvé avec l'ID : " + id));

        SecurityAdmin.verifierDroitsModification(utilisateur, evenement.getUtilisateur());

        evenement.setStatutEvenement(StatutEvenement.Annuler);
        Evenement saved = evenementRepository.save(evenement);

        notificationService.notifyEvenementUpdate(
                saved,
                "L'événement a été annulé : " + saved.getTitre());

        return EvenementMapper.toDto(saved);
    }

    public void deleteEvenement(Long id, Utilisateur utilisateur) {
        if (!SecurityAdmin.estAdmin(utilisateur)) {
            throw new AppException(CodeErreur.NOT_ADMIN_UNAUTHORIZED, "Seuls les administrateurs peuvent supprimer un événement.");
        }

        if (!evenementRepository.existsById(id)) {
            throw new AppException(CodeErreur.EVENT_NOT_FOUND, "Événement non trouvé avec l'ID : " + id);
        }

        evenementRepository.deleteById(id);
    }

    public EvenementDto getEvenementById(Long id) {
        Evenement evenement = evenementRepository.findById(id)
                .orElseThrow(() -> new AppException(CodeErreur.EVENT_NOT_FOUND, "Événement non trouvé avec l'ID : " + id));
        return EvenementMapper.toDto(evenement);
    }

    public Page<EvenementDto> findAllEvenement(
            Pageable pageable, Long dureeEvenement,
            StatutEvenement statutEvenement, String titre, String localisation,
            LocalDateTime dateCreationEvenement, LocalDateTime dateDebutEvenement,
            LocalDateTime dateFinEvenement, String utilisateurId) {

        List<Specification<Evenement>> specs = new ArrayList<>();

        if (utilisateurId != null) {
            specs.add((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("utilisateur"), utilisateurId));
        }

        if (dureeEvenement != null) {
            specs.add((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("dureeEvenement"), dureeEvenement));
        }

        if (statutEvenement != null) {
            specs.add((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("statutEvenement"), statutEvenement));
        }

        if (titre != null) {
            specs.add((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("titre"), titre));
        }

        if (localisation != null) {
            specs.add((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("localisation"), localisation));
        }

        if (dateCreationEvenement != null) {
            specs.add((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("dateCreationEvenement"), dateCreationEvenement.toString()));
        }

        if (dateDebutEvenement != null) {
            specs.add((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("dateDebutEvenement"), dateDebutEvenement.toString()));
        }

        if (dateFinEvenement != null) {
            specs.add((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("dateFinEvenement"), dateFinEvenement.toString()));
        }

        Specification<Evenement> spec = Specification.allOf(specs);

        return evenementRepository
                .findAll(spec, pageable)
                .map(EvenementMapper::toDto);
    }
}