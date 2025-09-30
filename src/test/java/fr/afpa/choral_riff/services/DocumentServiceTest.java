package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.dto.DocumentDto;
import fr.afpa.choral_riff.entity.Document;
import fr.afpa.choral_riff.entity.Morceau;
import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.mapper.DocumentMapper;
import fr.afpa.choral_riff.repositories.DocumentRepository;
import fr.afpa.choral_riff.repositories.MorceauRepository;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DocumentServiceTest {

    private DocumentRepository documentRepository;
    private MorceauRepository morceauRepository;
    private UtilisateurRepository utilisateurRepository;
    private DocumentMapper documentMapper;
    private DocumentService documentService;

    @BeforeEach
    public void setUp() {
        documentRepository = mock(DocumentRepository.class);
        morceauRepository = mock(MorceauRepository.class);
        utilisateurRepository = mock(UtilisateurRepository.class);
        documentMapper = mock(DocumentMapper.class);

        documentService = new DocumentService(
                documentRepository,
                morceauRepository,
                utilisateurRepository,
                documentMapper);
    }

    @Test
    public void testGetAll() {
        // Préparation des données mockées
        Document doc = new Document();
        doc.setId(1L);
        doc.setType("Partition");
        doc.setFormat("PDF");
        doc.setDateAjout(LocalDate.now());
        doc.setUrlFichier("fichier.pdf");

        Utilisateur user = new Utilisateur();
        user.setId(10L);
        doc.setUtilisateur(user);

        Morceau morceau = new Morceau();
        morceau.setId(20L);
        doc.setMorceau(morceau);

        DocumentDto dto = new DocumentDto(
                1L,
                "Partition",
                "PDF",
                doc.getDateAjout(),
                "fichier.pdf",
                10L,
                20L);

        // Configuration des mocks
        when(documentRepository.findAll()).thenReturn(List.of(doc));
        when(documentMapper.toDto(doc)).thenReturn(dto);

        // Appel de la méthode à tester
        List<DocumentDto> result = documentService.getAll();

        // Vérifications
        assertEquals(1, result.size());
        assertEquals("Partition", result.get(0).type());
        assertEquals(10L, result.get(0).utilisateurId());
        assertEquals(20L, result.get(0).morceauId());
    }

}
