package fr.afpa.choral_riff.controllers;

import fr.afpa.choral_riff.dto.DocumentDto;
import fr.afpa.choral_riff.services.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    // GET /api/documents : tous les documents
    @GetMapping
    public ResponseEntity<List<DocumentDto>> getAllDocuments() {
        List<DocumentDto> documents = documentService.getAll();
        return ResponseEntity.ok(documents);
    }

    // GET /api/documents/{id} : document par ID
    @GetMapping("/{id}")
    public ResponseEntity<DocumentDto> getDocumentById(@PathVariable Long id) {
        DocumentDto document = documentService.getById(id);
        return ResponseEntity.ok(document);
    }

    // GET /api/documents/morceau/{morceauId} : documents par morceau
    @GetMapping("/morceau/{morceauId}")
    public ResponseEntity<List<DocumentDto>> getDocumentsByMorceau(@PathVariable Long morceauId) {
        List<DocumentDto> documents = documentService.getDocumentsByMorceauId(morceauId);
        return ResponseEntity.ok(documents);
    }

    // GET /api/documents/utilisateur/{utilisateurId} : documents par utilisateur
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<DocumentDto>> getDocumentsByUtilisateur(@PathVariable Long utilisateurId) {
        List<DocumentDto> documents = documentService.getDocumentsByUtilisateurId(utilisateurId);
        return ResponseEntity.ok(documents);
    }

    // GET /api/documents/ensemble/{ensembleId} : documents par ensemble
    @GetMapping("/ensemble/{ensembleId}")
    public ResponseEntity<List<DocumentDto>> getDocumentsByEnsemble(@PathVariable Long ensembleId) {
        List<DocumentDto> documents = documentService.getDocumentsByEnsembleId(ensembleId);
        return ResponseEntity.ok(documents);
    }

}
