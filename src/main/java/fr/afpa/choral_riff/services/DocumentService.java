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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service

public class DocumentService {

        private final DocumentRepository documentRepository;
        private final MorceauRepository morceauRepository;
        private final UtilisateurRepository utilisateurRepository;
        private final DocumentMapper documentMapper;
/**
 * 
 * @param documentRepository
 * @param morceauRepository
 * @param utilisateurRepository
 * @param documentMapper
 */
        public DocumentService(DocumentRepository documentRepository,
                        MorceauRepository morceauRepository,
                        UtilisateurRepository utilisateurRepository,
                        DocumentMapper documentMapper) {
                this.documentRepository = documentRepository;
                this.morceauRepository = morceauRepository;
                this.utilisateurRepository = utilisateurRepository;
                this.documentMapper = documentMapper;
        }

        public DocumentDto create(DocumentDto dto) {
                Document entity = documentMapper.toEntity(dto);

                // Récupérer et associer l'utilisateur
                Utilisateur utilisateur = utilisateurRepository.findById(dto.utilisateurId())
                                .orElseThrow(() -> new NoSuchElementException(
                                                "Utilisateur non trouvé avec id " + dto.utilisateurId()));
                entity.setUtilisateur(utilisateur);

                // Récupérer et associer le morceau
                Morceau morceau = morceauRepository.findById(dto.morceauId())
                                .orElseThrow(() -> new NoSuchElementException(
                                                "Morceau non trouvé avec id " + dto.morceauId()));
                entity.setMorceau(morceau);

                Document saved = documentRepository.save(entity);
                return documentMapper.toDto(saved);
        }

        /**
         * @return List<DocumentDto>
         */
        // Tous les documents
        public List<DocumentDto> getAll() {
                return documentRepository.findAll().stream()
                                .map(documentMapper::toDto)
                                .collect(Collectors.toList());
        }

        /**
         * @param id
         * @return DocumentDto
         */
        // Un document par ID
        public DocumentDto getById(Long id) {
                Document document = documentRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Document non trouvé avec l'ID : " + id));
                return documentMapper.toDto(document);
        }

        /**
         * @param morceauId
         * @return List<DocumentDto>
         */
        public List<DocumentDto> getDocumentsByMorceauId(Long morceauId) {
                Morceau morceau = morceauRepository.findById(morceauId)
                                .orElseThrow(() -> new RuntimeException("Morceau non trouvé avec l'ID : " + morceauId));

                List<Document> documents = documentRepository.findByMorceau(morceau);

                return documents.stream()
                                .map(documentMapper::toDto)
                                .collect(Collectors.toList());
        }

        /**
         * @param utilisateurId
         * @return List<DocumentDto>
         */
        public List<DocumentDto> getDocumentsByUtilisateurId(Long utilisateurId) {
                Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                                .orElseThrow(() -> new RuntimeException(
                                                "Utilisateur non trouvé avec l'ID : " + utilisateurId));

                List<Document> documents = documentRepository.findByUtilisateur(utilisateur);

                return documents.stream()
                                .map(documentMapper::toDto)
                                .collect(Collectors.toList());
        }

        /**
         * @param ensembleId
         * @return List<DocumentDto>
         */
        public List<DocumentDto> getDocumentsByEnsembleId(Long ensembleId) {
                // Récupérer tous les morceaux liés à l'ensemble
                List<Morceau> morceaux = morceauRepository.findByEnsembleId(ensembleId);

                List<Document> documents = morceaux.stream()
                                .flatMap(morceau -> documentRepository.findByMorceau(morceau).stream())
                                .collect(Collectors.toList());

                return documents.stream()
                                .map(documentMapper::toDto)
                                .collect(Collectors.toList());
        }

}