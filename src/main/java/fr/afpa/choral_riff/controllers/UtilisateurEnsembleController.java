package fr.afpa.choral_riff.controllers;

import fr.afpa.choral_riff.entity.Role;
import fr.afpa.choral_riff.services.UtilisateurEnsembleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/utilisateur-ensembles")
public class UtilisateurEnsembleController {

    private final UtilisateurEnsembleService utilisateurEnsembleService;

    public UtilisateurEnsembleController(
            UtilisateurEnsembleService utilisateurEnsembleService) {
        this.utilisateurEnsembleService = utilisateurEnsembleService;
    }

    /**
     * Permet à un OWNER de modifier le rôle d'un membre
     * de son ensemble.
     */
  @PutMapping("/role")
public ResponseEntity<?> changerRole(
        @RequestParam Long acteurId,
        @RequestParam Long cibleId,
        @RequestParam Long ensembleId,
        @RequestParam Role nouveauRole) {

    try {
        utilisateurEnsembleService.changerRole(
                acteurId,
                cibleId,
                ensembleId,
                nouveauRole
        );

        return ResponseEntity.ok(
                java.util.Map.of(
                        "message", "Rôle modifié avec succès",
                        "utilisateurId", cibleId,
                        "ensembleId", ensembleId,
                        "nouveauRole", nouveauRole.name()
                )
        );

    } catch (SecurityException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(java.util.Map.of(
                        "message", e.getMessage()
                ));

    } catch (RuntimeException e) {
        return ResponseEntity.notFound()
                .build();
    }
}


@GetMapping("/ensemble/{ensembleId}/membres")
public ResponseEntity<?> getMembresDeLEnsemble(
        @PathVariable Long ensembleId) {

    try {
        return ResponseEntity.ok(
                utilisateurEnsembleService.getMembresDeLEnsemble(ensembleId)
        );
    } catch (RuntimeException e) {
        return ResponseEntity.notFound().build();
    }
}

@DeleteMapping("/membre")
public ResponseEntity<?> retirerMembre(
        @RequestParam Long acteurId,
        @RequestParam Long cibleId,
        @RequestParam Long ensembleId) {

    try {

        utilisateurEnsembleService.retirerUtilisateurDeLEnsemble(
                acteurId,
                cibleId,
                ensembleId
        );

        return ResponseEntity.ok(
                java.util.Map.of(
                        "message", "Membre retiré de l'ensemble",
                        "utilisateurId", cibleId,
                        "ensembleId", ensembleId
                )
        );

    } catch (SecurityException e) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(java.util.Map.of(
                        "message", e.getMessage()
                ));

    } catch (RuntimeException e) {

        return ResponseEntity.notFound().build();
    }
}



}

