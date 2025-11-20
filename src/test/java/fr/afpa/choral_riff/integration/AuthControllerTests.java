package fr.afpa.choral_riff.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.afpa.choral_riff.dto.LoginDTO;
import fr.afpa.choral_riff.dto.RegisterDto;
import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;





@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@EnableTransactionManagement
class AuthControllerTests {

    @Autowired
    private WebApplicationContext context;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UtilisateurRepository userRepository;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    void setupDatabase() {

        Utilisateur user = new Utilisateur();
        user.setEmail("newuser@example.com");
        user.setPrenom("Bob");
        user.setNom("Bub");
        user.setMotDePasse(passwordEncoder.encode("password"));

        userRepository.save(user);
    }

    // ObjectMapper : convertit les objets Java (DTO) en JSON (utilisé pour envoyer
    // le @RequestBody)
    // Méthode exécutée avant chaque test
    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Transactional
    @Test
    // Création d’un objet de connexion avec un email et mot de passe.
    void login_should_return_tokens() throws Exception {
        LoginDTO login = new LoginDTO();
        login.setEmail("newuser@example.com");
        login.setPassword("password");

        // Envoie une requête POST vers /api/auth/login avec le corps JSON du login.

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void register_should_return_created_user() throws Exception {
        RegisterDto dto = new RegisterDto();
        dto.setNom("Test");
        dto.setPrenom("User");
        dto.setEmail("user" + System.currentTimeMillis() + "@example.com"); // Email unique à chaque fois
        dto.setMotDePasse("password");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value(dto.getEmail()))
                .andExpect(jsonPath("$.nom").value("Test"));
    }

    @Test
    void logout_should_return_confirmation() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Déconnexion réussie")); // on vérifie la valeur exacte
    }
}
