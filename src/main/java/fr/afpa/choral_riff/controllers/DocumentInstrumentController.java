package fr.afpa.choral_riff.controllers;

import fr.afpa.choral_riff.dto.InstrumentDto;
import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.mapper.InstrumentMapper;
import fr.afpa.choral_riff.services.DocumentInstrumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;

import java.util.Set;

@RestController
@RequestMapping("/api/document-instruments")
public class DocumentInstrumentController {

    private final DocumentInstrumentService documentInstrumentService;
    private final InstrumentMapper instrumentMapper;
    private final UtilisateurRepository utilisateurRepository;

    public DocumentInstrumentController(DocumentInstrumentService documentInstrumentService,
            InstrumentMapper instrumentMapper,
            UtilisateurRepository utilisateurRepository) { 
        this.documentInstrumentService = documentInstrumentService;
        this.instrumentMapper = instrumentMapper;
        this.utilisateurRepository = utilisateurRepository; 
    }

    /**
     * Ajouter un instrument à un document
     */
    // @PostMapping("/add")
    // public ResponseEntity<Void> addInstrumentToDocument(
    // @RequestParam Long documentId,
    // @RequestParam Long instrumentId) {

    // documentInstrumentService.addInstrumentToDocument(documentId, instrumentId);
    // return ResponseEntity.ok().build();
    // }

    // @PostMapping("/add")
    // public ResponseEntity<Void> addInstrumentToDocument(
    //         @RequestParam Long documentId,
    //         @RequestParam Long instrumentId) {

    //     // 🔹 Récupérer un utilisateur de test
    //     // Par exemple, le premier utilisateur de la base (ou ID fixe)
    //     Utilisateur utilisateur = utilisateurRepository.findById(1L)
    //             .orElseThrow(() -> new RuntimeException("Utilisateur de test non trouvé"));

    //     // Appel du service avec l'utilisateur
    //     documentInstrumentService.addInstrumentToDocument(documentId, instrumentId, utilisateur);

    //     return ResponseEntity.ok().build();
    // }
@PostMapping("/add")
public ResponseEntity<Void> addInstrumentToDocument(
        @RequestParam Long documentId,
        @RequestParam Long instrumentId,
        @RequestParam Long userId  // <-- ID de l'utilisateur connecté
) {
    Utilisateur utilisateur = utilisateurRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + userId));

    // Appel du service qui va vérifier si l'utilisateur peut ajouter l'instrument
    documentInstrumentService.addInstrumentToDocument(documentId, instrumentId, utilisateur);

    return ResponseEntity.ok().build();
}

    

    /**
     * Supprimer un instrument d’un document
     */
    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeInstrumentFromDocument(
            @RequestParam Long documentId,
            @RequestParam Long instrumentId) {

        documentInstrumentService.removeInstrumentFromDocument(documentId, instrumentId);
        return ResponseEntity.ok().build();
    }

    /**
     * Récupérer tous les instruments d’un document
     */
    @GetMapping("/{documentId}/instruments")
    public ResponseEntity<Set<InstrumentDto>> getInstrumentsByDocument(@PathVariable Long documentId) {
        Set<InstrumentDto> instruments = documentInstrumentService.getInstrumentsByDocument(documentId,
                instrumentMapper);
        return ResponseEntity.ok(instruments);
    }
}
