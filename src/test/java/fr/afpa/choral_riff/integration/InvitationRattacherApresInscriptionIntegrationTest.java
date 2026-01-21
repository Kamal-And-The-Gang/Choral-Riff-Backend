package fr.afpa.choral_riff.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.afpa.choral_riff.dto.InvitationDTO;
import fr.afpa.choral_riff.entity.Ensemble;
import fr.afpa.choral_riff.entity.Invitation;
import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.entity.StatusInvitation;
import fr.afpa.choral_riff.repositories.EnsembleRepository;
import fr.afpa.choral_riff.repositories.InvitationRepository;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import jakarta.transaction.Transactional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class InvitationRattacherApresInscriptionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EnsembleRepository ensembleRepository;

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Ensemble ensemble;
    private Invitation invitation;
    private String token = "TOKEN_TEST_123";

    @BeforeEach
    void setUp() {
        // création d'un ensemble
        ensemble = new Ensemble();
        ensemble.setNom("Ensemble Test");
        ensemble = ensembleRepository.save(ensemble);

        // création d'une invitation sans utilisateur
        invitation = new Invitation();
        invitation.setEnsemble(ensemble);
        invitation.setEmailInvite("invite@test.com");
        invitation.setToken(token);
        invitation.setEtat(StatusInvitation.EN_ATTENTE);
        invitationRepository.save(invitation);
    }

    @Test
    void testRattacherApresInscription() throws Exception {

        // utilisateur à envoyer en body
        Utilisateur nouvelUtilisateur = new Utilisateur();
        nouvelUtilisateur.setNom("Doe");
        nouvelUtilisateur.setPrenom("John");
        nouvelUtilisateur.setEmail("john.doe@test.com");
        nouvelUtilisateur.setMotDePasse("password123");

        // appel de l'endpoint
        String response = mockMvc.perform(post("/api/invitations/rattacher-apres-inscription")
                        .param("token", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nouvelUtilisateur)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // vérifier que la réponse contient une invitation DTO
        InvitationDTO dto = objectMapper.readValue(response, InvitationDTO.class);
        assertThat(dto).isNotNull();
        assertThat(dto.getEmailInvite()).isEqualTo("invite@test.com");

        // vérifier que l'utilisateur a été créé
        Utilisateur createdUser = utilisateurRepository.findByEmail("john.doe@test.com").orElse(null);
        assertThat(createdUser).isNotNull();

        // vérifier que l'invitation a bien été rattachée
        Invitation updatedInvitation = invitationRepository.findById(invitation.getId()).orElse(null);
        assertThat(updatedInvitation).isNotNull();
        assertThat(updatedInvitation.getUtilisateur()).isNotNull();
        assertThat(updatedInvitation.getUtilisateur().getEmail()).isEqualTo("john.doe@test.com");
    }
}
