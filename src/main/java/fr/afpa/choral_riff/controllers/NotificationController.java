
package fr.afpa.choral_riff.controllers;

import fr.afpa.choral_riff.dto.NotificationDto;
import fr.afpa.choral_riff.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Récupérer toutes les notifications pour un utilisateur spécifique.
     *
     * @param utilisateurId L'ID de l'utilisateur
     * @return Liste des notifications sous forme de DTO
     */
    @GetMapping
    public List<NotificationDto> getNotifications(@RequestParam Long utilisateurId) {
        // Appel du service pour récupérer les notifications sous forme de
        // NotificationDTO
        return notificationService.getNotificationsByUtilisateur(utilisateurId);
    }

    /**
     * Marquer une notification comme lue.
     *
     * @param notificationId L'ID de la notification
     * @return Notification marquée comme lue sous forme de DTO
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Supprimer une notification.
     *
     * @param notificationId L'ID de la notification à supprimer
     */
    @DeleteMapping("/{notificationId}")
    public void deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
    }
}
