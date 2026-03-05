
package fr.afpa.choral_riff.controllers;

import fr.afpa.choral_riff.dto.InstrumentDto;
import fr.afpa.choral_riff.services.InstrumentService;
import fr.afpa.choral_riff.services.NotificationService;

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
    private final NotificationService notificationService; // Déclarer NotificationService

    /**
     * Injection des services InstrumentService et NotificationService via le
     * constructeur.
     * Permet au controller d'appeler la logique métier.
     */
    public InstrumentController(InstrumentService instrumentService, NotificationService notificationService) {
        this.instrumentService = instrumentService;
        this.notificationService = notificationService; // Assurez-vous que le service est injecté
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

    /**
     * POST /api/instruments/morceaux/{morceauId}/instruments/{instrumentId}
     * Ajoute un instrument à un morceau et envoie une notification.
     */
    // @PostMapping("/morceaux/{morceauId}/instruments/{instrumentId}")
    // public ResponseEntity<InstrumentDto> addInstrumentToMorceau(@PathVariable Long morceauId,
    //         @PathVariable Long instrumentId,
    //         @RequestParam Long utilisateurId) {

    //     // Ajouter l'instrument au morceau
    //     InstrumentDto instrumentDto = instrumentService.addInstrumentToMorceau(morceauId, instrumentId, utilisateurId);

    //     // Récupérer le nom de l'instrument ajouté
    //     String instrumentNom = instrumentDto.nom();

    //     // Appeler le service de notification pour envoyer l'alerte
    //     notificationService.notifyInstrumentAjoute(utilisateurId, instrumentId, morceauId, instrumentNom);

    //     // Retourner une réponse avec le DTO de l'instrument ajouté
    //     return ResponseEntity.ok(instrumentDto);
    // }
}
