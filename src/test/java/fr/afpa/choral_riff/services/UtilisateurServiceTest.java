package fr.afpa.choral_riff.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.NoSuchElementException;

import fr.afpa.choral_riff.dto.UtilisateurDto;
import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.mapper.UtilisateurMapper;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;
// import fr.afpa.choral_riff.services.UtilisateurService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

public class UtilisateurServiceTest {

    @Mock
    UtilisateurRepository utilisateurRepository;

    @Mock
    UtilisateurMapper utilisateurMapper;

    @InjectMocks
    UtilisateurService utilisateurService;

    private Utilisateur utilisateur;
    private UtilisateurDto utilisateurDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setNom("Dupont");
        utilisateur.setPrenom("Jean");
        utilisateur.setEmail("dupont@example.com");

        utilisateurDto = new UtilisateurDto(1L, "Dupont", "Jean", "dupont@example.com", null);
    }

    @Test
    void testGetById_Success() {
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(utilisateurMapper.toDto(utilisateur)).thenReturn(utilisateurDto);

        UtilisateurDto dto = utilisateurService.getById(1L);
        assertEquals("Dupont", dto.getNom());
    }

    @Test
    void testGetById_NotFound() {
        when(utilisateurRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> utilisateurService.getById(2L));
    }
}
