package fr.afpa.choral_riff.controllers;

import fr.afpa.choral_riff.dto.DocumentDto;
import fr.afpa.choral_riff.dto.InstrumentDto;
import fr.afpa.choral_riff.services.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
/**
 * 
 */
@RequestMapping("/api/documents")

public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    // === UPLOAD de fichier ===
    @PostMapping("/upload")
    public ResponseEntity<DocumentDto> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type,
            @RequestParam("format") String format,
            @RequestParam("morceauId") Long morceauId,
            @RequestParam("utilisateurId") Long utilisateurId
    ) throws IOException {
        DocumentDto created = documentService.upload(file, type, format, morceauId, utilisateurId);
        return ResponseEntity.ok(created);
    }

    // === Création sans upload (JSON pur) ===
    @PostMapping
    public ResponseEntity<DocumentDto> createDocument(@RequestBody DocumentDto documentDto) {
        DocumentDto created = documentService.create(documentDto);
        return ResponseEntity.ok(created);
    }
    @GetMapping
    public ResponseEntity<List<DocumentDto>> getAllDocuments() {
        List<DocumentDto> documents = documentService.getAll();
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentDto> getDocumentById(@PathVariable Long id) {
        DocumentDto document = documentService.getById(id);
        return ResponseEntity.ok(document);
    }

    @GetMapping("/morceau/{morceauId}")
    public ResponseEntity<List<DocumentDto>> getDocumentsByMorceau(@PathVariable Long morceauId) {
        List<DocumentDto> documents = documentService.getDocumentsByMorceauId(morceauId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<DocumentDto>> getDocumentsByUtilisateur(@PathVariable Long utilisateurId) {
        List<DocumentDto> documents = documentService.getDocumentsByUtilisateurId(utilisateurId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/ensemble/{ensembleId}")
    public ResponseEntity<List<DocumentDto>> getDocumentsByEnsemble(@PathVariable Long ensembleId) {
        List<DocumentDto> documents = documentService.getDocumentsByEnsembleId(ensembleId);
        return ResponseEntity.ok(documents);
    }
@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
    documentService.delete(id);
    return ResponseEntity.noContent().build(); // renvoie un code HTTP 204 (No Content)
}

// @PostMapping("/{documentId}/instruments")
// public ResponseEntity<Void> addInstrumentToDocument(
//         @PathVariable Long documentId,
//         @RequestParam Long instrumentId) {
//     documentService.addInstrument(documentId, instrumentId);
//     return ResponseEntity.ok().build();
// }

// @PostMapping("/{documentId}/instruments")
// public ResponseEntity<DocumentDto> addInstrumentToDocument(
//         @PathVariable Long documentId,
//         @RequestBody String nomInstrument) { // récupère le nom
//     DocumentDto updatedDocument = documentService.addInstrument(documentId, nomInstrument);
//     return ResponseEntity.ok(updatedDocument);
// }
// === Ajout d’un instrument à un document ===
    @PostMapping("/{documentId}/instruments")
    public ResponseEntity<DocumentDto> addInstrumentToDocument(
            @PathVariable Long documentId,
            @RequestParam Long instrumentId) {

        DocumentDto updatedDocument = documentService.addInstrument(documentId, instrumentId);
        return ResponseEntity.ok(updatedDocument);
    }




@GetMapping("/{documentId}/instruments")
public ResponseEntity<List<InstrumentDto>> getInstrumentsByDocument(@PathVariable Long documentId) {
    List<InstrumentDto> instruments = documentService.getInstrumentsByDocument(documentId);
    return ResponseEntity.ok(instruments);
}




}
