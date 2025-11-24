package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.dto.InstrumentDto;
import fr.afpa.choral_riff.entity.Document;
import fr.afpa.choral_riff.entity.Instrument;
import fr.afpa.choral_riff.mapper.InstrumentMapper;
import fr.afpa.choral_riff.repositories.DocumentRepository;
import fr.afpa.choral_riff.repositories.InstrumentRepository;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DocumentInstrumentService {

    private final DocumentRepository documentRepository;
    private final InstrumentRepository instrumentRepository;

    public DocumentInstrumentService(DocumentRepository documentRepository,
                                     InstrumentRepository instrumentRepository) {
        this.documentRepository = documentRepository;
        this.instrumentRepository = instrumentRepository;
    }

    /**
     * Ajoute un instrument à un document
     */
    public void addInstrumentToDocument(Long documentId, Long instrumentId) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document non trouvé : " + documentId));

        Instrument instrument = instrumentRepository.findById(instrumentId)
            .orElseThrow(() -> new RuntimeException("Instrument non trouvé : " + instrumentId));

        // Utilise la méthode utilitaire de Document
        document.addInstrument(instrument);

        documentRepository.save(document);
    }

    /**
     * Récupère les instruments liés à un document sous forme de DTO
     */
    public Set<InstrumentDto> getInstrumentsByDocument(Long documentId, InstrumentMapper mapper) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document non trouvé : " + documentId));

        // Utilise la méthode utilitaire getInstruments() pour récupérer les instruments
        return document.getInstruments().stream()
            .map(mapper::toDto)
            .collect(Collectors.toSet());
    }

    /**
     * Supprime un instrument d’un document
     */
    public void removeInstrumentFromDocument(Long documentId, Long instrumentId) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document non trouvé : " + documentId));

        Instrument instrument = instrumentRepository.findById(instrumentId)
            .orElseThrow(() -> new RuntimeException("Instrument non trouvé : " + instrumentId));

        document.removeInstrument(instrument);

        documentRepository.save(document);
    }
}
