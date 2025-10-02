package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.dto.InvitationDTO;
import fr.afpa.choral_riff.entity.Ensemble;
import fr.afpa.choral_riff.entity.Invitation;
import fr.afpa.choral_riff.entity.StatutInvitation;
import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.mapper.InvitationMapper;
import fr.afpa.choral_riff.repositories.EnsembleRepository;
import fr.afpa.choral_riff.repositories.InvitationRepository;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class InvitationServiceTest {

    private InvitationRepository invitationRepository;
    private InvitationMapper invitationMapper;
    private EnsembleRepository ensembleRepository;
    private UtilisateurRepository utilisateurRepository;
    private InvitationService invitationService;

    @BeforeEach
    public void setUp() {
        invitationRepository = mock(InvitationRepository.class);
        invitationMapper = mock(InvitationMapper.class);
        ensembleRepository = mock(EnsembleRepository.class);
        utilisateurRepository = mock(UtilisateurRepository.class);

        invitationService = new InvitationService(
                invitationRepository,
                invitationMapper,
                ensembleRepository,
                utilisateurRepository);
    }

    @Test
    public void testCreateInvitation_UserNotFound() {
        // Prépare le DTO avec un email qui n'existe pas
        InvitationDTO dto = new InvitationDTO();
        dto.setEmailInvite("missing@example.com");
        dto.setEnsembleId(1L);

        // Mock utilisateur non trouvé
        when(utilisateurRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        // Mock ensemble existant
        Ensemble ensemble = new Ensemble();
        ensemble.setId(1L);
        when(ensembleRepository.findById(1L)).thenReturn(Optional.of(ensemble));

        // Mock save d'invitation : retourne l'invitation avec un ID
        when(invitationRepository.save(any(Invitation.class))).thenAnswer(invocation -> {
            Invitation inv = invocation.getArgument(0);
            inv.setId(1L);
            return inv;
        });

        // Mock conversion vers DTO : retourne un DTO avec l'id 1L
        when(invitationMapper.toDto(any(Invitation.class))).thenAnswer(invocation -> {
            Invitation inv = invocation.getArgument(0);
            InvitationDTO invitationDTO = new InvitationDTO();
            invitationDTO.setId(inv.getId());
            invitationDTO.setEmailInvite(inv.getEmailInvite());
            invitationDTO.setEnsembleId(inv.getEnsemble().getId());
            return invitationDTO;
        });

        // Appel de la méthode testée
        InvitationDTO result = invitationService.create(dto);

        // Vérifications
        assertNotNull(result, "Le DTO retourné ne doit pas être null");
        assertEquals(1L, result.getId(), "L'ID de l'invitation doit être 1L");
        verify(invitationRepository).save(argThat(inv -> inv.getUtilisateur() == null));
        verify(invitationRepository).save(argThat(inv -> inv.getEnsemble().equals(ensemble)));
    }

    @Test
    public void testAcceptInvitation_Success() {
        String token = "token123";
        Invitation invitation = new Invitation();
        invitation.setEtat(StatutInvitation.EN_ATTENTE);

        Invitation updated = new Invitation();
        updated.setEtat(StatutInvitation.ACCEPTEE);

        InvitationDTO dto = new InvitationDTO();
        dto.setEtat(StatutInvitation.ACCEPTEE.name());

        when(invitationRepository.findByToken(token)).thenReturn(Optional.of(invitation));
        when(invitationRepository.save(invitation)).thenReturn(updated);
        when(invitationMapper.toDto(updated)).thenReturn(dto);

        InvitationDTO result = invitationService.accept(token);
        assertEquals("ACCEPTEE", result.getEtat());
    }

    @Test
    public void testRefuseInvitation_Success() {
        String token = "tokenRefuse";
        Invitation invitation = new Invitation();
        invitation.setEtat(StatutInvitation.EN_ATTENTE);

        Invitation updated = new Invitation();
        updated.setEtat(StatutInvitation.REFUSEE);

        InvitationDTO dto = new InvitationDTO();
        dto.setEtat(StatutInvitation.REFUSEE.name());

        when(invitationRepository.findByToken(token)).thenReturn(Optional.of(invitation));
        when(invitationRepository.save(invitation)).thenReturn(updated);
        when(invitationMapper.toDto(updated)).thenReturn(dto);

        InvitationDTO result = invitationService.refuse(token);
        assertEquals("REFUSEE", result.getEtat());
    }

    @Test
    public void testGetAllByEnsembleId_ReturnsList() {
        Invitation invitation = new Invitation();
        invitation.setId(1L);

        InvitationDTO dto = new InvitationDTO();
        dto.setId(1L);

        when(invitationRepository.findByEnsembleId(1L)).thenReturn(List.of(invitation));
        when(invitationMapper.toDto(invitation)).thenReturn(dto);

        List<InvitationDTO> result = invitationService.getAllByEnsembleId(1L);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    public void testGetByToken_Success() {
        String token = "someToken";
        Invitation invitation = new Invitation();
        invitation.setToken(token);

        InvitationDTO dto = new InvitationDTO();
        dto.setToken(token);

        when(invitationRepository.findByToken(token)).thenReturn(Optional.of(invitation));
        when(invitationMapper.toDto(invitation)).thenReturn(dto);

        InvitationDTO result = invitationService.getByToken(token);
        assertEquals(token, result.getToken());
    }

    @Test
    public void testDelete_Success() {
        when(invitationRepository.existsById(1L)).thenReturn(true);
        doNothing().when(invitationRepository).deleteById(1L);

        assertDoesNotThrow(() -> invitationService.delete(1L));
        verify(invitationRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDelete_NotFound() {
        when(invitationRepository.existsById(99L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> invitationService.delete(99L));
        assertTrue(ex.getMessage().contains("Invitation non trouvée"));
    }

    @Test
    public void testRattacherUtilisateurAprèsInscription_Success() {
        String token = "token123";
        Invitation invitation = new Invitation();
        invitation.setUtilisateur(null); // pas encore rattaché

        Utilisateur nouvelUtilisateur = new Utilisateur();
        nouvelUtilisateur.setId(10L);

        when(invitationRepository.findByToken(token)).thenReturn(Optional.of(invitation));
        when(invitationRepository.save(invitation)).thenReturn(invitation);

        invitationService.rattacherUtilisateurAprèsInscription(token, nouvelUtilisateur);

        assertEquals(nouvelUtilisateur, invitation.getUtilisateur());
        verify(invitationRepository).save(invitation);
    }

    @Test
    public void testRattacherUtilisateurAprèsInscription_AlreadyAttached() {
        String token = "token123";
        Utilisateur utilisateurExistant = new Utilisateur();
        utilisateurExistant.setId(5L);

        Invitation invitation = new Invitation();
        invitation.setUtilisateur(utilisateurExistant);

        Utilisateur nouvelUtilisateur = new Utilisateur();
        nouvelUtilisateur.setId(10L);

        when(invitationRepository.findByToken(token)).thenReturn(Optional.of(invitation));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            invitationService.rattacherUtilisateurAprèsInscription(token, nouvelUtilisateur);
        });

        assertTrue(ex.getMessage().contains("déjà rattachée"));
    }

}
