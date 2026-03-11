
package fr.afpa.choral_riff.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.afpa.choral_riff.entity.Ensemble;
import fr.afpa.choral_riff.entity.Invitation;
import fr.afpa.choral_riff.entity.StatusInvitation;
import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.repositories.EnsembleRepository;
import fr.afpa.choral_riff.repositories.InvitationRepository;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;
import jakarta.transaction.Transactional;
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
@Transactional
public class InvitationRattacherApresInscriptionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EnsembleRepository ensembleRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Invitation invitation;

    private Utilisateur utilisateur;

    @BeforeEach
    public void setUp() {
        // Crée un ensemble fictif
        Ensemble ensemble = new Ensemble();
        ensemble.setNom("Ensemble Test");
        ensemble = ensembleRepository.save(ensemble);

        //  Crée un utilisateur existant avec mot de passe (pour la base)
        utilisateur = new Utilisateur();
        utilisateur.setNom("New");
        utilisateur.setPrenom("User");
        utilisateur.setEmail("newuser+" + UUID.randomUUID() + "@example.com"); // email unique
        utilisateur.setMotDePasse("Password123!"); // obligatoire
        utilisateur = utilisateurRepository.save(utilisateur);

        // Crée une invitation associée à cet utilisateur
        invitation = new Invitation();
        invitation.setEmailInvite(utilisateur.getEmail());
        invitation.setToken(UUID.randomUUID().toString());
        invitation.setDateEnvoi(LocalDateTime.now());
        invitation.setDateExpiration(LocalDateTime.now().plusDays(7));
        invitation.setEtat(StatusInvitation.EN_ATTENTE);
        invitation.setEnsemble(ensemble);
        invitationRepository.save(invitation);
    }

    @Test
    public void testRattacherApresInscription() throws Exception {
        //  Prépare l'objet Utilisateur complet (comme attendu par le controller)
        Utilisateur nouvelUtilisateur = new Utilisateur();
        nouvelUtilisateur.setNom("New");
        nouvelUtilisateur.setPrenom("User");
        nouvelUtilisateur.setEmail(utilisateur.getEmail());
        nouvelUtilisateur.setMotDePasse("Password123!"); // obligatoire NOT NULL

        //  Appel MockMvc du endpoint /rattacher-apres-inscription
        mockMvc.perform(post("/api/invitations/rattacher-apres-inscription")
                        .param("token", invitation.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nouvelUtilisateur)))
                .andExpect(status().isOk()); //  Vérifie OK
    }
}