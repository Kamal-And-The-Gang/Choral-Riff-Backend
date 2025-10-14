package fr.afpa.choral_riff.controllers;

import fr.afpa.choral_riff.dto.InvitationDTO;
import fr.afpa.choral_riff.services.InvitationService;
import fr.afpa.choral_riff.services.MailService;
import fr.afpa.choral_riff.dto.CreateInvitationDTO;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
public class InvitationController {

    private final InvitationService invitationService;
    private final MailService mailService;

    public InvitationController(InvitationService invitationService, MailService mailService) {
        this.invitationService = invitationService;
        this.mailService = mailService;
    }

   

    @PostMapping
    // ("/invitations")
    public ResponseEntity<InvitationDTO> creerInvitation(
            @Valid @RequestBody CreateInvitationDTO createInvitationDTO) {

                //  LOGS POUR DÉBOGAGE
    System.out.println(">>> Reçu dans creerInvitation <<<");
    System.out.println("Email reçu : " + createInvitationDTO.getEmailInvite());
    System.out.println("EnsembleId reçu : " + createInvitationDTO.getEnsembleId());

        InvitationDTO created = invitationService.creerInvitation(createInvitationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
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
    @GetMapping("/token/{token}")
    public ResponseEntity<InvitationDTO> getByToken(@PathVariable String token) {
        InvitationDTO dto = invitationService.getByToken(token);
        return ResponseEntity.ok(dto);
    }
}
