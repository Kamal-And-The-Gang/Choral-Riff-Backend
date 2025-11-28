package fr.afpa.choral_riff.controllers;

import fr.afpa.choral_riff.dto.InvitationDTO;
import fr.afpa.choral_riff.entity.Invitation;
import fr.afpa.choral_riff.entity.Role;
import fr.afpa.choral_riff.services.InvitationService;
import fr.afpa.choral_riff.services.MailService;
import fr.afpa.choral_riff.services.UtilisateurEnsembleService;
import fr.afpa.choral_riff.dto.CreateInvitationDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invitations")
public class InvitationController {

    private final InvitationService invitationService;
    private final MailService mailService;
    private final UtilisateurEnsembleService utilisateurEnsembleService; // <-- ajout

    public InvitationController(InvitationService invitationService, MailService mailService,
            UtilisateurEnsembleService utilisateurEnsembleService) {
        this.invitationService = invitationService;
        this.mailService = mailService;
        this.utilisateurEnsembleService = utilisateurEnsembleService; // <-- ajout
    }

    

    @PostMapping
    public ResponseEntity<?> creerInvitation(@Valid @RequestBody CreateInvitationDTO createInvitationDTO) {
        try {
            InvitationDTO created = invitationService.creerInvitation(createInvitationDTO);
            // Utiliser le token réel de l'invitation
            mailService.sendInvitationEmail(created.getEmailInvite(), created.getToken());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
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
    //     InvitationDTO dto = invitationService.getByToken(token);
    //     return ResponseEntity.ok(dto);
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
}
