package fr.schoolbyhiit.projetfilrouge.service;


import fr.schoolbyhiit.projetfilrouge.dto.session.SessionDateDto;
import fr.schoolbyhiit.projetfilrouge.dto.session.SessionDto;
import fr.schoolbyhiit.projetfilrouge.entity.Evenement;
import fr.schoolbyhiit.projetfilrouge.entity.Inscription;
import fr.schoolbyhiit.projetfilrouge.entity.Session;
import fr.schoolbyhiit.projetfilrouge.enums.StatutEvenement;
import fr.schoolbyhiit.projetfilrouge.enums.StatutInscription;
import fr.schoolbyhiit.projetfilrouge.enums.StatutSession;
import fr.schoolbyhiit.projetfilrouge.exception.AppException;
import fr.schoolbyhiit.projetfilrouge.exception.CodeErreur;
import fr.schoolbyhiit.projetfilrouge.mapper.SessionMapper;
import fr.schoolbyhiit.projetfilrouge.repository.EvenementRepository;
import fr.schoolbyhiit.projetfilrouge.repository.InscriptionRepository;
import fr.schoolbyhiit.projetfilrouge.repository.SessionRepository;
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

public class SessionService {

    private final SessionRepository sessionRepository;
    private final EvenementRepository evenementRepository;
    private final NotificationService notificationService;
    private final InscriptionRepository inscriptionRepository;


    private void verifierSessionMetier(SessionDto dto, SessionDateDto datesDto, Evenement evenement) {

        LocalDateTime now = LocalDateTime.now();

        if (datesDto == null
                || datesDto.getDateDebutSession() == null
                || datesDto.getDateFinSession() == null) {
            throw new AppException(CodeErreur.SESSION_DATES_NEEDED, "Dates obligatoires");
        }

        if (datesDto.getDateFinSession().isBefore(datesDto.getDateDebutSession())) {
            throw new AppException(CodeErreur.SESSION_DATES_INVALID,
                    "Date de fin avant date de début");
        }

        // session dans le futur
        if (datesDto.getDateDebutSession().isBefore(now)) {
            throw new AppException(CodeErreur.SUBMIT_START_INVALID,
                    "Impossible de créer une session dans le passé");
        }

        // capacité valide
        if (dto.getInfos().getCapaciteMax() == null || dto.getInfos().getCapaciteMax() <= 0) {
            throw new AppException(CodeErreur.SESSION_CAPACITY_INVALID,
                    "Capacité invalide");
        }

        long duree = calculDureeSession(datesDto);

        if (duree <= 0) {
            throw new AppException(CodeErreur.SESSION_DURATION_INVALID, "Durée invalide");
        }

        if (datesDto.getDateDebutSession().isBefore(now.plusHours(2))) {
            throw new AppException(CodeErreur.SESSION_DATES_INVALID,
                    "Une session doit être créée au moins 1h à l'avance");
        }

        // champs obligatoires
        if (dto.getInfos().getTitre() == null || dto.getInfos().getTitre().isBlank()) {
            throw new AppException(CodeErreur.SESSION_DATA_INCOMPLETE, "Titre obligatoire");
        }

        if (dto.getInfos().getLieu() == null || dto.getInfos().getLieu().isBlank()) {
            throw new AppException(CodeErreur.SESSION_DATA_INCOMPLETE, "Lieu obligatoire");
        }

        if (dto.getInfos().getAnimateur() == null || dto.getInfos().getAnimateur().isBlank()) {
            throw new AppException(CodeErreur.SESSION_DATA_INCOMPLETE, "Animateur obligatoire");
        }

        // événement annulé
        if (evenement.getStatutEvenement() == StatutEvenement.Annuler) {
            throw new AppException(CodeErreur.SESSION_EVENT_CANCELLED,
                    "Impossible de créer une session sur un événement annulé");
        }

        // session dans l’événement
        verifierDepassementSessionDansEvenement(
                datesDto.getDateDebutSession(),
                datesDto.getDateFinSession(),
                evenement
        );
    }

    private Session asignSession(SessionDto dto, SessionDateDto datesDto, Evenement evenement) {

        LocalDateTime now = LocalDateTime.now();

        Session session = SessionMapper.toEntity(dto);

        if (dto.getStatut() != null && dto.getStatut().getStatutSession() != null) {
            session.setStatutSession(dto.getStatut().getStatutSession());
        } else {
            session.setStatutSession(StatutSession.Confirmer);
        }

        if (datesDto.getDateCreationSession() != null) {
            session.setDateCreationSession(datesDto.getDateCreationSession());
        } else {
            session.setDateCreationSession(now);
        }

        session.setDureeSession(calculDureeSession(datesDto));

        session.setEvenement(evenement);

        return session;
    }

    private Session asignUpdateSession(SessionDto dto, SessionDateDto datesDto) {

        LocalDateTime now = LocalDateTime.now();

        Session existing = sessionRepository.findById(dto.getSessionId())
                .orElseThrow(() -> new AppException(CodeErreur.SESSION_NOT_FOUND, "Session inexistante"));

        if (existing.getDateDebutSession().isBefore(now)) {
            throw new AppException(CodeErreur.SESSION_UPDATE_START_INVALID,
                    "Impossible de modifier une session commencée");
        }

        existing.setDateDebutSession(datesDto.getDateDebutSession());
        existing.setDateFinSession(datesDto.getDateFinSession());

        existing.setTitre(dto.getInfos().getTitre());
        existing.setDescription(dto.getInfos().getDescription());
        existing.setAnimateur(dto.getInfos().getAnimateur());
        existing.setLieu(dto.getInfos().getLieu());
        existing.setCapaciteMax(dto.getInfos().getCapaciteMax());

        existing.setStatutSession(dto.getStatut().getStatutSession());

        if (!existing.getEvenement().getEvenementId()
                .equals(dto.getEvenement().getEvenementId())) {

            throw new AppException(CodeErreur.SESSION_UPDATE_EVENT_IMPOSSIBLE, "Impossible de modifier l'événement d'une session");
        }

        long nbInscrits = inscriptionRepository
                .countBySessionAndStatutInscription(existing, StatutInscription.Confirmer);

        if (dto.getInfos().getCapaciteMax() < nbInscrits) {
            throw new AppException(CodeErreur.SESSION_CAPACITY_INVALID,
                    "Capacité inférieure au nombre d'inscrits");
        }

        if (existing.getStatutSession() == StatutSession.Annuler) {
            throw new AppException(CodeErreur.SESSION_UPDATE_CANCELLED_INVALID, "Impossible de modifier une session annulée");
        }

        long duree = calculDureeSession(datesDto);
        existing.setDureeSession(duree);

        return existing;
    }

    private void verifierDepassementSessionDansEvenement(
            LocalDateTime dateDebutSession,
            LocalDateTime dateFinSession,
            Evenement evenement
    ) {

        if (dateDebutSession.isBefore(evenement.getDateDebutEvenement())
                || dateFinSession.isAfter(evenement.getDateFinEvenement())) {

            throw new AppException(CodeErreur.SESSION_DATES_OUT_OF_EVENT, "La session doit être comprise entre "
                    + evenement.getDateDebutEvenement()
                    + " et "
                    + evenement.getDateFinEvenement()
            );
        }
    }


    public Long calculDureeSession(SessionDateDto datesDto) {

        return ChronoUnit.HOURS.between(
                datesDto.getDateDebutSession(),
                datesDto.getDateFinSession()
        );
    }

    public void verifierChevauchementSession(
            Long sessionId,
            Long evenementId,
            String lieu,
            LocalDateTime dateDebut,
            LocalDateTime dateFin
    ) {

        Page<Session> sessions = sessionRepository
                .findByEvenement_EvenementId(evenementId, Pageable.unpaged());

        for (Session s : sessions) {

            if (s.getStatutSession() == StatutSession.Annuler) {
                continue;
            }

            // ignore elle-même (cas update)
            if (sessionId != null && sessionId.equals(s.getSessionId())) {
                continue;
            }

            boolean memeLieu = s.getLieu() != null &&
                    s.getLieu().equalsIgnoreCase(lieu);

            boolean chevauchement =
                    dateDebut.isBefore(s.getDateFinSession()) &&
                            dateFin.isAfter(s.getDateDebutSession());

            if (memeLieu && chevauchement) {
                throw new AppException(CodeErreur.SESSION_OVERLAP,
                        "Une session existe déjà au lieu '" +
                                lieu +
                                "' du " +
                                s.getDateDebutSession() +
                                " au " +
                                s.getDateFinSession()
                );
            }
        }
    }


    public SessionDto findById(Long id) {

        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new AppException(CodeErreur.SESSION_NOT_FOUND, "session introuvable"));
        return SessionMapper.toDto(session);
    }


    public SessionDto createSession(SessionDto dto) {

        Evenement evenement = evenementRepository.findById(dto.getEvenement().getEvenementId())
                .orElseThrow(() -> new AppException(CodeErreur.EVENT_NOT_FOUND, "Evenement introuvable"));

        SessionDateDto datesDto = dto.getDates();

        if (datesDto == null
                || datesDto.getDateDebutSession() == null
                || datesDto.getDateFinSession() == null) {

            throw new AppException(CodeErreur.SESSION_DATES_NEEDED, "Dates obligatoires");
        }

        verifierSessionMetier(dto, datesDto, evenement);


        verifierChevauchementSession(
                dto.getSessionId(),
                dto.getEvenement().getEvenementId(),
                dto.getInfos().getLieu(),
                datesDto.getDateDebutSession(),
                datesDto.getDateFinSession()
        );

        Session session = asignSession(dto, datesDto, evenement);
        sessionRepository.save(session);

        return SessionMapper.toDto(session);
    }

    public SessionDto updateSession(SessionDto dto) {

        SessionDateDto datesDto = dto.getDates();

        Evenement evenement = evenementRepository.findById(dto.getEvenement().getEvenementId())
                .orElseThrow(() -> new AppException(CodeErreur.EVENT_NOT_FOUND, "Evenement introuvable"));

        sessionRepository.findById(dto.getSessionId())
                .orElseThrow(() -> new AppException(CodeErreur.SESSION_NOT_FOUND, "Session inexistante"));

        Session existing = asignUpdateSession(dto, datesDto);


        verifierSessionMetier(dto, datesDto, evenement);

        verifierChevauchementSession(
                dto.getSessionId(),
                dto.getEvenement().getEvenementId(),
                dto.getInfos().getLieu(),
                datesDto.getDateDebutSession(),
                datesDto.getDateFinSession()
        );

        Session saved = sessionRepository.save(existing);
        notificationService.notifySessionUpdate(saved, "Une session a été modifié : "
                + saved.getTitre());

        return SessionMapper.toDto(saved);

    }

    public SessionDto annulerSession(Long id) {

        LocalDateTime now = LocalDateTime.now();

        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new AppException(CodeErreur.SESSION_NOT_FOUND,
                        "Session inexistante"));


        if (session.getStatutSession() == StatutSession.Annuler) {
            throw new AppException(CodeErreur.SESSION_ALREADY_CANCELLED,
                    "La session est déjà annulée");
        }

        if (session.getDateDebutSession().isBefore(now)) {
            throw new AppException(CodeErreur.SESSION_UPDATE_START_INVALID,
                    "Impossible d'annuler une session déjà commencée");
        }

        if (session.getDateDebutSession().minusHours(24).isBefore(now)) {
            throw new AppException(CodeErreur.SESSION_CANCEL_MIN_DELAY_NOT_RESPECTED,
                    "Annulation possible uniquement 24h avant le début");
        }

        session.setStatutSession(StatutSession.Annuler);

        Session saved = sessionRepository.save(session);

        List<Inscription> inscriptions = inscriptionRepository
                .findBySession_SessionId(session.getSessionId());

        for (Inscription inscription : inscriptions) {
            if (inscription.getStatutInscription() != StatutInscription.Annuler) {
                inscription.setStatutInscription(StatutInscription.Annuler);
            }
        }

        inscriptionRepository.saveAll(inscriptions);

        notificationService.notifySessionUpdate(
                saved,
                "Session annulée : " + saved.getTitre()
        );

        return SessionMapper.toDto(saved);
    }

    public void deleteSession(Long id) {

        LocalDateTime now = LocalDateTime.now();

        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new AppException(CodeErreur.SESSION_NOT_FOUND, "Session inexistante"));

        if (session.getDateDebutSession().isBefore(now)) {
            throw new AppException(CodeErreur.SESSION_UPDATE_START_INVALID,
                    "Impossible de supprimer une session commencée");
        }

        if (inscriptionRepository.existsBySession(session)) {
            throw new AppException(CodeErreur.SESSION_HAS_REGISTRATIONS,
                    "Impossible de supprimer une session avec des inscrits");
        }

        sessionRepository.delete(session);
    }


    public Page<SessionDto> findAllSession(
            Pageable page,
            Long dureeSession,
            LocalDateTime dateCreationSession,
            LocalDateTime dateDebutSession,
            LocalDateTime dateFinSession,
            StatutSession statutSession,
            String titre,
            String lieu,
            String animateur,
            Integer capaciteMax,
            Long evenementId) {

        List<Specification<Session>> specs = new ArrayList<>();

        if (titre != null) {
            specs.add((root, query, cb) ->
                    cb.like(cb.lower(root.get("titre")), "%" + titre.toLowerCase() + "%"));
        }

        if (lieu != null) {
            specs.add((root, query, cb) ->
                    cb.like(cb.lower(root.get("lieu")), "%" + lieu.toLowerCase() + "%"));
        }

        if (animateur != null) {
            specs.add((root, query, cb) ->
                    cb.like(cb.lower(root.get("animateur")), "%" + animateur.toLowerCase() + "%"));
        }

        if (statutSession != null) {
            specs.add((root, query, cb) ->
                    cb.equal(root.get("statutSession"), statutSession));
        }

        if (dureeSession != null) {
            specs.add((root, query, cb) ->
                    cb.equal(root.get("dureeSession"), dureeSession));
        }

        if (dateCreationSession != null) {
            specs.add((root, query, cb) ->
                    cb.equal(root.get("dateCreationSession"), dateCreationSession));
        }

        if (dateDebutSession != null) {
            specs.add((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("dateDebutSession"), dateDebutSession));
        }

        if (dateFinSession != null) {
            specs.add((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("dateFinSession"), dateFinSession));
        }

        if (evenementId != null) {
            specs.add((root, query, cb) ->
                    cb.equal(root.get("evenement").get("evenementId"), evenementId));
        }

        if (capaciteMax != null && capaciteMax > 0) {
            specs.add((root, query, cb) ->
                    cb.equal(root.get("capaciteMax"), capaciteMax));
        }

        Specification<Session> spec = Specification.allOf(specs);

        return sessionRepository
                .findAll(spec, page)
                .map(SessionMapper::toDto);
    }

}