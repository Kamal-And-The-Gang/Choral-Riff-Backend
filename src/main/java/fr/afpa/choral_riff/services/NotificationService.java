
package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.dto.NotificationDto;
import fr.afpa.choral_riff.entity.Ensemble;
import fr.afpa.choral_riff.entity.Invitation;
import fr.afpa.choral_riff.entity.Morceau;
import fr.afpa.choral_riff.entity.Notification;
import fr.afpa.choral_riff.entity.NotificationType;
import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.entity.UtilisateurEnsemble;
import fr.afpa.choral_riff.mapper.NotificationMapper;
import fr.afpa.choral_riff.repositories.InvitationRepository;
import fr.afpa.choral_riff.repositories.NotificationRepository;
import fr.afpa.choral_riff.repositories.UtilisateurEnsembleRepository;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final UtilisateurEnsembleRepository utilisateurEnsembleRepository;

    public NotificationService(UtilisateurEnsembleRepository utilisateurEnsembleRepository,
            NotificationRepository notificationRepository,
            UtilisateurRepository utilisateurRepository,
            InvitationRepository invitationRepository,
            NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.invitationRepository = invitationRepository;
        this.notificationMapper = notificationMapper;
        this.utilisateurEnsembleRepository = utilisateurEnsembleRepository;
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
    public NotificationDto createNotification(Long utilisateurId, String type, String message, Long invitationId,
            Long ensembleId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        Invitation invitation = null;
        if (invitationId != null) {
            invitation = invitationRepository.findById(invitationId)
                    .orElseThrow(() -> new IllegalArgumentException("Invitation non trouvée"));
        }

        NotificationType notificationType = NotificationType.valueOf(type);

        Notification notification = new Notification();
        notification.setUtilisateur(utilisateur);
        notification.setType(notificationType);
        notification.setMessage(message);
        notification.setDateCreation(LocalDateTime.now());
        notification.setInvitation(invitation);
        notification.setEnsembleId(ensembleId); // <-- important !

        notificationRepository.saveAndFlush(notification);

        return notificationMapper.toDTO(notification);
    }

    // Surcharge pour notifications générales (sans invitation)
    @Transactional
    public NotificationDto createNotification(Long utilisateurId, String type, String message, Long ensembleId) {
        return createNotification(utilisateurId, type, message, null, ensembleId);
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
        // Créer une notification de type RATTACHEMENT pour l'utilisateur
        Notification notification = new Notification();
        notification.setUtilisateur(utilisateur); // Assure que l'utilisateur est bien lié
        notification.setType(NotificationType.RATTACHEMENT);
        notification.setMessage(
                "Vous avez été rattaché à l’ensemble \"" + ensemble.getNom() + "\"");
        notification.setIsRead(false);
        notification.setDateCreation(LocalDateTime.now());
        notification.setEnsembleId(ensemble.getId());

        // Sauvegarder la notification
        notificationRepository.saveAndFlush(notification);
    }

    @Transactional
    public void notifyMorceauAjoute(Morceau morceau) {
        Ensemble ensemble = morceau.getEnsemble();
        Utilisateur createur = morceau.getCreateur();

        // Construire le nom du créateur
        String nomCreateur = createur.getPrenom() + " " + createur.getNom();

        // Récupérer tous les membres de l'ensemble
        List<UtilisateurEnsemble> membresEnsemble = utilisateurEnsembleRepository.findByEnsembleId(ensemble.getId());

        List<Notification> notifications = new ArrayList<>();

        for (UtilisateurEnsemble ue : membresEnsemble) {
            Notification notification = new Notification();
            notification.setUtilisateur(ue.getUtilisateur());
            notification.setType(NotificationType.MORCEAU_AJOUTE);
            notification.setMessage(
                    "L'utilisateur \"" + nomCreateur + "\" a ajouté le morceau \"" + morceau.getTitre()
                            + "\" dans l'ensemble \"" + ensemble.getNom() + "\"");
            notification.setIsRead(false);
            notification.setDateCreation(LocalDateTime.now());
            notification.setEnsembleId(ensemble.getId());

            notifications.add(notification);
        }

        notificationRepository.saveAll(notifications);
    }

}
