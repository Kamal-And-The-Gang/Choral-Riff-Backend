
// package fr.afpa.choral_riff.services;

// import fr.afpa.choral_riff.dto.CreateInvitationDTO;
// import fr.afpa.choral_riff.dto.InvitationDTO;
// import fr.afpa.choral_riff.entity.Ensemble;
// import fr.afpa.choral_riff.entity.Invitation;
// import fr.afpa.choral_riff.mapper.CreateInvitationMapper;
// import fr.afpa.choral_riff.mapper.InvitationMapper;
// import fr.afpa.choral_riff.repositories.*;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// import java.util.Optional;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;

// public class InvitationServiceTest {

//         private InvitationRepository invitationRepository;
//         private InvitationMapper invitationMapper;
//         private EnsembleRepository ensembleRepository;
//         private UtilisateurRepository utilisateurRepository;
//         private MailService mailService;
//         private CreateInvitationMapper createInvitationMapper;
//         private UtilisateurEnsembleRepository utilisateurEnsembleRepository;
//         private NotificationRepository notificationRepository;
//         private NotificationService notificationService;

//         private InvitationService invitationService;

//         @BeforeEach
//         public void setUp() {
//                 invitationRepository = mock(InvitationRepository.class);
//                 invitationMapper = mock(InvitationMapper.class);
//                 ensembleRepository = mock(EnsembleRepository.class);
//                 utilisateurRepository = mock(UtilisateurRepository.class);
//                 mailService = mock(MailService.class);
//                 createInvitationMapper = mock(CreateInvitationMapper.class);
//                 utilisateurEnsembleRepository = mock(UtilisateurEnsembleRepository.class);
//                 notificationRepository = mock(NotificationRepository.class);
//                 notificationService = mock(NotificationService.class);

//                 invitationService = new InvitationService(
//                                 invitationRepository,
//                                 invitationMapper,
//                                 ensembleRepository,
//                                 utilisateurRepository,
//                                 mailService,
//                                 createInvitationMapper,
//                                 utilisateurEnsembleRepository,
//                                 notificationRepository,
//                                 notificationService);
//         }

//         @Test
//         public void testCreerInvitationUtilisateurExiste() {
//                 CreateInvitationDTO dto = new CreateInvitationDTO();
//                 dto.setEmailInvite("test@test.com");
//                 dto.setEnsembleId(1L);

//                 // Mock utilisateur existant
//                 when(utilisateurRepository.findByEmail("test@test.com"))
//                                 .thenReturn(Optional.of(new fr.afpa.choral_riff.entity.Utilisateur()));

//                 InvitationDTO result = invitationService.creerInvitation(dto);

//                 assertTrue(result.isExistant());
//                 assertFalse(result.isDejaMembre());
//         }

//         @Test
//         public void testCreerInvitationUtilisateurNonExistant() {
//                 CreateInvitationDTO dto = new CreateInvitationDTO();
//                 dto.setEmailInvite("nouveau@test.com");
//                 dto.setEnsembleId(1L);

//                 // utilisateur non existant
//                 when(utilisateurRepository.findByEmail("nouveau@test.com"))
//                                 .thenReturn(Optional.empty());

//                 Ensemble ensemble = new Ensemble();
//                 ensemble.setId(1L);
//                 ensemble.setNom("Chorale");

//                 when(ensembleRepository.findById(1L))
//                                 .thenReturn(Optional.of(ensemble));

//                 Invitation invitation = new Invitation();
//                 invitation.setEmailInvite("nouveau@test.com");
//                 invitation.setEnsemble(ensemble);

//                 when(invitationRepository.save(any(Invitation.class)))
//                                 .thenReturn(invitation);

//                 when(invitationMapper.toDto(any(Invitation.class)))
//                                 .thenReturn(new InvitationDTO());

//                 InvitationDTO result = invitationService.creerInvitation(dto);

//                 assertNotNull(result);
//         }
// }
