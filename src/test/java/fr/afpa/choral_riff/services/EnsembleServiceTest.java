package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.dto.EnsembleDto;
import fr.afpa.choral_riff.entity.Ensemble;
import fr.afpa.choral_riff.entity.Role;
import fr.afpa.choral_riff.entity.TypeEnsemble;
import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.entity.UtilisateurEnsemble;
import fr.afpa.choral_riff.mapper.EnsembleMapper;
import fr.afpa.choral_riff.repositories.EnsembleRepository;
import fr.afpa.choral_riff.repositories.InvitationRepository;
import fr.afpa.choral_riff.repositories.MorceauRepository;
import fr.afpa.choral_riff.repositories.NotificationRepository;
import fr.afpa.choral_riff.repositories.UtilisateurEnsembleRepository;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnsembleServiceTest {

    @Mock
    private EnsembleRepository ensembleRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private UtilisateurEnsembleRepository utilisateurEnsembleRepository;

    @Mock
    private EnsembleMapper ensembleMapper;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private InvitationRepository invitationRepository;

    @Mock
    private MorceauRepository morceauRepository;

    @InjectMocks
    private EnsembleService ensembleService;

    private Utilisateur utilisateur;
    private Ensemble ensemble;
    private EnsembleDto ensembleDto;

    @BeforeEach
    void setUp() {
        utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setNom("Dupont");
        utilisateur.setPrenom("Jean");

        ensemble = new Ensemble();
        ensemble.setId(10L);
        ensemble.setNom("Chorale test");
        ensemble.setDescription("Description test");
        ensemble.setTypeEnsemble(TypeEnsemble.CHOEUR);
        ensemble.setDateCreation(LocalDate.now());
        ensemble.setUtilisateurEnsembles(new HashSet<>());

        ensembleDto = new EnsembleDto();
        ensembleDto.setId(10L);
        ensembleDto.setNom("Chorale test");
        ensembleDto.setDescription("Description test");
        ensembleDto.setTypeEnsemble(TypeEnsemble.CHOEUR);
        ensembleDto.setDateCreation(LocalDate.now());
        ensembleDto.setUserRole(null);
        ensembleDto.setIsCreator(false);
    }

    @Test
    void shouldCreateEnsembleWhenCreatorExists() {
        when(ensembleMapper.toEntity(ensembleDto)).thenReturn(ensemble);
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(ensembleRepository.save(any(Ensemble.class))).thenReturn(ensemble);
        when(ensembleMapper.toDto(ensemble, 1L)).thenReturn(ensembleDto);

        EnsembleDto result = ensembleService.create(ensembleDto, 1L);

        assertNotNull(result);
        verify(utilisateurRepository).findById(1L);
        verify(ensembleRepository).save(ensemble);
        verify(ensembleMapper).toDto(ensemble, 1L);
    }

    @Test
    void shouldThrowExceptionWhenCreatorNotFound() {
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> ensembleService.create(ensembleDto, 1L));

        assertEquals("Création ensemble - id créateur non retrouvé", exception.getMessage());
    }

    @Test
    void shouldReturnEnsembleDtoWithUserRoleAndCreatorFlag() {
        UtilisateurEnsemble utilisateurEnsemble = new UtilisateurEnsemble(
                utilisateur,
                ensemble,
                Role.ADMIN,
                LocalDateTime.now()
        );
        utilisateurEnsemble.setCreator(true);

        when(ensembleRepository.findById(10L)).thenReturn(Optional.of(ensemble));
        when(ensembleMapper.toDto(ensemble, 1L)).thenReturn(ensembleDto);
        when(utilisateurEnsembleRepository.findByUtilisateur_IdAndEnsemble_Id(1L, 10L))
                .thenReturn(Optional.of(utilisateurEnsemble));

        EnsembleDto result = ensembleService.getById(10L, 1L);

        assertEquals("ADMIN", result.getUserRole());
        assertTrue(result.isCreator());
    }

    @Test
    void shouldThrowExceptionWhenEnsembleNotFound() {
        when(ensembleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> ensembleService.getById(99L, 1L));
    }

    @Test
    void shouldUpdateEnsembleWhenUserHasRights() {
        when(ensembleRepository.findById(10L)).thenReturn(Optional.of(ensemble));
        when(utilisateurEnsembleRepository.findByUtilisateur_IdAndEnsemble_Id(1L, 10L))
                .thenReturn(Optional.of(new UtilisateurEnsemble(
                        utilisateur, ensemble, Role.ADMIN, LocalDateTime.now()
                )));
        when(ensembleRepository.save(any(Ensemble.class))).thenReturn(ensemble);
        when(ensembleMapper.toDto(ensemble, 1L)).thenReturn(ensembleDto);

        EnsembleDto result = ensembleService.update(10L, ensembleDto, 1L);

        assertEquals("Chorale test", result.getNom());
        verify(ensembleRepository).save(ensemble);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithoutRights() {
        when(ensembleRepository.findById(10L)).thenReturn(Optional.of(ensemble));
        when(utilisateurEnsembleRepository.findByUtilisateur_IdAndEnsemble_Id(1L, 10L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> ensembleService.update(10L, ensembleDto, 1L));
    }

    @Test
    void shouldDeleteEnsembleWhenUserHasRights() {
        UtilisateurEnsemble ue = new UtilisateurEnsemble(
                utilisateur,
                ensemble,
                Role.ADMIN,
                LocalDateTime.now()
        );
        ue.setCreator(true);

        when(utilisateurEnsembleRepository.findByUtilisateur_IdAndEnsemble_Id(1L, 10L))
                .thenReturn(Optional.of(ue));
        when(ensembleRepository.findById(10L)).thenReturn(Optional.of(ensemble));

        ensembleService.delete(10L, 1L);

        verify(ensembleRepository).delete(ensemble);
    }

    @Test
    void shouldThrowExceptionWhenDeletingWithoutRights() {
        when(utilisateurEnsembleRepository.findByUtilisateur_IdAndEnsemble_Id(1L, 10L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> ensembleService.delete(10L, 1L));
    }

    @Test
    void shouldReturnTrueWhenUserIsAdmin() {
        when(utilisateurEnsembleRepository.findByUtilisateur_IdAndEnsemble_Id(1L, 10L))
                .thenReturn(Optional.of(new UtilisateurEnsemble(
                        utilisateur, ensemble, Role.ADMIN, LocalDateTime.now()
                )));

        assertTrue(ensembleService.hasRights(1L, 10L));
    }

    @Test
    void shouldReturnFalseWhenUserIsMember() {
        when(utilisateurEnsembleRepository.findByUtilisateur_IdAndEnsemble_Id(1L, 10L))
                .thenReturn(Optional.of(new UtilisateurEnsemble(
                        utilisateur, ensemble, Role.MEMBRE, LocalDateTime.now()
                )));

        assertFalse(ensembleService.hasRights(1L, 10L));
    }
}
