package fr.afpa.choral_riff.controllers;

import fr.afpa.choral_riff.dto.EnsembleDto;
import fr.afpa.choral_riff.services.EnsembleService;
import jakarta.persistence.EntityNotFoundException;
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

    // 28/10/2025

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EnsembleDto>> getEnsemblesByUser(@PathVariable Long userId) {
        List<EnsembleDto> ensembles = ensembleService.getAllForUser(userId);
        return ResponseEntity.ok(ensembles);
    }

    // Récupérer tous les ensembles
    @GetMapping
    public ResponseEntity<List<EnsembleDto>> getAllEnsembles() {
        return ResponseEntity.ok(ensembleService.getAll());
    }

    // Récupérer un ensemble par ID
    @GetMapping("/{id}")
    public ResponseEntity<EnsembleDto> getEnsembleById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ensembleService.getById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Créer un nouvel ensemble
    @PostMapping
    public ResponseEntity<EnsembleDto> createEnsemble(@RequestBody EnsembleDto dto, @RequestParam Long userId) {
        EnsembleDto created = ensembleService.create(dto, userId);
        return ResponseEntity.ok(created);
    }

    // Mettre à jour un ensemble
    @PutMapping("/{id}")
    public ResponseEntity<EnsembleDto> updateEnsemble(@PathVariable Long id, @RequestBody EnsembleDto dto) {
        try {
            return ResponseEntity.ok(ensembleService.update(id, dto));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Supprimer un ensemble
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnsemble(@PathVariable Long id) {
        try {
            ensembleService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
