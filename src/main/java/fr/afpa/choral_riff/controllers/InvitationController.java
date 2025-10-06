package fr.afpa.choral_riff.controllers;

import fr.afpa.choral_riff.dto.InvitationDTO;
import fr.afpa.choral_riff.services.InvitationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
public class InvitationController {

    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
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
     * Créer une invitation.
     */
    @PostMapping
    public ResponseEntity<InvitationDTO> create(@RequestBody InvitationDTO invitationDTO) {
        InvitationDTO created = invitationService.create(invitationDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
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
     * Supprimer une invitation (par son ID).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        invitationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Récupérer une invitation via son token (utile par exemple pour le front en réception de lien email).
     */
    @GetMapping("/token/{token}")
    public ResponseEntity<InvitationDTO> getByToken(@PathVariable String token) {
        InvitationDTO dto = invitationService.getByToken(token);
        return ResponseEntity.ok(dto);
    }
}


