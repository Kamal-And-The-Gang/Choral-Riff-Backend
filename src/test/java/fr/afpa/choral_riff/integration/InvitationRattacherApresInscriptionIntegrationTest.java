
// // package fr.afpa.choral_riff.integration;

// // import com.fasterxml.jackson.databind.ObjectMapper;
// // import fr.afpa.choral_riff.dto.UtilisateurDto;
// // import fr.afpa.choral_riff.entity.Ensemble;
// // import fr.afpa.choral_riff.entity.Invitation;
// // import fr.afpa.choral_riff.entity.Utilisateur;
// // import fr.afpa.choral_riff.repositories.EnsembleRepository;
// // import fr.afpa.choral_riff.repositories.InvitationRepository;
// // import fr.afpa.choral_riff.repositories.UtilisateurRepository;
// // import jakarta.transaction.Transactional;

// // import org.junit.jupiter.api.BeforeEach;
// // import org.junit.jupiter.api.Test;
// // import org.springframework.beans.factory.annotation.Autowired;
// // import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// // import org.springframework.boot.test.context.SpringBootTest;
// // import org.springframework.http.MediaType;
// // import org.springframework.test.web.servlet.MockMvc;

// // import java.time.LocalDateTime;
// // import java.util.UUID;

// // import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// // import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// // @SpringBootTest
// // @AutoConfigureMockMvc
// // @Transactional
// // public class InvitationRattacherApresInscriptionIntegrationTest {

// //     @Autowired
// //     private MockMvc mockMvc;

// //     @Autowired
// //     private EnsembleRepository ensembleRepository;

// //     @Autowired
// //     private UtilisateurRepository utilisateurRepository;

// //     @Autowired
// //     private InvitationRepository invitationRepository;

// //     @Autowired
// //     private ObjectMapper objectMapper;

// //     private Invitation invitation;

// //     private Utilisateur utilisateur;

// //     @BeforeEach
// //     public void setUp() {
// //         // Crée un ensemble fictif
// //         Ensemble ensemble = new Ensemble();
// //         ensemble.setNom("Ensemble Test");
// //         ensemble = ensembleRepository.save(ensemble);

// //         // Crée un utilisateur existant avec mot de passe obligatoire
// //         utilisateur = new Utilisateur();
// //         utilisateur.setNom("New");
// //         utilisateur.setPrenom("User");
// //         utilisateur.setEmail("newuser+" + UUID.randomUUID() + "@example.com"); // <- unique
// //         utilisateur.setMotDePasse("Password123!");
// //         utilisateur = utilisateurRepository.save(utilisateur);

// //         // Crée une invitation fictive associée à cet utilisateur
// //         invitation = new Invitation();
// //         invitation.setEmailInvite(utilisateur.getEmail());
// //         invitation.setToken(UUID.randomUUID().toString());
// //         invitation.setDateEnvoi(LocalDateTime.now());
// //         invitation.setDateExpiration(LocalDateTime.now().plusDays(7));
// //         invitation.setEtat(fr.afpa.choral_riff.entity.StatusInvitation.EN_ATTENTE);
// //         invitation.setEnsemble(ensemble);
// //         invitationRepository.save(invitation);
// //     }

// //     @Test
// //     public void testRattacherApresInscription() throws Exception {

// //         // On utilise le DTO correspondant à l'utilisateur existant
// //         UtilisateurDto fakeUser = new UtilisateurDto();
// //         fakeUser.setEmail(utilisateur.getEmail());

// //         mockMvc.perform(post("/api/invitations/rattacher-apres-inscription")
// //                 .param("token", invitation.getToken())
// //                 .contentType(MediaType.APPLICATION_JSON)
// //                 .content(objectMapper.writeValueAsString(fakeUser)))
// //                 .andExpect(status().isOk());
// //     }
// // }

// package fr.afpa.choral_riff.integration;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import fr.afpa.choral_riff.dto.UtilisateurDto;
// import fr.afpa.choral_riff.entity.Ensemble;
// import fr.afpa.choral_riff.entity.Invitation;
// import fr.afpa.choral_riff.entity.Utilisateur;
// import fr.afpa.choral_riff.repositories.EnsembleRepository;
// import fr.afpa.choral_riff.repositories.InvitationRepository;
// import fr.afpa.choral_riff.repositories.UtilisateurRepository;
// import jakarta.transaction.Transactional;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;

// import java.time.LocalDateTime;
// import java.util.UUID;

// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @SpringBootTest
// @AutoConfigureMockMvc
// @Transactional
// public class InvitationRattacherApresInscriptionIntegrationTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @Autowired
//     private EnsembleRepository ensembleRepository;

//     @Autowired
//     private UtilisateurRepository utilisateurRepository;

//     @Autowired
//     private InvitationRepository invitationRepository;

//     @Autowired
//     private ObjectMapper objectMapper;

//     private Invitation invitation;

//     private Utilisateur utilisateur;

//     @BeforeEach
//     public void setUp() {
//         // Crée un ensemble fictif
//         Ensemble ensemble = new Ensemble();
//         ensemble.setNom("Ensemble Test");
//         ensemble = ensembleRepository.save(ensemble);

//         // Crée un utilisateur existant avec mot de passe obligatoire
//         utilisateur = new Utilisateur();
//         utilisateur.setNom("New");
//         utilisateur.setPrenom("User");
//         utilisateur.setEmail("newuser+" + UUID.randomUUID() + "@example.com"); // email unique
//         utilisateur.setMotDePasse("Password123!"); // ⚠️ obligatoire pour passer le NOT NULL
//         utilisateur = utilisateurRepository.save(utilisateur);

//         // Crée une invitation fictive associée à cet utilisateur
//         invitation = new Invitation();
//         invitation.setEmailInvite(utilisateur.getEmail());
//         invitation.setToken(UUID.randomUUID().toString());
//         invitation.setDateEnvoi(LocalDateTime.now());
//         invitation.setDateExpiration(LocalDateTime.now().plusDays(7));
//         invitation.setEtat(fr.afpa.choral_riff.entity.StatusInvitation.EN_ATTENTE);
//         invitation.setEnsemble(ensemble);
//         invitationRepository.save(invitation);
//     }

//     @Test
//     public void testRattacherApresInscription() throws Exception {

//         // On utilise le DTO correspondant à l'utilisateur existant
//         UtilisateurDto fakeUser = new UtilisateurDto();
//         fakeUser.setEmail(utilisateur.getEmail());

//         mockMvc.perform(post("/api/invitations/rattacher-apres-inscription")
//                 .param("token", invitation.getToken())
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(fakeUser)))
//                 .andExpect(status().isOk());
//     }
// }