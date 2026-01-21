// package fr.afpa.choral_riff.services;

// import static org.mockito.Mockito.*;
// import static org.junit.jupiter.api.Assertions.*;
// import java.util.NoSuchElementException;
// import fr.afpa.choral_riff.dto.UtilisateurDto;
// import fr.afpa.choral_riff.entity.Utilisateur;
// import fr.afpa.choral_riff.mapper.UtilisateurMapper;
// import fr.afpa.choral_riff.repositories.UtilisateurRepository;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.*;

// import java.util.Optional;

// public class UtilisateurServiceTest {

//     @Mock
//     UtilisateurRepository utilisateurRepository;

//     @Mock
//     UtilisateurMapper utilisateurMapper;

//     @InjectMocks
//     UtilisateurService utilisateurService;

//     private Utilisateur utilisateur;
//     private UtilisateurDto utilisateurDto;

//     @BeforeEach
//     void setUp() {
//         MockitoAnnotations.openMocks(this);

//         utilisateur = new Utilisateur();
//         utilisateur.setId(1L);
//         utilisateur.setNom("Dupont");
//         utilisateur.setPrenom("Jean");
//         utilisateur.setEmail("dupont@example.com");
//         utilisateurDto = new UtilisateurDto(1L, "Dupont", "Jean", "dupont@example.com", null);
//     }

//     @Test
//     void testGetById_Success() {
//         when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));
//         when(utilisateurMapper.toDto(utilisateur)).thenReturn(utilisateurDto);

//         UtilisateurDto dto = utilisateurService.getById(1L);
//         assertEquals("Dupont", dto.getNom());
//     }

//     @Test
//     void testGetById_NotFound() {
//         when(utilisateurRepository.findById(2L)).thenReturn(Optional.empty());

//         assertThrows(NoSuchElementException.class, () -> utilisateurService.getById(2L));
//     }
// }
package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.dto.RegisterDto;
import fr.afpa.choral_riff.dto.UtilisateurDto;
import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.mapper.UtilisateurMapper;
import fr.afpa.choral_riff.repositories.UtilisateurEnsembleRepository;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UtilisateurServiceTest {

    private UtilisateurRepository utilisateurRepository;
    private UtilisateurMapper utilisateurMapper;
    private PasswordEncoder passwordEncoder;
    private UtilisateurEnsembleRepository utilisateurEnsembleRepository;

    private UtilisateurService utilisateurService;

    @BeforeEach
    public void setUp() {
        utilisateurRepository = mock(UtilisateurRepository.class);
        utilisateurMapper = mock(UtilisateurMapper.class);
        passwordEncoder = mock(PasswordEncoder.class);
        utilisateurEnsembleRepository = mock(UtilisateurEnsembleRepository.class);

        utilisateurService = new UtilisateurService(
                utilisateurRepository,
                utilisateurMapper,
                passwordEncoder,
                null,
                utilisateurEnsembleRepository
        );
    }

    @Test
    public void testRegisterUtilisateurExiste() {
        RegisterDto dto = new RegisterDto();
        dto.setEmail("test@test.com");
        dto.setMotDePasse("123456");

        Utilisateur user = new Utilisateur();
        user.setId(1L);
        user.setEmail("test@test.com");

        when(utilisateurRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        when(utilisateurMapper.toDto(user))
                .thenReturn(new UtilisateurDto(1L, "nom", "prenom", "test@test.com", null));

        UtilisateurDto result = utilisateurService.register(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@test.com", result.getEmail());
    }

    @Test
    public void testRegisterUtilisateurNonExiste() {
        RegisterDto dto = new RegisterDto();
        dto.setEmail("nouveau@test.com");
        dto.setMotDePasse("123456");

        when(utilisateurRepository.findByEmail("nouveau@test.com"))
                .thenReturn(Optional.empty());

        Utilisateur entity = new Utilisateur();
        entity.setEmail("nouveau@test.com");
        entity.setMotDePasse("encoded");

        when(utilisateurMapper.fromRegisterDto(dto))
                .thenReturn(entity);

        when(passwordEncoder.encode("123456"))
                .thenReturn("encoded");

        when(utilisateurRepository.save(entity))
                .thenReturn(entity);

        when(utilisateurMapper.toDto(entity))
                .thenReturn(new UtilisateurDto(2L, "nom", "prenom", "nouveau@test.com", null));

        UtilisateurDto result = utilisateurService.register(dto);

        assertNotNull(result);
        assertEquals("nouveau@test.com", result.getEmail());
        assertEquals(2L, result.getId());
    }

    @Test
    public void testGetById() {
        Utilisateur user = new Utilisateur();
        user.setId(5L);
        user.setEmail("test@test.com");

        when(utilisateurRepository.findById(5L))
                .thenReturn(Optional.of(user));

        when(utilisateurMapper.toDto(user))
                .thenReturn(new UtilisateurDto(5L, "nom", "prenom", "test@test.com", null));

        UtilisateurDto result = utilisateurService.getById(5L);

        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertEquals("test@test.com", result.getEmail());
    }
}
