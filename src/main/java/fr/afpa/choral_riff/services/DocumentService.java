package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.dto.DocumentDto;
import fr.afpa.choral_riff.dto.InstrumentDto;
import fr.afpa.choral_riff.entity.Document;
import fr.afpa.choral_riff.entity.Instrument;
import fr.afpa.choral_riff.entity.Morceau;
import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.mapper.DocumentMapper;
import fr.afpa.choral_riff.repositories.DocumentRepository;
import fr.afpa.choral_riff.repositories.InstrumentRepository;
import fr.afpa.choral_riff.repositories.MorceauRepository;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.access.AccessDeniedException;

/**
 * Service pour gérer les documents liés aux morceaux et utilisateurs.
 * 
 * Fonctionnalités :
 * - Upload de documents
 * - Récupération de documents par ID, morceau, utilisateur ou ensemble
 * - Création et suppression de documents
 *
 * Dépendances :
 * - DocumentRepository
 * - MorceauRepository
 * - UtilisateurRepository
 * - DocumentMapper
 */

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final MorceauRepository morceauRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final DocumentMapper documentMapper;
    private final InstrumentRepository instrumentRepository;
    private final UtilisateurEnsembleService utilisateurEnsembleService;
    private static final String UPLOAD_DIR = "uploads/documents/";

    public DocumentService(DocumentRepository documentRepository,
            MorceauRepository morceauRepository,
            UtilisateurRepository utilisateurRepository,
            DocumentMapper documentMapper,
            InstrumentRepository instrumentRepository,
            UtilisateurEnsembleService utilisateurEnsembleService) {
        this.documentRepository = documentRepository;
        this.morceauRepository = morceauRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.documentMapper = documentMapper;
        this.instrumentRepository = instrumentRepository;
        this.utilisateurEnsembleService = utilisateurEnsembleService;
    }

    /**
     * Upload un fichier et l'associe à un morceau et un utilisateur.
     *
     * @param file          Le fichier à uploader
     * @param type          Le type de document
     * @param format        Le format du document
     * @param morceauId     L'ID du morceau
     * @param utilisateurId L'ID de l'utilisateur
     * @return Le DTO du document enregistré
     * @throws IOException Si une erreur survient lors de l'écriture du fichier
     */

    public DocumentDto upload(MultipartFile file, String type, String format, Long morceauId, Long utilisateurId)
            throws IOException {

        // 1) Validation du fichier
        if (file.isEmpty()) {
            throw new RuntimeException("Fichier vide");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("Fichier trop volumineux");
        }

        // 2) Calcul du type réel
        String realType = file.getContentType(); // ex: image/png
        String realFormat = Optional.ofNullable(file.getOriginalFilename())
                .filter(n -> n.contains("."))
                .map(n -> n.substring(n.lastIndexOf(".") + 1))
                .orElse("unknown");

        // 3) Tu peux quand même ignorer les valeurs passées
        type = realType;
        format = realFormat;

        // 4) Sauvegarde fichier
        Files.createDirectories(Paths.get(UPLOAD_DIR));
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR, fileName);
        Files.copy(file.getInputStream(), filePath);

        Morceau morceau = morceauRepository.findById(morceauId)
                .orElseThrow(() -> new RuntimeException("Morceau non trouvé avec l'ID : " + morceauId));

        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + utilisateurId));

        Document document = new Document();
        document.setType(type);
        document.setFormat(format);
        document.setDateAjout(LocalDate.now());
        document.setUrlFichier("/" + UPLOAD_DIR + fileName);
        document.setNomOriginal(file.getOriginalFilename()); // <-- ici le vrai nom
        document.setMorceau(morceau);
        document.setUtilisateur(utilisateur);

        // <-- ICI on met le nom original
document.setNomOriginal(file.getOriginalFilename());

        Document saved = documentRepository.save(document);
        return documentMapper.toDto(saved);
    }

    public List<DocumentDto> getAll() {
        return documentRepository.findAll().stream()
                .map(documentMapper::toDto)
                .collect(Collectors.toList());
    }

    public DocumentDto getById(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document non trouvé avec l'ID : " + id));
        return documentMapper.toDto(document);
    }

//     public DocumentDto create(DocumentDto documentDto) {
//         Document document = documentMapper.toEntity(documentDto);

//         if (documentDto.morceauId() != null) {
//             Morceau morceau = morceauRepository.findById(documentDto.morceauId())
//                     .orElseThrow(
//                             () -> new RuntimeException("Morceau non trouvé avec l'ID : " + documentDto.morceauId()));
//             document.setMorceau(morceau);
//         }

//         if (documentDto.utilisateurId() != null) {
//             Utilisateur utilisateur = utilisateurRepository.findById(documentDto.utilisateurId())
//                     .orElseThrow(() -> new RuntimeException(
//                             "Utilisateur non trouvé avec l'ID : " + documentDto.utilisateurId()));
//             document.setUtilisateur(utilisateur);
//         }

//         Document savedDocument = documentRepository.save(document);
//         return documentMapper.toDto(savedDocument);
//     }

public DocumentDto create(DocumentDto documentDto) {

    Morceau morceau = morceauRepository.findById(documentDto.morceauId())
            .orElseThrow(() ->
                    new RuntimeException("Morceau non trouvé avec l'ID : " + documentDto.morceauId()));

    Long ensembleId = morceau.getEnsemble().getId();

    boolean adminOuModerateur = utilisateurEnsembleService.utilisateurAutorise(
            documentDto.utilisateurId(),
            ensembleId,
            List.of("ADMIN", "MODERATEUR"));

    if (!adminOuModerateur) {
        throw new AccessDeniedException(
                "Vous n'avez pas le droit d'ajouter un document");
    }

    Document document = documentMapper.toEntity(documentDto);

    document.setMorceau(morceau);

    Utilisateur utilisateur = utilisateurRepository.findById(documentDto.utilisateurId())
            .orElseThrow(() ->
                    new RuntimeException("Utilisateur non trouvé avec l'ID : " + documentDto.utilisateurId()));

    document.setUtilisateur(utilisateur);

    Document savedDocument = documentRepository.save(document);

    return documentMapper.toDto(savedDocument);
}

    public List<DocumentDto> getDocumentsByMorceauId(Long morceauId) {
        Morceau morceau = morceauRepository.findById(morceauId)
                .orElseThrow(() -> new RuntimeException("Morceau non trouvé avec l'ID : " + morceauId));

        List<Document> documents = documentRepository.findByMorceau(morceau);

        return documents.stream()
                .map(documentMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<DocumentDto> getDocumentsByUtilisateurId(Long utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + utilisateurId));

        List<Document> documents = documentRepository.findByUtilisateur(utilisateur);

        return documents.stream()
                .map(documentMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<DocumentDto> getDocumentsByEnsembleId(Long ensembleId) {
        List<Morceau> morceaux = morceauRepository.findByEnsembleId(ensembleId);

        List<Document> documents = morceaux.stream()
                .flatMap(morceau -> documentRepository.findByMorceau(morceau).stream())
                .collect(Collectors.toList());

        return documents.stream()
                .map(documentMapper::toDto)
                .collect(Collectors.toList());
    }

//     public void delete(Long documentId) {
//         Document document = documentRepository.findById(documentId)
//                 .orElseThrow(() -> new EntityNotFoundException("Document not found with id: " + documentId));

//         documentRepository.delete(document);
//     }

public void delete(Long documentId, Long userId) {

    Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new EntityNotFoundException(
                    "Document not found with id: " + documentId));

    Morceau morceau = document.getMorceau();
    Long ensembleId = morceau.getEnsemble().getId();

    boolean adminOuModerateur = utilisateurEnsembleService.utilisateurAutorise(
            userId,
            ensembleId,
            List.of("ADMIN", "MODERATEUR"));

    boolean createur = document.getUtilisateur().getId().equals(userId);

    if (!adminOuModerateur && !createur) {
        throw new AccessDeniedException(
                "Vous n'avez pas le droit de supprimer ce document");
    }

    documentRepository.delete(document);
}

    public List<InstrumentDto> getInstrumentsByDocument(Long documentId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getInstrumentsByDocument'");
    }

    public DocumentDto addInstrument(Long documentId, Long instrumentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document non trouvé avec l'ID : " + documentId));

        Instrument instrument = instrumentRepository.findById(instrumentId)
                .orElseThrow(() -> new RuntimeException("Instrument non trouvé avec l'ID : " + instrumentId));

        document.addInstrument(instrument);
        Document savedDocument = documentRepository.save(document);

        return documentMapper.toDto(savedDocument);
    }

}
