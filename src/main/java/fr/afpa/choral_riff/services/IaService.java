package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.config.AnthropicConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.Map;
import java.util.List;

@Service
public class IaService {

    private final AnthropicConfig anthropicConfig;
    private final RestTemplate restTemplate;

    public IaService(AnthropicConfig anthropicConfig) {
        this.anthropicConfig = anthropicConfig;
        this.restTemplate = new RestTemplate();
    }

    public String genererDescriptif(String titre, String compositeur, String genre) {

       String prompt = """
        Tu es un assistant musical professionnel.

        Rédige une description courte et précise du morceau à partir de son
        titre, de son compositeur et de son genre.

        Utilise tes connaissances musicales générales pour identifier le morceau
        lorsqu'il est connu.

        La description doit :
        - tenir en une seule phrase ;
        - faire moins de 180 caractères ;
        - être destinée à des musiciens ;
        - mentionner si possible le style, la structure, le rythme,
          l'instrumentation ou une caractéristique musicale importante ;
        - rester factuelle et professionnelle ;
        - ne pas utiliser de formulations commerciales ou poétiques.

        Si le morceau est connu, donne des informations musicales concrètes.
        Si tu ne connais pas suffisamment le morceau, reste général plutôt que
        d'inventer des informations.

        Titre : %s
        Compositeur / Artiste : %s
        Genre : %s

        Réponds uniquement avec le descriptif, sans introduction ni guillemets.
        """.formatted(titre, compositeur, genre);

        

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + anthropicConfig.getApiKey());

        Map<String, Object> body = Map.of(
                "model", "openai/gpt-oss-20b",

                "messages", List.of(Map.of("role", "user", "content", prompt)));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.groq.com/openai/v1/chat/completions",
                request,
                Map.class);

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        return (String) message.get("content");
    }
}
