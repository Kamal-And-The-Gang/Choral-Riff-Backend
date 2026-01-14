
package fr.afpa.choral_riff.controllers;

import fr.afpa.choral_riff.dto.DocumentDto;
import fr.afpa.choral_riff.dto.InstrumentDto;
import fr.afpa.choral_riff.services.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    /**
     * Injection du service DocumentService via le constructeur.
     * Permet au controller d'appeler la logique métier.
     */
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    /*
     * ============================================================
     * UPLOAD DE FICHIERS
     * ============================================================
     */

    /**
     * POST /api/documents/upload
     * Upload d’un fichier pour un document.
     * Nécessite les informations suivantes :
     * - file : le fichier à uploader
     * - type : type du document (ex : partition, audio)
     * - format : format du document (ex : pdf, mp3)
     * - morceauId : ID du morceau auquel rattacher le document
     * - utilisateurId : ID de l'utilisateur qui upload
     */
    @PostMapping("/upload")
    public ResponseEntity<DocumentDto> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type,
            @RequestParam("format") String format,
            @RequestParam("morceauId") Long morceauId,
            @RequestParam("utilisateurId") Long utilisateurId) throws IOException {

        DocumentDto created = documentService.upload(file, type, format, morceauId, utilisateurId);
        return ResponseEntity.ok(created);
    }

    /*
     * ============================================================
     * CRÉATION D’UN DOCUMENT SANS FICHIER (JSON PUR)
     * ============================================================
     */

    /**
     * POST /api/documents
     * Crée un document uniquement via JSON.
     */
    @PostMapping
    public ResponseEntity<DocumentDto> createDocument(@RequestBody DocumentDto documentDto) {
        DocumentDto created = documentService.create(documentDto);
        return ResponseEntity.ok(created);
    }

    /*
     * ============================================================
     * RÉCUPÉRATIONS DE DOCUMENTS
     * ============================================================
     */

    /**
     * GET /api/documents
     * Retourne tous les documents.
     */
    @GetMapping
    public ResponseEntity<List<DocumentDto>> getAllDocuments() {
        List<DocumentDto> documents = documentService.getAll();
        return ResponseEntity.ok(documents);
    }

    /**
     * GET /api/documents/{id}
     * Retourne un document par son ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DocumentDto> getDocumentById(@PathVariable Long id) {
        DocumentDto document = documentService.getById(id);
        return ResponseEntity.ok(document);
    }

    /**
     * GET /api/documents/morceau/{morceauId}
     * Retourne tous les documents liés à un morceau.
     */
    @GetMapping("/morceau/{morceauId}")
    public ResponseEntity<List<DocumentDto>> getDocumentsByMorceau(@PathVariable Long morceauId) {
        List<DocumentDto> documents = documentService.getDocumentsByMorceauId(morceauId);
        return ResponseEntity.ok(documents);
    }

    /**
     * GET /api/documents/utilisateur/{utilisateurId}
     * Retourne tous les documents uploadés par un utilisateur.
     */
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<DocumentDto>> getDocumentsByUtilisateur(@PathVariable Long utilisateurId) {
        List<DocumentDto> documents = documentService.getDocumentsByUtilisateurId(utilisateurId);
        return ResponseEntity.ok(documents);
    }

    /**
     * GET /api/documents/ensemble/{ensembleId}
     * Retourne tous les documents liés à un ensemble.
     */
    @GetMapping("/ensemble/{ensembleId}")
    public ResponseEntity<List<DocumentDto>> getDocumentsByEnsemble(@PathVariable Long ensembleId) {
        List<DocumentDto> documents = documentService.getDocumentsByEnsembleId(ensembleId);
        return ResponseEntity.ok(documents);
    }

    /*
     * ============================================================
     * SUPPRESSION
     * ============================================================
     */

    /**
     * DELETE /api/documents/{id}
     * Supprime un document par son ID.
     * Renvoie HTTP 204 (No Content) si succès.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /*
     * ============================================================
     * AJOUT D’INSTRUMENTS À UN DOCUMENT
     * ============================================================
     */

    /**
     * POST /api/documents/{documentId}/instruments
     * Ajoute un instrument existant à un document.
     * 
     * @param instrumentId : ID de l'instrument à ajouter
     */
    @PostMapping("/{documentId}/instruments")
    public ResponseEntity<DocumentDto> addInstrumentToDocument(
            @PathVariable Long documentId,
            @RequestParam Long instrumentId) {

        DocumentDto updatedDocument = documentService.addInstrument(documentId, instrumentId);
        return ResponseEntity.ok(updatedDocument);
    }

    /**
     * GET /api/documents/{documentId}/instruments
     * Liste tous les instruments liés à un document.
     */
    @GetMapping("/{documentId}/instruments")
    public ResponseEntity<List<InstrumentDto>> getInstrumentsByDocument(@PathVariable Long documentId) {
        List<InstrumentDto> instruments = documentService.getInstrumentsByDocument(documentId);
        return ResponseEntity.ok(instruments);
    }

}
