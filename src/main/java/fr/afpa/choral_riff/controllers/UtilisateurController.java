package fr.afpa.choral_riff.controllers;

import fr.afpa.choral_riff.dto.RegisterDto;
import fr.afpa.choral_riff.dto.UtilisateurDto;
import fr.afpa.choral_riff.entity.Invitation;
import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.services.InvitationService;
import fr.afpa.choral_riff.services.UtilisateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * Contrôleur REST pour gérer les utilisateurs.
 */

@RestController
@RequestMapping("/api/utilisateur")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;
    private final InvitationService invitationService;

    public UtilisateurController(UtilisateurService utilisateurService, InvitationService invitationService) {
        this.utilisateurService = utilisateurService;
        this.invitationService = invitationService; // Injection
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
        // Créer l'utilisateur
        Utilisateur nouvelUtilisateur = utilisateurService.createFromRegisterDto(dto);

        // Rattacher l'utilisateur à l'invitation si token présent
        String token = dto.getToken();
        if (token != null && !token.isEmpty()) {
            // 🔹 Correction : récupérer l'invitation et passer l'utilisateur et
            // l'invitation
            Invitation invitation = invitationService.getByTokenEntity(token);
            invitationService.rattacherUtilisateurApresInscription(nouvelUtilisateur, invitation);
        }

        return ResponseEntity.ok("Inscription via invitation réussie !");
    }

    @GetMapping("/ensembles/{ensembleId}/membres")
    public ResponseEntity<List<UtilisateurDto>> getMembresEnsemble(@PathVariable Long ensembleId) {
        List<UtilisateurDto> membres = utilisateurService.getUtilisateursParEnsemble(ensembleId);
        return ResponseEntity.ok(membres);
    }

    @GetMapping("/me")
    public ResponseEntity<UtilisateurDto> getCurrentUser(@AuthenticationPrincipal Utilisateur utilisateur) {
        if (utilisateur == null) {
            return ResponseEntity.status(401).build(); // Non authentifié
        }
        UtilisateurDto dto = utilisateurService.toDto(utilisateur);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/me/photo")
    public ResponseEntity<UtilisateurDto> updatePhotoProfil(@RequestParam("photoProfil") MultipartFile file) {
        UtilisateurDto updatedUser = utilisateurService.updatePhotoProfil(file);
        return ResponseEntity.ok(updatedUser);
    }

}
