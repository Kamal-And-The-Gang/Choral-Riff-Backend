
package fr.afpa.choral_riff.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.afpa.choral_riff.dto.RegisterDto;
import fr.afpa.choral_riff.dto.UtilisateurDto;
import fr.afpa.choral_riff.entity.Ensemble;
import fr.afpa.choral_riff.entity.Invitation;
import fr.afpa.choral_riff.entity.StatusInvitation;
import fr.afpa.choral_riff.repositories.DocumentRepository;
import fr.afpa.choral_riff.repositories.EnsembleRepository;
import fr.afpa.choral_riff.repositories.InvitationRepository;
import fr.afpa.choral_riff.repositories.MorceauRepository;
import fr.afpa.choral_riff.repositories.NotificationRepository;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test d'intégration pour l'inscription via invitation.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class InscriptionAvecInvitationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private EnsembleRepository ensembleRepository;

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private MorceauRepository morceauRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Invitation invitation;
    private Ensemble ensemble;

    @BeforeEach
    public void setup() {
        // Supprimer d'abord les données dépendantes des utilisateurs.
        notificationRepository.deleteAll();
        invitationRepository.deleteAll();
        documentRepository.deleteAll();
        morceauRepository.deleteAll();

        // Supprimer ensuite les utilisateurs et les ensembles.
        utilisateurRepository.deleteAll();
        ensembleRepository.deleteAll();

        ensemble = new Ensemble();
        ensemble.setNom("Chorale Test");
        ensembleRepository.save(ensemble);

        invitation = new Invitation();
        invitation.setEmailInvite("testuser@example.com");
        invitation.setToken("token-test-123");
        invitation.setEtat(StatusInvitation.EN_ATTENTE);
        invitation.setDateEnvoi(LocalDateTime.now());
        invitation.setDateExpiration(LocalDateTime.now().plusDays(7));
        invitation.setEnsemble(ensemble);

        invitationRepository.save(invitation);
    }

    @Test
    public void testInscriptionAvecTokenInvitation() throws Exception {
        RegisterDto dto = new RegisterDto();
        dto.setEmail("testuser@example.com");
        dto.setNom("Test");
        dto.setPrenom("Utilisateur");
        dto.setMotDePasse("password123");
        dto.setToken("token-test-123");

        mockMvc.perform(
                post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        var utilisateur = utilisateurRepository
                .findByEmail("testuser@example.com")
                .orElseThrow(() -> new AssertionError("Utilisateur non créé"));

        assertNotNull(utilisateur);
        assertEquals("testuser@example.com", utilisateur.getEmail());

        UtilisateurDto utilisateurDto = new UtilisateurDto();
        utilisateurDto.setEmail("testuser@example.com");

        mockMvc.perform(
                post("/api/invitations/rattacher-apres-inscription")
                        .param("token", "token-test-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(utilisateurDto)))
                .andExpect(status().isOk());

        Invitation updatedInvitation = invitationRepository
                .findByToken("token-test-123")
                .orElseThrow(() -> new AssertionError("Invitation non trouvée"));

        assertEquals(StatusInvitation.ACCEPTEE, updatedInvitation.getEtat());
        assertNotNull(updatedInvitation.getUtilisateur());
        assertEquals("testuser@example.com", updatedInvitation.getUtilisateur().getEmail());
        assertNotNull(updatedInvitation.getDateUtilisation());
    }
}
