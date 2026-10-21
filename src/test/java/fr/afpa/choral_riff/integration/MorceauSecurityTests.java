
package fr.afpa.choral_riff.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;

@SpringBootTest
class MorceauSecurityTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void suppression_sans_authentification_doit_etre_refusee() throws Exception {
        mockMvc.perform(delete("/api/morceaux/999999"))
                .andExpect(status().isForbidden());
    }

    
@Test
void suppression_avec_faux_jeton_doit_etre_refusee() throws Exception {
    mockMvc.perform(delete("/api/morceaux/999999")
            .header("Authorization", "Bearer faux.jeton.invalide"))
            .andExpect(status().isForbidden());
}

}

