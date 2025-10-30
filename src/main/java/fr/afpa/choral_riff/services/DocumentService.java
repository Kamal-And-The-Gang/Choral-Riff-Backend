package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.dto.DocumentDto;
import fr.afpa.choral_riff.entity.Document;
import fr.afpa.choral_riff.entity.Morceau;
import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.mapper.DocumentMapper;
import fr.afpa.choral_riff.repositories.DocumentRepository;
import fr.afpa.choral_riff.repositories.MorceauRepository;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final MorceauRepository morceauRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final DocumentMapper documentMapper;

    private static final String UPLOAD_DIR = "uploads/documents/";

    public DocumentService(DocumentRepository documentRepository,
                           MorceauRepository morceauRepository,
                           UtilisateurRepository utilisateurRepository,
                           DocumentMapper documentMapper) {
        this.documentRepository = documentRepository;
        this.morceauRepository = morceauRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.documentMapper = documentMapper;
    }

    public DocumentDto upload(MultipartFile file, String type, String format, Long morceauId, Long utilisateurId)
            throws IOException {

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
        document.setMorceau(morceau);
        document.setUtilisateur(utilisateur);

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

    public DocumentDto create(DocumentDto documentDto) {
        Document document = documentMapper.toEntity(documentDto);

        if (documentDto.morceauId() != null) {
            Morceau morceau = morceauRepository.findById(documentDto.morceauId())
                    .orElseThrow(() -> new RuntimeException("Morceau non trouvé avec l'ID : " + documentDto.morceauId()));
            document.setMorceau(morceau);
        }

        if (documentDto.utilisateurId() != null) {
            Utilisateur utilisateur = utilisateurRepository.findById(documentDto.utilisateurId())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + documentDto.utilisateurId()));
            document.setUtilisateur(utilisateur);
        }

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
}
