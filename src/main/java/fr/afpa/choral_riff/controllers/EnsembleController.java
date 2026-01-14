
package fr.afpa.choral_riff.controllers;

import fr.afpa.choral_riff.dto.EnsembleDto;
import fr.afpa.choral_riff.services.EnsembleService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ensembles")
public class EnsembleController {

    private final EnsembleService ensembleService;

    public EnsembleController(EnsembleService ensembleService) {
        this.ensembleService = ensembleService;
    }

    // Récupérer tous les ensembles pour un utilisateur
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EnsembleDto>> getEnsemblesByUser(@PathVariable Long userId) {
        List<EnsembleDto> ensembles = ensembleService.getAllForUser(userId);
        return ResponseEntity.ok(ensembles);
    }

    // Récupérer tous les ensembles
    @GetMapping
    public ResponseEntity<List<EnsembleDto>> getAllEnsembles() {
        // Ici pas besoin de userId spécifique
        return ResponseEntity.ok(ensembleService.getAll());
    }

    // Récupérer un ensemble par ID pour un utilisateur spécifique
    @GetMapping("/{id}")
    public ResponseEntity<EnsembleDto> getEnsembleById(
            @PathVariable Long id,
            @RequestParam Long userId) { // userId obligatoire
        try {
            return ResponseEntity.ok(ensembleService.getByIdForUser(id, userId));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Créer un nouvel ensemble
    @PostMapping
    public ResponseEntity<EnsembleDto> createEnsemble(
            @RequestBody EnsembleDto dto,
            @RequestParam Long userId) { // userId obligatoire
        EnsembleDto created = ensembleService.create(dto, userId);
        return ResponseEntity.ok(created);
    }

    // Mettre à jour un ensemble
    @PutMapping("/{id}")
    public ResponseEntity<EnsembleDto> updateEnsemble(
            @PathVariable Long id,
            @RequestParam Long userId, // userId obligatoire
            @RequestBody EnsembleDto dto) {
        // Vérification des droits
        if (!ensembleService.hasRights(userId, id)) {
            return ResponseEntity.status(403).build(); // Forbidden
        }
        try {
            return ResponseEntity.ok(ensembleService.update(id, dto, userId));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Supprimer un ensemble
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnsemble(
            @PathVariable Long id,
            @RequestParam Long userId) {

        try {
            ensembleService.delete(id, userId);
            return ResponseEntity.noContent().build();

        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();

        } catch (IllegalStateException | SecurityException e) {
            // uniquement les erreurs de droits
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // Récupérer le nombre de membres d'un ensemble
    @GetMapping("/{id}/members/count")
    public ResponseEntity<Integer> getNombreMembres(@PathVariable Long id) {
        try {
            int count = ensembleService.getNombreMembres(id);
            return ResponseEntity.ok(count);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
