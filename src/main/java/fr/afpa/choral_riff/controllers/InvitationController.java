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
     * Récupérer toutes les invitations d’un ensemble donné.
     */
    @GetMapping("/ensemble/{ensembleId}")
    public ResponseEntity<List<InvitationDTO>> getByEnsemble(@PathVariable Long ensembleId) {
        List<InvitationDTO> invitations = invitationService.getAllByEnsembleId(ensembleId);
        return ResponseEntity.ok(invitations);

    }

    /**
     * Accepter une invitation via token.
     */
    @PostMapping("/accept")
    public ResponseEntity<InvitationDTO> accept(@RequestParam String token) {
        InvitationDTO updated = invitationService.accept(token);
        return ResponseEntity.ok(updated);
    }

    /**
     * Refuser une invitation via token.
     */
    @PostMapping("/refuse")
    public ResponseEntity<InvitationDTO> refuse(@RequestParam String token) {
        InvitationDTO updated = invitationService.refuse(token);
        return ResponseEntity.ok(updated);
    }

    /**
     * Supprimer une invitation par son ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        invitationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Récupérer une invitation via son token.
     */
    // @GetMapping("/token/{token}")
    // public ResponseEntity<InvitationDTO> getByToken(@PathVariable String token) {
    // InvitationDTO dto = invitationService.getByToken(token);
    // return ResponseEntity.ok(dto);
    // }

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

    @PostMapping("/rattacher")
    public ResponseEntity<?> rattacherUtilisateurAvecNotif(
            @RequestParam Long ensembleId,
            @RequestParam Long utilisateurId) {
        try {
            // 1️⃣ Rattachement réel
            utilisateurEnsembleService.rattacherUtilisateurAEnsemble(utilisateurId, ensembleId);

            // 2️⃣ Création de la notification (nouvelle méthode propre)
            Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            Ensemble ensemble = ensembleRepository.findById(ensembleId)
                    .orElseThrow(() -> new RuntimeException("Ensemble introuvable"));

            notificationService.notifyRattachement(utilisateur, ensemble);

            // 3️⃣ Réponse HTTP
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Vous êtes maintenant rattaché à l'ensemble.");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

}
