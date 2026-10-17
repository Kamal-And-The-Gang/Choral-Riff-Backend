
package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.dto.UtilisateurDto;
import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.mapper.UtilisateurMapper;
import fr.afpa.choral_riff.repositories.MorceauRepository;
import fr.afpa.choral_riff.repositories.UtilisateurEnsembleRepository;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UtilisateurIdorTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private MorceauRepository morceauRepository;

    @Mock
    private UtilisateurMapper utilisateurMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UtilisateurEnsembleRepository utilisateurEnsembleRepository;

    @InjectMocks
    private UtilisateurService utilisateurService;

    @BeforeEach
    void connecterUtilisateurA() {
        // Simulation de l'utilisateur A connecté
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(
                "alice@example.com",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
            )
        );
    }

    @AfterEach
    void nettoyerContexteSecurite() {
        SecurityContextHolder.clearContext();
    }

   @Test
void utilisateurA_neDoitPasLireLeProfilDeB() {
    // Alice est l'utilisatrice connectée
    Utilisateur utilisateurA = new Utilisateur();
    utilisateurA.setId(1L);

    when(utilisateurRepository.findByEmail("alice@example.com"))
            .thenReturn(Optional.of(utilisateurA));

    // Alice tente de consulter le profil de Bob (id = 2)
    assertThrows(
            AccessDeniedException.class,
            () -> utilisateurService.getById(2L)
    );

    // Vérifie qu'aucun profil de Bob n'a été chargé
    verify(utilisateurRepository, never()).findById(2L);
}

   @Test
void utilisateurA_neDoitPasModifierLeProfilDeB() {
    // Alice est l'utilisatrice connectée
    Utilisateur utilisateurA = new Utilisateur();
    utilisateurA.setId(1L);

    when(utilisateurRepository.findByEmail("alice@example.com"))
            .thenReturn(Optional.of(utilisateurA));

    UtilisateurDto dto = mock(UtilisateurDto.class);

    // Alice tente de modifier le profil de Bob (id = 2)
    assertThrows(
            AccessDeniedException.class,
            () -> utilisateurService.update(2L, dto)
    );

    // Vérifie qu'aucune modification n'a été enregistrée
    verify(utilisateurRepository, never()).save(any(Utilisateur.class));
}

    
@Test
void utilisateurA_neDoitPasSupprimerLeCompteDeB() {
    Utilisateur utilisateurA = new Utilisateur();
    utilisateurA.setId(1L);

    when(utilisateurRepository.findByEmail("alice@example.com"))
            .thenReturn(Optional.of(utilisateurA));

    assertThrows(AccessDeniedException.class,
            () -> utilisateurService.delete(2L));

    verify(utilisateurRepository, never()).delete(any(Utilisateur.class));
    verify(morceauRepository, never()).findByCreateur_Id(2L);
}

@Test
void utilisateurNormal_neDoitPasRecupererTousLesUtilisateurs() {
    assertThrows(
        AccessDeniedException.class,
        () -> utilisateurService.getAll()
    );

    verify(utilisateurRepository, never()).findAll();
}

}
