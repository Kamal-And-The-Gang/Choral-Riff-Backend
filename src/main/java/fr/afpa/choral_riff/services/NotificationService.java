
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

/**
 * Service pour gérer toutes les notifications de l'application.
 * <p>
 * Ce service centralise la création, la récupération, la mise à jour
 * et la suppression des notifications pour les utilisateurs.
 * </p>
 */

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

    // ==========================
    // Méthode principale de création de notification
    // ==========================
    /**
     * Crée une notification pour un utilisateur.
     * Peut être liée à une invitation (invitationId facultatif).
     * 
     * @param utilisateurId ID de l'utilisateur destinataire
     * @param type          Type de notification (ex : INVITATION, RATTACHEMENT)
     * @param message       Message de la notification
     * @param invitationId  ID de l'invitation si applicable, sinon null
     * @return NotificationDto correspondant à la notification créée
     */
    @Transactional
    public NotificationDto createNotification(Long utilisateurId, String type, String message, Long invitationId) {
        // Étape 1 : récupérer l'utilisateur depuis la base (managed)
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        // Étape 2 : récupérer l'invitation si elle existe
        Invitation invitation = null;
        if (invitationId != null) {
            invitation = invitationRepository.findById(invitationId)
                    .orElseThrow(() -> new IllegalArgumentException("Invitation non trouvée"));
            System.out.println("Invitation MANAGED id=" + invitation.getId());
        }
        // Étape 3 : convertir le type en enum NotificationType
        NotificationType notificationType = NotificationType.valueOf(type);
        // Étape 4 : créer l'objet Notification
        Notification notification = new Notification();
        notification.setUtilisateur(utilisateur);
        notification.setType(notificationType);
        notification.setMessage(message);
        notification.setDateCreation(LocalDateTime.now());

        // Lier l'invitation si elle existe
        notification.setInvitation(invitation);

        // Étape 5 : sauvegarder immédiatement en base
        notificationRepository.saveAndFlush(notification);
        // Étape 6 : convertir l'entité en DTO pour le front
        return notificationMapper.toDTO(notification);
    }

    // ==========================
    // Surcharge pour notifications générales
    // ==========================
    /**
     * Crée une notification simple (pas d'invitation liée)
     */

    @Transactional
    public NotificationDto createNotification(Long utilisateurId, String type, String message) {
        // Appelle la version principale avec invitationId = null
        return createNotification(utilisateurId, type, message, null);
    }

    // ==========================
    // Récupération des notifications
    // ==========================
    /**
     * Retourne toutes les notifications pour un utilisateur donné
     * sous forme de DTO.
     * 
     * @param utilisateurId ID de l'utilisateur
     * @return Liste de NotificationDto
     */

    public List<NotificationDto> getNotificationsByUtilisateur(Long utilisateurId) {
        List<Notification> notifications = notificationRepository.findByUtilisateurId(utilisateurId);

        for (Notification n : notifications) {
            System.out.println("=== NOTIFICATION ===");
            System.out.println("notif id = " + n.getId());
            System.out.println("id_invitation FK = " +
                    (n.getInvitation() != null ? n.getInvitation().getId() : "NULL"));
        }
        // Convertir en DTO pour le front
        return notifications.stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ==========================
    // Marquer une notification comme lue
    // ==========================
    /**
     * Met à jour l'état d'une notification pour la marquer comme lue.
     */
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

    // ==========================
    // Notifications de rattachement
    // ==========================
    /**
     * Crée une notification de type RATTACHEMENT pour un utilisateur.
     * Utilisé lorsqu'un utilisateur est rattaché automatiquement à un ensemble.
     * 
     * @param utilisateur Utilisateur rattaché
     * @param ensemble    Ensemble auquel l'utilisateur est rattaché
     */

    public void notifyRattachement(Utilisateur utilisateur, Ensemble ensemble) {
        // Étape 1 : récupérer l'utilisateur MANAGED
        Utilisateur managedUser = utilisateurRepository.findById(utilisateur.getId())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        // Étape 2 : créer la notification
        Notification notification = new Notification();
        notification.setUtilisateur(managedUser); // utiliser managedUser ici
        notification.setType(NotificationType.RATTACHEMENT);
        notification.setMessage(
                "Vous avez été rattaché à l’ensemble \"" + ensemble.getNom() + "\"");
        notification.setIsRead(false);
        notification.setDateCreation(LocalDateTime.now());
        notification.setEnsembleId(ensemble.getId());

        // Étape 3 : sauvegarder
        notificationRepository.saveAndFlush(notification);
    }

    @Transactional
    public void notifyMorceauAjoute(Utilisateur utilisateur, Ensemble ensemble, String nomMorceau) {
        Utilisateur managedUser = utilisateurRepository.findById(utilisateur.getId())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        Notification notification = new Notification();
        notification.setUtilisateur(managedUser);
        notification.setType(NotificationType.MORCEAU_AJOUTE);
        notification.setMessage(
                "Nouveau morceau ajouté : \"" + nomMorceau + "\" dans l'ensemble \"" + ensemble.getNom() + "\"");
        notification.setIsRead(false);
        notification.setDateCreation(LocalDateTime.now());
        notification.setEnsembleId(ensemble.getId());

        notificationRepository.saveAndFlush(notification);
    }

}
