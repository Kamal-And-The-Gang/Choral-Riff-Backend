package fr.afpa.choral_riff.controllers;

import fr.afpa.choral_riff.dto.InvitationDTO;
import fr.afpa.choral_riff.entity.Ensemble;
import fr.afpa.choral_riff.entity.Invitation;
import fr.afpa.choral_riff.entity.Notification;
import fr.afpa.choral_riff.entity.Role;
import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.repositories.EnsembleRepository;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;
import fr.afpa.choral_riff.services.InvitationService;
import fr.afpa.choral_riff.services.MailService;
import fr.afpa.choral_riff.services.NotificationService;
import fr.afpa.choral_riff.services.UtilisateurEnsembleService;
import fr.afpa.choral_riff.dto.CreateInvitationDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invitations")
public class InvitationController {

    private final UtilisateurEnsembleService utilisateurEnsembleService;
    private final NotificationService notificationService;
    private final UtilisateurRepository utilisateurRepository;
    private final EnsembleRepository ensembleRepository;
    private final InvitationService invitationService;
    private final MailService mailService;

    public InvitationController(InvitationService invitationService,
            UtilisateurEnsembleService utilisateurEnsembleService,
            NotificationService notificationService,
            UtilisateurRepository utilisateurRepository,
            EnsembleRepository ensembleRepository,
            MailService mailService) { // <--- nouveau param
        this.invitationService = invitationService;
        this.utilisateurEnsembleService = utilisateurEnsembleService;
        this.notificationService = notificationService;
        this.utilisateurRepository = utilisateurRepository;
        this.ensembleRepository = ensembleRepository;
        this.mailService = mailService; // <--- affectation
    }

    /**
     * Crée une nouvelle invitation pour un utilisateur.
     * 
     * @param createInvitationDTO DTO contenant les informations de l'invitation
     * @return L'invitation créée avec HTTP 201 ou une erreur HTTP 404 si
     *         utilisateur/ensemble introuvable
     */

    @PostMapping
    public ResponseEntity<?> creerInvitation(@Valid @RequestBody CreateInvitationDTO createInvitationDTO) {
        try {
            InvitationDTO created = invitationService.creerInvitation(createInvitationDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Récupère toutes les invitations pour un ensemble donné.
     * 
     * @param ensembleId ID de l'ensemble
     * @return Liste des invitations sous forme de DTO
     */
    @GetMapping("/ensemble/{ensembleId}")
    public ResponseEntity<List<InvitationDTO>> getByEnsemble(@PathVariable Long ensembleId) {
        List<InvitationDTO> invitations = invitationService.getAllByEnsembleId(ensembleId);
        return ResponseEntity.ok(invitations);

    }

    /**
     * Accepte une invitation via son token.
     * 
     * @param token Token unique de l'invitation
     * @return DTO de l'invitation mise à jour
     */
    @PostMapping("/accept")
    public ResponseEntity<InvitationDTO> accept(@RequestParam String token) {
        InvitationDTO updated = invitationService.accept(token);
        return ResponseEntity.ok(updated);
    }

    /**
     * Refuse une invitation via son token.
     * 
     * @param token Token unique de l'invitation
     * @return DTO de l'invitation refusée
     */
    @PostMapping("/refuse")
    public ResponseEntity<InvitationDTO> refuse(@RequestParam String token) {
        InvitationDTO updated = invitationService.refuse(token);
        return ResponseEntity.ok(updated);
    }

    /**
     * Supprime une invitation par son ID.
     * 
     * @param id ID de l'invitation à supprimer
     * @return HTTP 204 si succès
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        invitationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Récupère le rôle d'un utilisateur dans l'ensemble via le token de
     * l'invitation.
     * 
     * @param token Token de l'invitation
     * @return Nom du rôle (ADMIN, MEMBRE, etc.) ou 404 si l'utilisateur n'existe
     *         pas encore
     */
    @GetMapping("/role/{token}")
    public ResponseEntity<String> getRoleViaToken(@PathVariable String token) {
        try {
            Invitation invitation = invitationService.getByTokenEntity(token);

            if (invitation.getUtilisateur() == null) {
                // L'utilisateur n'est pas encore inscrit
                return ResponseEntity.notFound().build();
            }

            Role role = utilisateurEnsembleService.getRoleUtilisateurDansEnsemble(
                    invitation.getUtilisateur().getId(),
                    invitation.getEnsemble().getId());

            return ResponseEntity.ok(role.name());

        } catch (RuntimeException e) {
            // Par exemple : token expiré
            return ResponseEntity.status(410).body("Invitation expirée");
        }
    }

    /**
     * Rattache un nouvel utilisateur à une invitation après inscription.
     * 
     * @param token             Token de l'invitation
     * @param nouvelUtilisateur L'utilisateur fraîchement créé
     * @return DTO de l'invitation mise à jour
     */

    @PostMapping("/rattacher-apres-inscription")
    public ResponseEntity<InvitationDTO> rattacherApresInscription(
            @RequestParam String token,
            @RequestBody Utilisateur nouvelUtilisateur) {

        // Appelle le service pour rattacher l'utilisateur à l'invitation
        InvitationDTO invitationDTO = invitationService.rattacherUtilisateurApresInscription(
                nouvelUtilisateur,
                invitationService.getByTokenEntity(token));

        return ResponseEntity.ok(invitationDTO);
    }

    /**
     * Renvoie l'email d'invitation.
     * 
     * @param id ID de l'invitation
     * @return Message de succès ou erreur si non trouvée ou échec d'envoi
     */

    @PostMapping("/resend/{id}")
    public ResponseEntity<?> resendInvitation(@PathVariable Long id) {
        try {
            InvitationDTO inv = invitationService.getById(id);
            mailService.sendInvitationEmail(inv.getEmailInvite(), inv.getToken());
            return ResponseEntity.ok(Map.of("message", "Email renvoyé"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Invitation non trouvée"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de l'envoi de l'email"));
        }
    }

    // @PostMapping("/rattacher")
    // public ResponseEntity<?> rattacherUtilisateurAvecNotif(
    // @RequestParam Long ensembleId,
    // @RequestParam Long utilisateurId) {
    // try {
    // // Rattachement réel
    // utilisateurEnsembleService.rattacherUtilisateurAEnsemble(utilisateurId,
    // ensembleId);

    // // Création de la notification (nouvelle méthode propre)
    // Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
    // .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

    // Ensemble ensemble = ensembleRepository.findById(ensembleId)
    // .orElseThrow(() -> new RuntimeException("Ensemble introuvable"));

    // notificationService.notifyRattachement(utilisateur, ensemble);

    // // Réponse HTTP
    // Map<String, Object> response = new HashMap<>();
    // response.put("message", "Vous êtes maintenant rattaché à l'ensemble.");

    // return ResponseEntity.ok(response);
    // } catch (RuntimeException e) {
    // Map<String, String> error = new HashMap<>();
    // error.put("error", e.getMessage());
    // return ResponseEntity.badRequest().body(error);
    // }
    // }

    /**
     * Rattache un utilisateur à un ensemble suite à une demande de rattachement.
     * 
     * Flux :
     * 1) Rattache l'utilisateur à l'ensemble (création du lien
     * utilisateur-ensemble).
     * 2) Supprime la notification de type DEMANDE_RATTACHEMENT (celle avec les
     * boutons accepter/refuser),
     * afin qu'elle ne s'affiche plus dans le front après traitement.
     * 3) Crée une nouvelle notification de type RATTACHEMENT pour informer
     * l'utilisateur
     * qu'il a bien été rattaché à l'ensemble.
     *
     * 
     * 
     * 
     * /**
     * Rattache un utilisateur à un ensemble suite à une demande de rattachement.
     * Flux :
     * 1) Rattache l'utilisateur à l'ensemble.
     * 2) Supprime la notification de demande (si fournie).
     * 3) Crée une notification finale pour informer l'utilisateur.
     */

    @PostMapping("/rattacher")
    public ResponseEntity<?> rattacherUtilisateurAvecNotif(
            @RequestParam Long ensembleId,
            @RequestParam Long utilisateurId,
            @RequestParam(required = false) Long notificationId) { // <-- ajout

        try {
            // 1) rattacher
            utilisateurEnsembleService.rattacherUtilisateurAEnsemble(utilisateurId, ensembleId);

            // 2) supprimer la notification DEMANDE (si fournie)
            if (notificationId != null) {
                notificationService.deleteNotification(notificationId);
            }

            // 3) créer la notification finale
            Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            Ensemble ensemble = ensembleRepository.findById(ensembleId)
                    .orElseThrow(() -> new RuntimeException("Ensemble introuvable"));

            notificationService.notifyRattachement(utilisateur, ensemble);

            // return ResponseEntity.ok(Map.of("message", "Vous êtes maintenant rattaché à
            // l'ensemble."));
            return ResponseEntity.ok(Map.of(
                    "message", "Vous êtes maintenant rattaché à l'ensemble \"" + ensemble.getNom() + "\"."));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Demande le rattachement d'un utilisateur à un ensemble.
     * Vérifie d'abord que l'utilisateur n'est pas déjà membre.
     * Crée ensuite une notification de demande de rattachement.
     */

    @PostMapping("/demanderRattachement")
    public ResponseEntity<?> demanderRattachement(
            @RequestParam Long ensembleId,
            @RequestParam Long utilisateurId) {

        try {
            boolean dejaMembre = utilisateurEnsembleService
                    .estMembreDeLensemble(utilisateurId, ensembleId);

            if (dejaMembre) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "L'utilisateur fait déjà partie de cet ensemble."));
            }

            // Récupère l'ensemble pour inclure son nom dans la notification
            Ensemble ensemble = ensembleRepository.findById(ensembleId)
                    .orElseThrow(() -> new RuntimeException("Ensemble introuvable"));

            // Crée la notification de demande de rattachement
            notificationService.createNotification(
                    utilisateurId,
                    "DEMANDE_RATTACHEMENT",
                    "L'administrateur souhaite vous rattacher à l’ensemble \"" + ensemble.getNom() + "\".",
                    ensembleId // <-- passer ensembleId ici
            );

            return ResponseEntity.ok(Map.of("message", "Demande de rattachement envoyée à l'utilisateur."));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Refuse une demande de rattachement.
     * Supprime simplement la notification correspondante.
     * 
     * @param notificationId ID de la notification à supprimer
     */

    @PostMapping("/refuser")
    public void refuser(@RequestParam Long notificationId) {
        notificationService.deleteNotification(notificationId);
    }

}
