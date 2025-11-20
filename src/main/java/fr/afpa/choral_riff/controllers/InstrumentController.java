package fr.afpa.choral_riff.controllers;

import fr.afpa.choral_riff.dto.InstrumentDto;
import fr.afpa.choral_riff.services.InstrumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instruments")
public class InstrumentController {

    private final InstrumentService instrumentService;

    public InstrumentController(InstrumentService instrumentService) {
        this.instrumentService = instrumentService;
    }

    // Récupérer tous les instruments
    @GetMapping
    public ResponseEntity<List<InstrumentDto>> getAllInstruments() {
        return ResponseEntity.ok(instrumentService.getAll());
    }

    // Récupérer un instrument par ID
    @GetMapping("/{id}")
    public ResponseEntity<InstrumentDto> getInstrumentById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(instrumentService.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Créer un nouvel instrument (optionnellement lié à un ensemble)
    @PostMapping
    public ResponseEntity<InstrumentDto> createInstrument(
            @RequestBody InstrumentDto dto,
            @RequestParam(required = false) Long ensembleId) {
        return ResponseEntity.ok(instrumentService.create(dto, ensembleId));
    }

    // Mettre à jour un instrument
    @PutMapping("/{id}")
    public ResponseEntity<InstrumentDto> updateInstrument(
            @PathVariable Long id,
            @RequestBody InstrumentDto dto,
            @RequestParam(required = false) Long ensembleId) {
        try {
            return ResponseEntity.ok(instrumentService.update(id, dto, ensembleId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Supprimer un instrument
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstrument(@PathVariable Long id) {
        try {
            instrumentService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{ensembleId}/instruments")
    public ResponseEntity<InstrumentDto> addInstrument(
            @PathVariable Long ensembleId,
            @RequestParam Long utilisateurId, // reçu depuis le front
            @RequestBody InstrumentDto dto) {

        InstrumentDto saved = instrumentService.addInstrumentToEnsemble(utilisateurId, ensembleId, dto);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/{ensembleId}/add")
    public ResponseEntity<InstrumentDto> addToEnsemble(
            @PathVariable Long ensembleId,
            @RequestParam Long utilisateurId,
            @RequestBody InstrumentDto dto) {

        // Vérifie que l'utilisateur est admin ou chef de chœur(modérateur)
        InstrumentDto saved = instrumentService.addInstrumentToEnsemble(ensembleId, utilisateurId, dto);
        return ResponseEntity.ok(saved);
    }

}
