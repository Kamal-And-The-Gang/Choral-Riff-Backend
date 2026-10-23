// package fr.afpa.choral_riff.integration;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import fr.afpa.choral_riff.dto.InvitationDTO;
// import fr.afpa.choral_riff.entity.Ensemble;
// import fr.afpa.choral_riff.repositories.EnsembleRepository;
// import fr.afpa.choral_riff.services.MailService;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mock;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.boot.test.mock.mockito.SpyBean;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;

// import static org.mockito.ArgumentMatchers.anyString;
// import static org.mockito.ArgumentMatchers.eq;
// import static org.mockito.Mockito.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @SpringBootTest
// @AutoConfigureMockMvc
// public class InvitationEmailIntegrationTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @Autowired
//     private EnsembleRepository ensembleRepository;

//     @Autowired
//     private ObjectMapper objectMapper;

//     @Autowired
//     private MailService mailService;

//     private Ensemble ensemble;

//     @BeforeEach
//     void setUp() {
//         ensembleRepository.deleteByNom("Ensemble Test");
//         ensemble = new Ensemble();
//         ensemble.setNom("Ensemble Test");
//         ensemble = ensembleRepository.save(ensemble);

//         // Mock du comportement du mail service (ne fait rien)
//         doNothing().when(mailService).sendInvitationEmail(anyString(), anyString());
//     }

//     @Test
//     void testInvitationTriggersEmailSending() throws Exception {
//         InvitationDTO dto = new InvitationDTO();
//         dto.setEmailInvite("invite@example.com");
//         dto.setEnsembleId(ensemble.getId());

//         mockMvc.perform(post("/api/invitations")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(dto)))
//                 .andExpect(status().isCreated());

//         // Est-ce- que l’email a bien été "envoyé"=> Vérification
//         verify(mailService, times(1)).sendInvitationEmail(eq("invite@example.com"),
//                 anyString());
//     }
// }

package fr.afpa.choral_riff.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.afpa.choral_riff.dto.CreateInvitationDTO;
import fr.afpa.choral_riff.entity.Ensemble;
import fr.afpa.choral_riff.repositories.EnsembleRepository;
import fr.afpa.choral_riff.services.MailService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import fr.afpa.choral_riff.entity.Invitation;
import fr.afpa.choral_riff.entity.StatusInvitation;
import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.repositories.InvitationRepository;
import fr.afpa.choral_riff.services.InvitationService;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
class InvitationEmailIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EnsembleRepository ensembleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MailService mailService;

    private Ensemble ensemble;

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private InvitationRepository invitationRepository;

    @BeforeEach
    void setUp() {
        doNothing().when(mailService)
                .sendInvitationEmail(anyString(), anyString());

        ensemble = new Ensemble();
        ensemble.setNom("Ensemble Test");
        ensemble = ensembleRepository.save(ensemble);
    }

    @Test
    void testInvitationTriggersEmailSending() throws Exception {
        CreateInvitationDTO dto = new CreateInvitationDTO();
        dto.setEmailInvite("invite@example.com");
        dto.setEnsembleId(ensemble.getId());

        mockMvc.perform(post("/api/invitations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(mailService, times(1))
                .sendInvitationEmail(eq("invite@example.com"), anyString());
    }

    
private Invitation creerInvitationTest(
        String email,
        LocalDateTime expiration,
        LocalDateTime utilisation) {

    Invitation invitation = new Invitation();
    invitation.setEmailInvite(email);
    invitation.setEnsemble(ensemble);
    invitation.setToken(UUID.randomUUID().toString());
    invitation.setDateEnvoi(LocalDateTime.now());
    invitation.setDateExpiration(expiration);
    invitation.setDateUtilisation(utilisation);
    invitation.setEtat(StatusInvitation.EN_ATTENTE);

    return invitationRepository.save(invitation);
}

@Test
void testInvitationExpireeRefusee() {
    Invitation invitation = creerInvitationTest(
            "invite-expire@example.com",
            LocalDateTime.now().minusDays(1),
            null);

    Utilisateur utilisateur = new Utilisateur();
    utilisateur.setEmail("invite-expire@example.com");

    RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> invitationService.rattacherUtilisateurApresInscription(
                    utilisateur, invitation));

    assertEquals(
            "Le lien d'invitation a expiré.",
            exception.getMessage());
}

@Test
void testInvitationDejaUtiliseeRefusee() {
    Invitation invitation = creerInvitationTest(
            "invite-utilise@example.com",
            LocalDateTime.now().plusDays(7),
            LocalDateTime.now().minusMinutes(1));

    Utilisateur utilisateur = new Utilisateur();
    utilisateur.setEmail("invite-utilise@example.com");

    RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> invitationService.rattacherUtilisateurApresInscription(
                    utilisateur, invitation));

    assertEquals(
            "Cette invitation a déjà été utilisée.",
            exception.getMessage());
}

@Test
void testEmailDifferentRefuse() {
    Invitation invitation = creerInvitationTest(
            "alice@example.com",
            LocalDateTime.now().plusDays(7),
            null);

   Utilisateur utilisateur = new Utilisateur();
utilisateur.setEmail("bob@example.com");
utilisateur.setMotDePasse("MotDePasseTest123!");

    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> invitationService.rattacherUtilisateurApresInscription(
                    utilisateur, invitation));

    assertEquals(
            "L'adresse e-mail ne correspond pas à l'invitation.",
            exception.getMessage());
}

}
