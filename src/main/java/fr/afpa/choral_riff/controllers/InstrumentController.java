
package fr.afpa.choral_riff.controllers;

import fr.afpa.choral_riff.dto.InstrumentDto;
import fr.afpa.choral_riff.services.InstrumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controller REST pour gérer les instruments.
 * Toutes les routes sont préfixées par "/api/instruments".
 */

@RestController
@RequestMapping("/api/instruments")
public class InstrumentController {

    private final InstrumentService instrumentService;

    /**
     * Injection du service InstrumentService via le constructeur.
     * Permet au controller d'appeler la logique métier.
     */
    public InstrumentController(InstrumentService instrumentService) {
        this.instrumentService = instrumentService;
    }

    /*
     * ============================================================
     * MÉTHODES POUR LES ENDPOINTS
     * ============================================================
     */

    /**
     * GET /api/instruments
     * Retourne la liste de tous les instruments sous forme de DTO.
     */

    @GetMapping
    public ResponseEntity<List<InstrumentDto>> getAllInstruments() {
        return ResponseEntity.ok(instrumentService.getAll());
    }

    /**
     * GET /api/instruments/{id}
     * Retourne un instrument par son ID.
     * Si l'instrument n'existe pas → retourne 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<InstrumentDto> getInstrumentById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(instrumentService.getById(id));
        } catch (RuntimeException e) {
            // Retourne 404 si l'instrument n'existe pas
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/instruments/{id}
     * Supprime un instrument par son ID.
     * Retourne 204 No Content si suppression réussie.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstrument(@PathVariable Long id) {
        try {
            instrumentService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            // Retourne 404 si l'instrument n'existe pas
            return ResponseEntity.notFound().build();
        }
    }

}
