package fr.schoolbyhiit.projetfilrouge.service;

import fr.schoolbyhiit.projetfilrouge.dto.notification.NotificationDto;
import fr.schoolbyhiit.projetfilrouge.entity.*;
import fr.schoolbyhiit.projetfilrouge.exception.AppException;
import fr.schoolbyhiit.projetfilrouge.exception.CodeErreur;
import fr.schoolbyhiit.projetfilrouge.mapper.NotificationMapper;
import fr.schoolbyhiit.projetfilrouge.repository.InscriptionRepository;
import fr.schoolbyhiit.projetfilrouge.repository.NotificationRepository;
import fr.schoolbyhiit.projetfilrouge.repository.SessionRepository;
import fr.schoolbyhiit.projetfilrouge.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final InscriptionRepository inscriptionRepository;
    private final SessionRepository sessionRepository;
    private final UtilisateurRepository utilisateurRepository;

    public void marquerCommeLu(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(CodeErreur.NOTIFICATION_NOT_FOUND, "Notification non trouvée"));

        notification.setLu(true);
        notificationRepository.save(notification);
    }

    public Page<NotificationDto> getNotificationsByUtilisateur(String utilisateurId, Pageable pageable) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new AppException(CodeErreur.USER_NOT_FOUND, "Utilisateur non trouvé"));

        return notificationRepository.findByUtilisateur(utilisateur, pageable)
                .map(notificationMapper::toDto);
    }

    public void notifySessionUpdate(Session session, String message) {
        messagingTemplate.convertAndSend(
                "/topic/notification/session-" + session.getSessionId(),
                message
        );

        List<Inscription> inscriptions = inscriptionRepository.findBySession(session);

        for (Inscription inscription : inscriptions) {
            Utilisateur utilisateur = inscription.getUtilisateur();

            Notification notification = notificationMapper.toEntity(
                    "Mise à jour de la session",
                    message,
                    utilisateur,
                    session.getEvenement(),
                    session
            );

            notificationRepository.save(notification);
        }
    }

    public void notifyEvenementUpdate(Evenement evenement, String message) {
        // Log pour vérifier l'appel de la méthode
        System.out.println("DEBUG: notifyEvenementUpdate appelé pour l'événement ID: " + evenement.getEvenementId() + " - Message: " + message);

        // Envoi du message via WebSocket
        String topic = "/topic/notification/evenement-" + evenement.getEvenementId();
        System.out.println("DEBUG: Envoi du message sur le topic: " + topic);
        messagingTemplate.convertAndSend(topic, message);

        // Récupération des sessions liées à l'événement
        List<Session> sessions = sessionRepository.findByEvenement(evenement);
        System.out.println("DEBUG: Nombre de sessions trouvées pour cet événement: " + sessions);

        // Notification des sessions
        for (Session session : sessions) {
            System.out.println("DEBUG: Notification de la session ID: " + session.getSessionId());
            notifySessionUpdate(session, message);
        }
    }

    // À voir si notifié tous les utilisateurs est vraiment utile dans notre cas !
    public void notifyAllUsers(String message) {
        messagingTemplate.convertAndSend("/topic/notification/global", message);
    }

    public void notifyUser(Utilisateur utilisateur, String message) {

        messagingTemplate.convertAndSend(
                "/topic/notification/utilisateur-" + utilisateur.getUtilisateurId(),
                message
        );

        Notification notification = notificationMapper.toEntity(
                "Notification personnelle",
                message,
                utilisateur,
                null,
                null
        );

        notificationRepository.save(notification);
    }


    public void notifyInscriptionConfirme(Inscription inscription) {
        String message = String.format(
                "Votre inscription à la session:" + inscription.getSession().getTitre() + "a été confirmée."

        );

        notifyUser(inscription.getUtilisateur(), message);
    }

}

