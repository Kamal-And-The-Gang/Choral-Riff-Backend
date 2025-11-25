// package fr.afpa.choral_riff.controllers;

// import fr.afpa.choral_riff.dto.InstrumentDto;
// import fr.afpa.choral_riff.services.InstrumentService;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @RestController
// @RequestMapping("/api/instruments")
// public class InstrumentController {

//     private final InstrumentService instrumentService;

//     public InstrumentController(InstrumentService instrumentService) {
//         this.instrumentService = instrumentService;
//     }

//     // Récupérer tous les instruments
//     @GetMapping
//     public ResponseEntity<List<InstrumentDto>> getAllInstruments() {
//         return ResponseEntity.ok(instrumentService.getAll());
//     }

//     // Récupérer un instrument par ID
//     @GetMapping("/{id}")
//     public ResponseEntity<InstrumentDto> getInstrumentById(@PathVariable Long id) {
//         try {
//             return ResponseEntity.ok(instrumentService.getById(id));
//         } catch (RuntimeException e) {
//             return ResponseEntity.notFound().build();
//         }
//     }

//     // Créer un nouvel instrument (optionnellement lié à un ensemble)
//     @PostMapping
//     public ResponseEntity<InstrumentDto> createInstrument(
//             @RequestBody InstrumentDto dto,
//             @RequestParam(required = false) Long ensembleId) {
//         return ResponseEntity.ok(instrumentService.create(dto, ensembleId));
//     }

//     // Mettre à jour un instrument
//     @PutMapping("/{id}")
//     public ResponseEntity<InstrumentDto> updateInstrument(
//             @PathVariable Long id,
//             @RequestBody InstrumentDto dto,
//             @RequestParam(required = false) Long ensembleId) {
//         try {
//             return ResponseEntity.ok(instrumentService.update(id, dto, ensembleId));
//         } catch (RuntimeException e) {
//             return ResponseEntity.notFound().build();
//         }
//     }

//     // Supprimer un instrument
//     @DeleteMapping("/{id}")
//     public ResponseEntity<Void> deleteInstrument(@PathVariable Long id) {
//         try {
//             instrumentService.delete(id);
//             return ResponseEntity.noContent().build();
//         } catch (RuntimeException e) {
//             return ResponseEntity.notFound().build();
//         }
//     }

//     @PostMapping("/{ensembleId}/instruments")
//     public ResponseEntity<InstrumentDto> addInstrument(
//             @PathVariable Long ensembleId,
//             @RequestParam Long utilisateurId, // reçu depuis le front
//             @RequestBody InstrumentDto dto) {

//         InstrumentDto saved = instrumentService.addInstrumentToEnsemble(ensembleId, utilisateurId, dto);
//         return ResponseEntity.ok(saved);
//     }

//     @PostMapping("/{ensembleId}/add")
//     public ResponseEntity<InstrumentDto> addToEnsemble(
//             @PathVariable Long ensembleId,
//             @RequestParam Long utilisateurId,
//             @RequestBody InstrumentDto dto) {

//         // Vérifie que l'utilisateur est admin ou chef de chœur(modérateur)
//         InstrumentDto saved = instrumentService.addInstrumentToEnsemble(ensembleId, utilisateurId, dto);
//         return ResponseEntity.ok(saved);
//     }

// }

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

    /* ============================================================
       MÉTHODES POUR LES ENDPOINTS
       ============================================================ */

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
     * POST /api/instruments
     * Crée un nouvel instrument.
     * Paramètre optionnel ensembleId pour rattacher l’instrument à un ensemble.
     */
    @PostMapping
    public ResponseEntity<InstrumentDto> createInstrument(
            @RequestBody InstrumentDto dto,               // Corps de la requête JSON
            @RequestParam(required = false) Long ensembleId) { // Paramètre optionnel
        return ResponseEntity.ok(instrumentService.create(dto, ensembleId));
    }

    /**
     * PUT /api/instruments/{id}
     * Met à jour un instrument existant.
     * Peut mettre à jour le lien avec un ensemble si ensembleId est fourni.
     */
    @PutMapping("/{id}")
    public ResponseEntity<InstrumentDto> updateInstrument(
            @PathVariable Long id,                       // ID de l'instrument à modifier
            @RequestBody InstrumentDto dto,              // Données mises à jour
            @RequestParam(required = false) Long ensembleId) {
        try {
            return ResponseEntity.ok(instrumentService.update(id, dto, ensembleId));
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
     * POST /api/instruments/{ensembleId}/instruments
     * Ajoute un instrument à un ensemble.
     * Nécessite l'ID de l'utilisateur pour vérifier les droits.
     */
    @PostMapping("/{ensembleId}/instruments")
    public ResponseEntity<InstrumentDto> addInstrument(
            @PathVariable Long ensembleId,   // Ensemble cible
            @RequestParam Long utilisateurId, // ID de l'utilisateur qui fait l'action
            @RequestBody InstrumentDto dto) {

        InstrumentDto saved = instrumentService.addInstrumentToEnsemble(ensembleId, utilisateurId, dto);
        return ResponseEntity.ok(saved);
    }

    /**
     * POST /api/instruments/{ensembleId}/add
     * Variante pour ajouter un instrument à un ensemble.
     * Même logique que la précédente → pourrait être fusionnée.
     */
    @PostMapping("/{ensembleId}/add")
    public ResponseEntity<InstrumentDto> addToEnsemble(
            @PathVariable Long ensembleId,
            @RequestParam Long utilisateurId,
            @RequestBody InstrumentDto dto) {

        // Vérifie que l'utilisateur a les droits (ADMIN ou MODERATEUR)
        InstrumentDto saved = instrumentService.addInstrumentToEnsemble(ensembleId, utilisateurId, dto);
        return ResponseEntity.ok(saved);
    }

}

