package fr.schoolbyhiit.projetfilrouge.mapper;

import fr.schoolbyhiit.projetfilrouge.dto.notification.NotificationDto;
import fr.schoolbyhiit.projetfilrouge.entity.Evenement;
import fr.schoolbyhiit.projetfilrouge.entity.Notification;
import fr.schoolbyhiit.projetfilrouge.entity.Session;
import fr.schoolbyhiit.projetfilrouge.entity.Utilisateur;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class NotificationMapper {

    public NotificationDto toDto(Notification notification) {
        if (notification == null) return null;

        NotificationDto dto = new NotificationDto();
        dto.setNotificationId(notification.getNotificationId());
        dto.setTitre(notification.getTitre());
        dto.setDescription(notification.getDescription());
        dto.setDate(notification.getDate());
        dto.setLu(notification.isLu());

        if (notification.getUtilisateur() != null) {
            dto.setUtilisateurId(notification.getUtilisateur().getUtilisateurId());
        }
        if (notification.getEvenement() != null) {
            dto.setEvenementId(notification.getEvenement().getEvenementId());
        }
        if (notification.getSession() != null) {
            dto.setSessionId(notification.getSession().getSessionId());
        }

        return dto;
    }


    public Notification toEntity(String titre, String description,
                                 Utilisateur utilisateur,
                                 Evenement evenement,
                                 Session session) {

        Notification notification = new Notification();
        notification.setTitre(titre);
        notification.setDescription(description);
        notification.setDate(LocalDateTime.now());

        notification.setLu(false);

        notification.setUtilisateur(utilisateur);
        notification.setEvenement(evenement);
        notification.setSession(session);

        return notification;
    }
}