
package fr.afpa.choral_riff.integration;

import fr.afpa.choral_riff.dto.RegisterDto;
import fr.afpa.choral_riff.dto.UtilisateurDto;
import fr.afpa.choral_riff.entity.Ensemble;
import fr.afpa.choral_riff.entity.Invitation;
import fr.afpa.choral_riff.entity.StatusInvitation;
import fr.afpa.choral_riff.repositories.EnsembleRepository;
import fr.afpa.choral_riff.repositories.InvitationRepository;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
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
    private ObjectMapper objectMapper;

    private Invitation invitation;
    private Ensemble ensemble;

    @BeforeEach
    public void setup() {

        // Nettoyage base
        invitationRepository.deleteAll();
        utilisateurRepository.deleteAll();
        ensembleRepository.deleteAll();

        // Création ensemble
        ensemble = new Ensemble();
        ensemble.setNom("Chorale Test");
        ensembleRepository.save(ensemble);

        // Création invitation
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

        // --------  inscription utilisateur --------
        RegisterDto dto = new RegisterDto();
        dto.setEmail("testuser@example.com");
        dto.setNom("Test");
        dto.setPrenom("Utilisateur");
        dto.setMotDePasse("password123");
        dto.setToken("token-test-123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        // Vérifier que l'utilisateur a bien été créé
        utilisateurRepository.findByEmail("testuser@example.com")
                .orElseThrow(() -> new AssertionError("Utilisateur non créé"));

        // --------  rattachement via invitation --------
        UtilisateurDto utilisateurDto = new UtilisateurDto();
        utilisateurDto.setEmail("testuser@example.com");

        mockMvc.perform(post("/api/invitations/rattacher-apres-inscription")
                .param("token", "token-test-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(utilisateurDto)))
                .andExpect(status().isOk());

        // --------  vérification invitation --------
        Invitation updatedInvitation = invitationRepository
                .findByToken("token-test-123")
                .orElseThrow(() -> new AssertionError("Invitation non trouvée"));

        assertEquals(StatusInvitation.ACCEPTEE,
                updatedInvitation.getEtat(),
                "L'état de l'invitation doit être ACCEPTEE");

        assertNotNull(updatedInvitation.getUtilisateur(),
                "L'utilisateur doit être associé à l'invitation");

        assertNotNull(updatedInvitation.getDateUtilisation(),
                "La date d'utilisation doit être renseignée");
    }
}

