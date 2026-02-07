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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private MorceauRepository morceauRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private InstrumentRepository instrumentRepository;

    @Mock
    private DocumentMapper documentMapper;

    @Mock
    private UtilisateurEnsembleService utilisateurEnsembleService;

    @InjectMocks
    private DocumentService documentService;

    private Document document;
    private DocumentDto documentDto;
    private Morceau morceau;
    private Utilisateur utilisateur;
    private Instrument instrument;

    @BeforeEach
    void setUp() {
        utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setNom("Dupont");
        utilisateur.setPrenom("Jean");

        morceau = new Morceau();
        morceau.setId(10L);
        morceau.setTitre("Morceau test");

        document = new Document();
        document.setId(100L);
        document.setType("partition");
        document.setFormat("pdf");
        document.setDateAjout(LocalDate.now());
        document.setUrlFichier("/uploads/doc.pdf");
        document.setUtilisateur(utilisateur);
        document.setMorceau(morceau);

        documentDto = new DocumentDto(
                100L,
                "partition",
                "pdf",
                LocalDate.now(),
                "/uploads/doc.pdf",
                utilisateur.getId(),
                morceau.getId(),
                new ArrayList<>()
        );

        instrument = new Instrument();
        instrument.setId(50L);
        instrument.setNom("Piano");
    }

    @Test
    void shouldCreateDocumentWhenMorceauAndUtilisateurExist() {
        when(documentMapper.toEntity(documentDto)).thenReturn(document);
        when(morceauRepository.findById(morceau.getId())).thenReturn(Optional.of(morceau));
        when(utilisateurRepository.findById(utilisateur.getId())).thenReturn(Optional.of(utilisateur));
        when(documentRepository.save(any(Document.class))).thenReturn(document);
        when(documentMapper.toDto(document)).thenReturn(documentDto);

        DocumentDto result = documentService.create(documentDto);

        assertNotNull(result);
        assertEquals(documentDto.id_document(), result.id_document());
        verify(documentRepository).save(document);
        verify(morceauRepository).findById(morceau.getId());
        verify(utilisateurRepository).findById(utilisateur.getId());
    }

    @Test
    void shouldThrowExceptionWhenMorceauNotFound() {
        when(morceauRepository.findById(morceau.getId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> documentService.create(documentDto));

        assertTrue(exception.getMessage().contains("Morceau non trouvé"));
    }

    @Test
    void shouldReturnDocumentDtoWhenDocumentExists() {
        when(documentRepository.findById(document.getId())).thenReturn(Optional.of(document));
        when(documentMapper.toDto(document)).thenReturn(documentDto);

        DocumentDto result = documentService.getById(document.getId());

        assertNotNull(result);
        assertEquals(document.getId(), result.id_document());
        verify(documentRepository).findById(document.getId());
        verify(documentMapper).toDto(document);
    }

    @Test
    void shouldThrowExceptionWhenDocumentNotFound() {
        when(documentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> documentService.getById(999L));
    }

    @Test
    void shouldGetDocumentsByMorceauId() {
        when(morceauRepository.findById(morceau.getId())).thenReturn(Optional.of(morceau));
        when(documentRepository.findByMorceau(morceau)).thenReturn(List.of(document));
        when(documentMapper.toDto(document)).thenReturn(documentDto);

        List<DocumentDto> results = documentService.getDocumentsByMorceauId(morceau.getId());

        assertEquals(1, results.size());
        assertEquals(documentDto.id_document(), results.get(0).id_document());
        verify(documentRepository).findByMorceau(morceau);
    }

    @Test
    void shouldAddInstrumentToDocument() {
        when(documentRepository.findById(document.getId())).thenReturn(Optional.of(document));
        when(instrumentRepository.findById(instrument.getId())).thenReturn(Optional.of(instrument));
        when(documentRepository.save(document)).thenReturn(document);
        when(documentMapper.toDto(document)).thenReturn(documentDto);

        DocumentDto result = documentService.addInstrument(document.getId(), instrument.getId());

        assertNotNull(result);
        verify(documentRepository).save(document);
        verify(documentMapper).toDto(document);
    }

    @Test
    void shouldDeleteDocumentWhenExists() {
        when(documentRepository.findById(document.getId())).thenReturn(Optional.of(document));

        documentService.delete(document.getId());

        verify(documentRepository).delete(document);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentDocument() {
        when(documentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> documentService.delete(999L));
    }
}
