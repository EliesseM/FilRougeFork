package fr.schoolbyhiit.projetfilrouge.service;

import fr.schoolbyhiit.projetfilrouge.dto.inscription.InscriptionDto;
import fr.schoolbyhiit.projetfilrouge.entity.Inscription;
import fr.schoolbyhiit.projetfilrouge.entity.Session;
import fr.schoolbyhiit.projetfilrouge.entity.Utilisateur;
import fr.schoolbyhiit.projetfilrouge.enums.StatutEvenement;
import fr.schoolbyhiit.projetfilrouge.enums.StatutInscription;
import fr.schoolbyhiit.projetfilrouge.enums.StatutSession;
import fr.schoolbyhiit.projetfilrouge.exception.AppException;
import fr.schoolbyhiit.projetfilrouge.exception.CodeErreur;
import fr.schoolbyhiit.projetfilrouge.mapper.InscriptionMapper;
import fr.schoolbyhiit.projetfilrouge.repository.InscriptionRepository;
import fr.schoolbyhiit.projetfilrouge.repository.SessionRepository;
import fr.schoolbyhiit.projetfilrouge.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InscriptionService {

    private final InscriptionRepository inscriptionRepository;
    private final SessionRepository sessionRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final NotificationService notificationService;

    LocalDateTime now = LocalDateTime.now();

    public void verifierChevauchementInscription(String utilisateurId, Session session) {
        Page<Inscription> inscriptions =
                inscriptionRepository
                        .findByUtilisateur_UtilisateurId(
                                utilisateurId,
                                Pageable.unpaged()
                        );

        verifierSessionValide(session);

        for (Inscription i : inscriptions) {
            if (i.getStatutInscription() == StatutInscription.Annuler) continue;

            Session s = i.getSession();

            if (s.getSessionId().equals(session.getSessionId())) {
                throw new AppException(CodeErreur.SUBMIT_ALREADY_EXIST,
                        "Déjà inscrit à cette session");
            }

            // Chevauchement horaire
            if (s.getDateDebutSession().isBefore(session.getDateFinSession())
                    && s.getDateFinSession().isAfter(session.getDateDebutSession())) {
                throw new AppException(CodeErreur.SUBMIT_HOUR_INVALID,
                        "Impossible de s'inscrire à une session au même horaire");
            }
        }
    }

    private void verifierSessionValide(Session session) {
        if (session.getDateDebutSession().isBefore(now)) {
            throw new AppException(CodeErreur.SUBMIT_START_INVALID,
                    "La session a déjà commencé");
        }

        if (session.getDateFinSession().isBefore(now)) {
            throw new AppException(CodeErreur.SUBMIT_END_INVALID,
                    "La session est terminée");
        }

        if (session.getEvenement().getStatutEvenement() == StatutEvenement.Annuler) {
            throw new AppException(CodeErreur.SUBMIT_SESSION_EVENT_CANCEL_IMPOSSIBLE,
                    "Impossible de s'inscrire à une session d'un événement annulé");
        }

        if (session.getDateDebutSession().minusHours(1).isBefore(now)) {
            throw new AppException(CodeErreur.SUBMIT_MIN_DELAY_NOT_RESPECTED,
                    "Inscription fermée 1h avant la session");
        }

        if (session.getStatutSession() == StatutSession.Annuler) {
            throw new AppException(CodeErreur.SESSION_NOT_FOUND,
                    "Session annulée");
        }
    }

    public StatutInscription determinerStatut(Session session) {
        long nbConfirmes = inscriptionRepository
                .countBySession_SessionIdAndStatutInscription(
                        session.getSessionId(),
                        StatutInscription.Confirmer
                );

        return (nbConfirmes >= session.getCapaciteMax())
                ? StatutInscription.En_attente
                : StatutInscription.Confirmer;
    }

    private void gererListeAttente(Session session) {
        long nbConfirmes = inscriptionRepository
                .countBySession_SessionIdAndStatutInscription(
                        session.getSessionId(),
                        StatutInscription.Confirmer
                );

        if (nbConfirmes >= session.getCapaciteMax()) return;

        Optional<Inscription> attente = inscriptionRepository
                .findFirstBySession_SessionIdAndStatutInscriptionOrderByInscriptionIdAsc(
                        session.getSessionId(),
                        StatutInscription.En_attente
                );

        attente.ifPresent(inscriptionAttente -> {
            inscriptionAttente.setStatutInscription(StatutInscription.Confirmer);
            inscriptionRepository.save(inscriptionAttente);

            notificationService.notifyInscriptionConfirme(inscriptionAttente);
        });
    }

    private void verifierInscriptionAnnuler(Inscription inscription) {
        if (inscription.getStatutInscription() == StatutInscription.Annuler) {
            throw new AppException(CodeErreur.SUBMIT_UPDATE_CANCEL_IMPOSSIBLE,
                    "Impossible de modifier une inscription annulée");
        }
    }

    public InscriptionDto findInscriptionById(Long id) {
        Inscription inscription = inscriptionRepository.findById(id).orElseThrow(() ->
                new AppException(CodeErreur.SUBMIT_NOT_FOUND,
                        "Inscription non trouvée"));
        return InscriptionMapper.toDto(inscription);
    }

    public InscriptionDto createInscription(InscriptionDto inscriptionDto) {
        Session session = sessionRepository.findById(inscriptionDto.getSessionId())
                .orElseThrow(() -> new AppException(CodeErreur.SESSION_NOT_FOUND,
                        "Session non trouvée"));

        Utilisateur utilisateur = utilisateurRepository.findById(inscriptionDto.getUtilisateurId())
                .orElseThrow(() -> new AppException(CodeErreur.USER_NOT_FOUND,
                        "Utilisateur non trouvé"));

        verifierChevauchementInscription(utilisateur.getUtilisateurId(), session);

        Inscription inscription = InscriptionMapper.toEntity(inscriptionDto);

        inscription.setStatutInscription(determinerStatut(session));
        inscription.setSession(session);
        inscription.setUtilisateur(utilisateur);

        Inscription saved = inscriptionRepository.save(inscription);

        // après save
        gererListeAttente(session);

        if (saved.getStatutInscription() == StatutInscription.Confirmer) {
            notificationService.notifyInscriptionConfirme(saved);
        }

        return InscriptionMapper.toDto(saved);
    }

    public InscriptionDto updateInscription(InscriptionDto dto) {
        Inscription existing = inscriptionRepository.findById(dto.getInscriptionId())
                .orElseThrow(() -> new AppException(CodeErreur.SUBMIT_NOT_FOUND,
                        "Inscription non trouvée"));

        verifierInscriptionAnnuler(existing);

        // interdit de changer utilisateur
        if (!existing.getUtilisateur().getUtilisateurId().equals(dto.getUtilisateurId())) {
            throw new AppException(CodeErreur.USER_SUBMITTED_ALREADY,
                    "Impossible de modifier l'utilisateur d'une inscription");
        }

        if (existing.getSession().getDateDebutSession().isBefore(LocalDateTime.now())) {
            throw new AppException(CodeErreur.SUBMIT_UPDATE_END_INVALID,
                    "Impossible de modifier après le début");
        }

        if (dto.getStatutInscription() != existing.getStatutInscription()) {
            throw new AppException(CodeErreur.SUBMIT_UPDATE_STATUT_IMPOSSIBLE,
                    "Modification du statut interdite");
        }

        Inscription saved = inscriptionRepository.save(existing);

        return InscriptionMapper.toDto(saved);
    }

    public InscriptionDto annulerInscription(Long id) {
        Inscription inscription = inscriptionRepository.findById(id)
                .orElseThrow(() -> new AppException(CodeErreur.SUBMIT_NOT_FOUND,
                        "Inscription non trouvée"));

        Session session = inscription.getSession();

        if (session.getDateDebutSession().minusHours(24).isBefore(LocalDateTime.now())) {
            throw new AppException(CodeErreur.SUBMIT_CANCEL_MIN_DELAY_NOT_RESPECTED,
                    "Annulation possible uniquement 24h avant");
        }

        if (inscription.getStatutInscription() == StatutInscription.Annuler) {
            throw new AppException(CodeErreur.SUBMIT_ALREADY_CANCEL,
                    "Déjà annulée");
        }

        inscription.setStatutInscription(StatutInscription.Annuler);

        Inscription saved = inscriptionRepository.save(inscription);

        gererListeAttente(session);

        return InscriptionMapper.toDto(saved);
    }

    public void deleteById(Long id, String utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new AppException(CodeErreur.USER_NOT_FOUND,
                        "Utilisateur non trouvé"));

        if (!utilisateur.getRole().equals("ADMIN")) {
            throw new AppException(CodeErreur.USER_NOT_ADMIN,
                    "Seul un administrateur peut supprimer une inscription");
        }

        Inscription inscription = inscriptionRepository.findById(id)
                .orElseThrow(() -> new AppException(CodeErreur.SUBMIT_NOT_FOUND,
                        "Inscription non trouvée"));

        inscriptionRepository.delete(inscription);
    }

    public Page<InscriptionDto> findAllInscription(Long sessionId,
                                                   String utilisateurId,
                                                   StatutInscription statutInscription,
                                                   Pageable page) {
        List<Specification<Inscription>> specs = new ArrayList<>();
        if (sessionId != null) {
            specs.add((root, query, cb) ->
                    cb.equal(root.get("session").get("sessionId"), sessionId));
        }
        if (utilisateurId != null) {
            specs.add((root, query, cb) ->
                    cb.equal(root.get("utilisateur").get("utilisateurId"), utilisateurId));
        }
        if (statutInscription != null) {
            specs.add((root, query, cb) ->
                    cb.equal(root.get("statutInscription"), statutInscription));
        }
        Specification<Inscription> spec = Specification.allOf(specs);
        return inscriptionRepository.findAll(spec, page).map(InscriptionMapper::toDto);
    }
}