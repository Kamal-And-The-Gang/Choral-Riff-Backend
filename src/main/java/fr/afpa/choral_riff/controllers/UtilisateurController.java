package fr.afpa.choral_riff.controllers;

import fr.afpa.choral_riff.dto.RegisterDto;
import fr.afpa.choral_riff.dto.UtilisateurDto;
import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.services.InvitationService;
import fr.afpa.choral_riff.services.UtilisateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour gérer les utilisateurs.
 */
@RestController
@RequestMapping("/api/utilisateur")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;
    private final InvitationService invitationService; // 👈 Ajouter ici

    public UtilisateurController(UtilisateurService utilisateurService, InvitationService invitationService) {
        this.utilisateurService = utilisateurService;
        this.invitationService = invitationService; // 👈 Injection
    }

    /**
     * Récupérer tous les utilisateurs
     */
    @GetMapping
    public ResponseEntity<List<UtilisateurDto>> getAll() {
        return ResponseEntity.ok(utilisateurService.getAll());
    }

    /**
     * Récupérer un utilisateur par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(utilisateurService.getById(id));
    }

    /**
     * Créer un utilisateur
     */
    @PostMapping
    public ResponseEntity<UtilisateurDto> create(@RequestBody UtilisateurDto dto) {
        return ResponseEntity.ok(utilisateurService.create(dto));
    }

    /**
     * Mettre à jour un utilisateur
     */
    @PutMapping("/{id}")
    public ResponseEntity<UtilisateurDto> update(@PathVariable Long id, @RequestBody UtilisateurDto dto) {
        return ResponseEntity.ok(utilisateurService.update(id, dto));
    }

    /**
     * Supprimer un utilisateur
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        utilisateurService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/inscription-invitation")
    public ResponseEntity<String> inscriptionViaInvitation(@RequestBody RegisterDto dto) {
        // 1️⃣ Créer l'utilisateur
        Utilisateur nouvelUtilisateur = utilisateurService.createFromRegisterDto(dto);

        // 2️⃣ Rattacher l'utilisateur à l'invitation si token présent
        String token = dto.getToken();
        if (token != null && !token.isEmpty()) {
            invitationService.rattacherUtilisateurApresInscription(token, nouvelUtilisateur);
        }

        return ResponseEntity.ok("Inscription via invitation réussie !");
    }

}
