package fr.afpa.choral_riff.controllers;

import fr.afpa.choral_riff.dto.InstrumentDto;
import fr.afpa.choral_riff.mapper.InstrumentMapper;
import fr.afpa.choral_riff.services.DocumentInstrumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/document-instruments")
public class DocumentInstrumentController {

    private final DocumentInstrumentService documentInstrumentService;
    private final InstrumentMapper instrumentMapper;

    public DocumentInstrumentController(DocumentInstrumentService documentInstrumentService,
                                        InstrumentMapper instrumentMapper) {
        this.documentInstrumentService = documentInstrumentService;
        this.instrumentMapper = instrumentMapper;
    }

    /**
     * Ajouter un instrument à un document
     */
    @PostMapping("/add")
    public ResponseEntity<Void> addInstrumentToDocument(
            @RequestParam Long documentId,
            @RequestParam Long instrumentId) {

        documentInstrumentService.addInstrumentToDocument(documentId, instrumentId);
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
        Set<InstrumentDto> instruments =
                documentInstrumentService.getInstrumentsByDocument(documentId, instrumentMapper);
        return ResponseEntity.ok(instruments);
    }
}
