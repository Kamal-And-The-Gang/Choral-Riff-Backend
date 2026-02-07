package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.dto.CreateInvitationDTO;
import fr.afpa.choral_riff.dto.InvitationDTO;
import fr.afpa.choral_riff.entity.*;
import fr.afpa.choral_riff.mapper.InvitationMapper;
import fr.afpa.choral_riff.repositories.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvitationServiceTest {

    @Mock
    private InvitationRepository invitationRepository;

    @Mock
    private InvitationMapper invitationMapper;

    @Mock
    private EnsembleRepository ensembleRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private UtilisateurEnsembleRepository utilisateurEnsembleRepository;

    @Mock
    private MailService mailService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private InvitationService invitationService;

    // ==================================================
    // Envoi d’invitation
    // ==================================================

    @Test
    void envoyerInvitation_utilisateurDejaMembre_aucunEmailEnvoye() {
        CreateInvitationDTO dto = new CreateInvitationDTO();
        dto.setEmailInvite("test@mail.com");
        dto.setEnsembleId(1L);

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(10L);

        when(utilisateurRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(utilisateur));

        when(utilisateurEnsembleRepository
                .existsByUtilisateurIdAndEnsembleId(10L, 1L))
                .thenReturn(true);

        InvitationDTO result = invitationService.creerInvitation(dto);

        assertTrue(result.isExistant());
        assertTrue(result.isDejaMembre());
        assertEquals(10L, result.getUtilisateurId());

        verify(invitationRepository, never()).save(any());
        verify(mailService, never()).sendInvitationEmail(any(), any());
    }

    @Test
    void envoyerInvitation_nouvelEmail_invitationCreeeEtEmailEnvoye() {
        CreateInvitationDTO dto = new CreateInvitationDTO();
        dto.setEmailInvite("new@mail.com");
        dto.setEnsembleId(1L);

        Ensemble ensemble = new Ensemble();
        ensemble.setId(1L);

        Invitation invitationSauvegardee = new Invitation();
        invitationSauvegardee.setId(100L);
        invitationSauvegardee.setEmailInvite("new@mail.com");
        invitationSauvegardee.setEnsemble(ensemble);

        InvitationDTO dtoRetour = new InvitationDTO();
        dtoRetour.setEmailInvite("new@mail.com");

        when(utilisateurRepository.findByEmail("new@mail.com"))
                .thenReturn(Optional.empty());

        when(ensembleRepository.findById(1L))
                .thenReturn(Optional.of(ensemble));

        when(invitationRepository.save(any(Invitation.class)))
                .thenReturn(invitationSauvegardee);

        when(invitationMapper.toDto(any(Invitation.class)))
                .thenReturn(dtoRetour);

        InvitationDTO result = invitationService.creerInvitation(dto);

        assertNotNull(result);
        assertEquals("new@mail.com", result.getEmailInvite());

        verify(invitationRepository).save(any());
        verify(mailService).sendInvitationEmail(eq("new@mail.com"), anyString());
    }

    // ==================================================
    // Clic sur lien d’invitation
    // ==================================================

    @Test
    void clicLienInvitation_valide_rattacheUtilisateurALensemble() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(5L);

        Ensemble ensemble = new Ensemble();
        ensemble.setId(2L);
        ensemble.setNom("Chorale Test");

        Invitation invitation = new Invitation();
        invitation.setId(42L);
        invitation.setEmailInvite("user@mail.com");
        invitation.setEnsemble(ensemble);
        invitation.setDateExpiration(LocalDateTime.now().plusDays(1));

        InvitationDTO dto = new InvitationDTO();

        when(utilisateurRepository.findByEmail("user@mail.com"))
                .thenReturn(Optional.of(utilisateur));

        when(utilisateurEnsembleRepository
                .existsByUtilisateurIdAndEnsembleId(5L, 2L))
                .thenReturn(false);

        when(invitationMapper.toDto(any(Invitation.class)))
                .thenReturn(dto);

        InvitationDTO result =
                invitationService.rattacherUtilisateurApresInscription(utilisateur, invitation);

        assertNotNull(result);
        assertNotNull(invitation.getDateUtilisation());
        assertEquals(StatusInvitation.ACCEPTEE, invitation.getEtat());

        verify(notificationService).createNotification(
                eq(5L),
                eq("INVITATION"),
                contains("Chorale Test"),
                eq(42L)
        );

        verify(utilisateurEnsembleRepository)
                .saveAndFlush(any(UtilisateurEnsemble.class));
    }

    @Test
    void clicLienInvitation_dejaUtilise_refuseLeRattachement() {
        Invitation invitation = new Invitation();
        invitation.setDateUtilisation(LocalDateTime.now());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> invitationService.rattacherUtilisateurApresInscription(
                        new Utilisateur(),
                        invitation
                )
        );

        assertEquals("Cette invitation a déjà été utilisée.", exception.getMessage());

        verifyNoInteractions(notificationService);
        verify(utilisateurEnsembleRepository, never()).saveAndFlush(any());
    }
}
