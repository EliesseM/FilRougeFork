package fr.schoolbyhiit.projetfilrouge.dto.notification;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationDto {
    private Long notificationId;

    private String titre;
    private String description;
    private LocalDateTime date;

    private boolean isLu;

    private String utilisateurId;
    private Long evenementId;
    private Long sessionId;
}
