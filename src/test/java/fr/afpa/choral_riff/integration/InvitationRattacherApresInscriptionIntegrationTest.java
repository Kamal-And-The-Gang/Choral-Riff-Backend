package fr.afpa.choral_riff.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.afpa.choral_riff.dto.UtilisateurDto;
import fr.afpa.choral_riff.entity.Ensemble;
import fr.afpa.choral_riff.entity.Invitation;
import fr.afpa.choral_riff.repositories.EnsembleRepository;
import fr.afpa.choral_riff.repositories.InvitationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class InvitationRattacherApresInscriptionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EnsembleRepository ensembleRepository; // ← ajouté

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Invitation invitation;

   @BeforeEach
public void setUp() {
    invitationRepository.deleteAll();

    // 1️⃣ Crée un ensemble fictif
    Ensemble ensemble = new Ensemble();
    ensemble.setNom("Ensemble Test");
    ensemble = ensembleRepository.save(ensemble); // persister pour avoir un ID

    // 2️⃣ Crée une invitation fictive
    invitation = new Invitation();
    invitation.setEmailInvite("newuser@example.com");
    invitation.setToken(UUID.randomUUID().toString());
    invitation.setDateEnvoi(LocalDateTime.now());
    invitation.setDateExpiration(LocalDateTime.now().plusDays(7));
    invitation.setEtat(fr.afpa.choral_riff.entity.StatusInvitation.EN_ATTENTE);

    invitation.setEnsemble(ensemble); // ⚡ très important
    invitationRepository.save(invitation);
}

    @Test
    public void testRattacherApresInscription() throws Exception {

        // Création d'un utilisateur fictif minimal (DTO)
        UtilisateurDto fakeUser = new UtilisateurDto();
        fakeUser.setNom("New");
        fakeUser.setPrenom("User");
        fakeUser.setEmail("newuser@example.com");
        fakeUser.setPhotoProfil(null);

        mockMvc.perform(post("/api/invitations/rattacher-apres-inscription")
                        .param("token", invitation.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fakeUser)))
                .andExpect(status().isOk()); 
    }
}