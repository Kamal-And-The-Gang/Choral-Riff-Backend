package fr.afpa.choral_riff.mapper;

import fr.afpa.choral_riff.dto.NotificationDto;
import fr.afpa.choral_riff.entity.Notification;
import fr.afpa.choral_riff.entity.NotificationType;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class NotificationMapper {

    // Convertir Notification en NotificationDto
    public NotificationDto toDTO(Notification notification) {
        if (notification == null) {
            return null;
        }

        NotificationDto dto = new NotificationDto();
        dto.setId(notification.getId());
        dto.setType(notification.getType().name()); // Convertir NotificationType en String ("INVITATION",
                                                    // "MORCEAU_AJOUTE", etc.)
        dto.setMessage(notification.getMessage());
        dto.setIsRead(notification.getIsRead());

        // LocalDateTime -> String en format ISO
        dto.setCreatedAt(notification.getDateCreation());

        if (notification.getInvitation() != null) {

            System.out.println("Notification " + notification.getId() + " a une invitation");
            System.out.println("Etat invitation : " + notification.getInvitation().getEtat());

            dto.setInvitationId(notification.getInvitation().getId());

            if (notification.getInvitation() != null && notification.getInvitation().getEtat() != null) {
                dto.setStatus(notification.getInvitation().getEtat().name()); // "ACCEPTEE", "REFUSEE", "EN_ATTENTE"
            }

            else {
                dto.setStatus("EN_ATTENTE"); // valeur par défaut
            }
            dto.setSenderName(notification.getInvitation().getUtilisateur().getNom());
        }

        if (notification.getInvitation().getEnsemble() != null) {
            dto.setEnsembleId(
                    notification.getInvitation().getEnsemble().getId());
            dto.setEnsembleNom(
                    notification.getInvitation().getEnsemble().getNom());
        }

        return dto;
    }

    // Convertir NotificationDto en Notification
    public Notification toEntity(NotificationDto notificationDTO) {
        if (notificationDTO == null) {
            return null;
        }

        Notification notification = new Notification();
        notification.setId(notificationDTO.getId());
        notification.setType(NotificationType.valueOf(notificationDTO.getType())); // Convertir String ("INVITATION") en
                                                                                   // NotificationType
        notification.setMessage(notificationDTO.getMessage());
        notification.setIsRead(notificationDTO.getIsRead());

        // String -> LocalDateTime (ISO format)
        notification.setDateCreation(notificationDTO.getCreatedAt()); // Si `createdAt` est déjà un LocalDateTime, on
                                                                      // peut le passer directement

        return notification;
    }
}
