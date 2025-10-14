package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.dto.CreateInvitationDTO;
import fr.afpa.choral_riff.dto.InvitationDTO;
import fr.afpa.choral_riff.entity.Ensemble;
import fr.afpa.choral_riff.entity.Invitation;
import fr.afpa.choral_riff.mapper.CreateInvitationMapper;
import fr.afpa.choral_riff.mapper.InvitationMapper;
import fr.afpa.choral_riff.repositories.EnsembleRepository;
import fr.afpa.choral_riff.repositories.InvitationRepository;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class InvitationServiceTest {

    private InvitationRepository invitationRepository;
    private InvitationMapper invitationMapper;
    private EnsembleRepository ensembleRepository;
    private UtilisateurRepository utilisateurRepository;
    private CreateInvitationMapper createInvitationMapper;
    private InvitationService invitationService;
    private MailService mailServiceMock;

    @BeforeEach
    public void setUp() {
        invitationRepository = mock(InvitationRepository.class);
        invitationMapper = mock(InvitationMapper.class);
        ensembleRepository = mock(EnsembleRepository.class);
        utilisateurRepository = mock(UtilisateurRepository.class);
        createInvitationMapper = mock(CreateInvitationMapper.class);
        mailServiceMock = mock(MailService.class);

        invitationService = new InvitationService(
                invitationRepository,
                invitationMapper,
                ensembleRepository,
                utilisateurRepository,
                mailServiceMock,
                createInvitationMapper);

        doNothing().when(mailServiceMock).sendInvitationEmail(anyString(), anyString());
    }

    @Test
    public void testCreerInvitation_UserNotFound() {
        // Prépare le DTO avec un email qui n'existe pas
        CreateInvitationDTO dto = new CreateInvitationDTO();
        dto.setEmailInvite("missing@example.com");
        dto.setEnsembleId(1L);

        // Mock utilisateur non trouvé
        when(utilisateurRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        // Mock ensemble existant
        Ensemble ensemble = new Ensemble();
        ensemble.setId(1L);
        when(ensembleRepository.findById(1L)).thenReturn(Optional.of(ensemble));

        // Mock conversion DTO -> entity
        when(createInvitationMapper.toEntity(any(CreateInvitationDTO.class))).thenAnswer(invocation -> {
            CreateInvitationDTO argDto = invocation.getArgument(0);
            Invitation invitation = new Invitation();
            invitation.setEmailInvite(argDto.getEmailInvite());
            return invitation;
        });

        // Mock save d'invitation : retourne l'invitation avec un ID
        when(invitationRepository.save(any(Invitation.class))).thenAnswer(invocation -> {
            Invitation inv = invocation.getArgument(0);
            inv.setId(1L);
            inv.setEnsemble(ensemble);
            return inv;
        });

        // Mock conversion entity -> DTO
        when(invitationMapper.toDto(any(Invitation.class))).thenAnswer(invocation -> {
            Invitation inv = invocation.getArgument(0);
            InvitationDTO invitationDTO = new InvitationDTO();
            invitationDTO.setId(inv.getId());
            invitationDTO.setEmailInvite(inv.getEmailInvite());
            invitationDTO.setEnsembleId(inv.getEnsemble().getId());
            return invitationDTO;
        });

        // Appel de la méthode testée
        InvitationDTO result = invitationService.creerInvitation(dto);

        // Vérifications
        assertNotNull(result, "Le DTO retourné ne doit pas être null");
        assertEquals(1L, result.getId(), "L'ID de l'invitation doit être 1L");
        verify(invitationRepository).save(argThat(inv -> inv.getUtilisateur() == null));
        verify(invitationRepository).save(argThat(inv -> inv.getEnsemble().equals(ensemble)));
    }
}
