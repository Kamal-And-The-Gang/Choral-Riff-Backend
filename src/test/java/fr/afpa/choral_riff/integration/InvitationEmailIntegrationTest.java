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
// import
// org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;

// import static org.mockito.Mockito.*;
// import static
// org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static
// org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @SpringBootTest
// @AutoConfigureMockMvc
// public class InvitationEmailIntegrationTest {

// @Autowired
// private MockMvc mockMvc;

// @Autowired
// private EnsembleRepository ensembleRepository;

// @Autowired
// private ObjectMapper objectMapper;

// @Mock
// private MailService mailService;

// private Ensemble ensemble;

// @BeforeEach
// void setUp() {
// ensembleRepository.deleteByNom("Ensemble Test");
// ensemble = new Ensemble();
// ensemble.setNom("Ensemble Test");
// ensemble = ensembleRepository.save(ensemble);

// // Mock du comportement du mail service (ne fait rien)
// doNothing().when(mailService).sendInvitationEmail(anyString(), anyString());
// }

// @Test
// void testInvitationTriggersEmailSending() throws Exception {
// InvitationDTO dto = new InvitationDTO();
// dto.setEmailInvite("invite@example.com");
// dto.setEnsembleId(ensemble.getId());

// mockMvc.perform(post("/api/invitations")
// .contentType(MediaType.APPLICATION_JSON)
// .content(objectMapper.writeValueAsString(dto)))
// .andExpect(status().isCreated());

// // Est-ce- que l’email a bien été "envoyé"=> Vérification
// verify(mailService, times(1)).sendInvitationEmail(eq("invite@example.com"),
// anyString());
// }
// }
