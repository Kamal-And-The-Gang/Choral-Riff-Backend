package fr.afpa.choral_riff.mapper;

import fr.afpa.choral_riff.dto.NotificationDto;
import fr.afpa.choral_riff.entity.Notification;
import fr.afpa.choral_riff.entity.NotificationType;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationDto toDTO(Notification notification) {
        if (notification == null)
            return null;

        NotificationDto dto = new NotificationDto();
        dto.setId(notification.getId());
        dto.setType(notification.getType().name());
        dto.setMessage(notification.getMessage());
        dto.setIsRead(notification.getIsRead());
        dto.setCreatedAt(notification.getDateCreation());

        // Champs communs : toujours remplis
        if (notification.getUtilisateur() != null) {
            dto.setUtilisateurId(notification.getUtilisateur().getId());
            dto.setSenderName(notification.getUtilisateur().getNom());
        }

        // Ensemble si présent
        dto.setEnsembleId(notification.getEnsembleId());
        // dto.setEnsembleNom(notification.getEnsembleNom());

        // Cas spécial invitation
        if (notification.getInvitation() != null) {
            dto.setInvitationId(notification.getInvitation().getId());
            dto.setStatus(notification.getInvitation().getEtat() != null
                    ? notification.getInvitation().getEtat().name()
                    : "EN_ATTENTE");

            if (notification.getInvitation().getUtilisateur() != null) {
                dto.setSenderName(notification.getInvitation().getUtilisateur().getNom());
            }
            if (notification.getInvitation().getEnsemble() != null) {
                dto.setEnsembleId(notification.getInvitation().getEnsemble().getId());
                dto.setEnsembleNom(notification.getInvitation().getEnsemble().getNom());
            }
        }
        // Pour les types autres que invitation, on peut définir un status par défaut
        else if (notification.getType() == NotificationType.RATTACHEMENT) {
            dto.setStatus("ACCEPTEE");
        }
        // ← AJOUT : remplir valid pour toutes les notifications
        dto.setValid(true);

        return dto;
    }
}
