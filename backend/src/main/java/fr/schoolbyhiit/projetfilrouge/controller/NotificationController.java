package fr.schoolbyhiit.projetfilrouge.controller;

import fr.schoolbyhiit.projetfilrouge.dto.notification.NotificationDto;
import fr.schoolbyhiit.projetfilrouge.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PutMapping("/lu/{notificationId}")
    public void marquerNotificationCommeLu(@PathVariable Long notificationId) {
        notificationService.marquerCommeLu(notificationId);
    }

    @GetMapping("/utilisateur/{utilisateurId}")
    public Page<NotificationDto> getNotifications(
            @PathVariable String utilisateurId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return notificationService.getNotificationsByUtilisateur(
                utilisateurId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date")) // Permet de forcer le tri par la plus notifs la plus récente
        );
    }

}
