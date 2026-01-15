
package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.dto.NotificationDto;
import fr.afpa.choral_riff.entity.Ensemble;
import fr.afpa.choral_riff.entity.Invitation;
import fr.afpa.choral_riff.entity.Notification;
import fr.afpa.choral_riff.entity.NotificationType;
import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.mapper.NotificationMapper;
import fr.afpa.choral_riff.repositories.InvitationRepository;
import fr.afpa.choral_riff.repositories.NotificationRepository;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final InvitationRepository invitationRepository;
    private final NotificationMapper notificationMapper;

    public NotificationService(NotificationRepository notificationRepository,
            UtilisateurRepository utilisateurRepository,
            InvitationRepository invitationRepository,
            NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.invitationRepository = invitationRepository;
        this.notificationMapper = notificationMapper;
    }

    // ===========================
    // Méthode principale (4 paramètres)
    // ===========================

    // Méthode principale

    // Gère les notifications générales et liées

    // Vérifie l’existence de :

    // l’utilisateur

    // le type

    // l’invitation

    // Utilise @Transactional (sécurité DB)

    // Utilise saveAndFlush (force l’écriture immédiate)

    // Retourne un DTO (bonne pratique)
    // @Transactional
    // public NotificationDto createNotification(Long utilisateurId, String type,
    // String message, Long invitationId) {
    // // Récupérer l'utilisateur
    // Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
    // .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec
    // cet ID"));

    // // Vérifier que le type est valide
    // NotificationType notificationType;
    // try {
    // notificationType = NotificationType.valueOf(type);
    // } catch (IllegalArgumentException e) {
    // throw new IllegalArgumentException("Type de notification invalide : " +
    // type);
    // }

    // // Créer la notification
    // Notification notification = new Notification();
    // notification.setUtilisateur(utilisateur);

    // notification.setType(notificationType);
    // notification.setMessage(message);
    // notification.setDateCreation(LocalDateTime.now());

    // if (invitationId != null) {
    // Invitation invitation = invitationRepository.findById(invitationId)
    // .orElseThrow(() -> new IllegalArgumentException("Invitation non trouvée"));
    // notification.setInvitation(invitation);

    // // Debug console
    // System.out.println("Notification liée à l'invitation ID=" +
    // invitation.getId() +
    // " avec état=" + invitation.getEtat());
    // }

    // // Sauvegarder la notification
    // notification = notificationRepository.saveAndFlush(notification); // <--
    // saveAndFlush au lieu de save
    // // Debug pour vérifier que l'invitation est bien liée
    // System.out.println("Notification saved: id=" + notification.getId() +
    // ", invitationId="
    // + (notification.getInvitation() != null ?
    // notification.getInvitation().getId() : "NULL"));

    // // Convertir en DTO
    // return notificationMapper.toDTO(notification);
    // }

    @Transactional
    public NotificationDto createNotification(Long utilisateurId, String type, String message, Long invitationId) {

        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        Invitation invitation = null;
        if (invitationId != null) {
            invitation = invitationRepository.findById(invitationId)
                    .orElseThrow(() -> new IllegalArgumentException("Invitation non trouvée"));
            System.out.println("Invitation MANAGED id=" + invitation.getId());
        }

        NotificationType notificationType = NotificationType.valueOf(type);

        Notification notification = new Notification();
        notification.setUtilisateur(utilisateur);
        notification.setType(notificationType);
        notification.setMessage(message);
        notification.setDateCreation(LocalDateTime.now());

        // LIGNE CRUCIALE
        notification.setInvitation(invitation);

        // UN SEUL SAVE
        notificationRepository.saveAndFlush(notification);

        return notificationMapper.toDTO(notification);
    }

    // ===========================
    // Surcharge pour notifications générales (3 paramètres)

    // ===========================

    // Appelle la méthode principale

    // Évite la duplication de code

    // Plus simple à appeler depuis un controller
    // Quand l’utiliser ? : Quand la notification n’est liée à rien

    @Transactional
    public NotificationDto createNotification(Long utilisateurId, String type, String message) {
        // Appelle la version principale avec invitationId = null
        return createNotification(utilisateurId, type, message, null);
    }

    public List<NotificationDto> getNotificationsByUtilisateur(Long utilisateurId) {
        List<Notification> notifications = notificationRepository.findByUtilisateurId(utilisateurId);

        for (Notification n : notifications) {
            System.out.println("=== NOTIFICATION ===");
            System.out.println("notif id = " + n.getId());
            System.out.println("id_invitation FK = " +
                    (n.getInvitation() != null ? n.getInvitation().getId() : "NULL"));
        }

        return notifications.stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ===========================
    // Marquer comme lue
    // ===========================
    public NotificationDto markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée"));

        notification.setIsRead(true);
        notification = notificationRepository.save(notification);

        return notificationMapper.toDTO(notification);
    }

    // ===========================
    // Supprimer une notification
    // ===========================
    public void deleteNotification(Long notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new RuntimeException("Notification non trouvée");
        }
        notificationRepository.deleteById(notificationId);
    }

    public void notifyRattachement(Utilisateur utilisateur, Ensemble ensemble) {
        Notification notification = new Notification();
        notification.setUtilisateur(utilisateur);
        notification.setType(NotificationType.RATTACHEMENT);
        notification.setMessage(
                "Vous avez été rattaché à l’ensemble \"" + ensemble.getNom() + "\"");
        notification.setIsRead(false);
        notification.setDateCreation(LocalDateTime.now());
        notification.setEnsembleId(ensemble.getId());

        notificationRepository.save(notification);
    }
}
