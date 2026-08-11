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

       String prompt = "Génère un court descriptif de 2 phrases maximum (moins de 200 caractères) pour un morceau de musique.\n" +
        "Titre : " + titre + "\n" +
        "Compositeur : " + compositeur + "\n" +
        "Genre : " + genre + "\n" +
        "Réponds uniquement avec le descriptif, sans introduction.";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + anthropicConfig.getApiKey());

        Map<String, Object> body = Map.of(
            "model", "llama-3.3-70b-versatile",
            "messages", List.of(Map.of("role", "user", "content", prompt))
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
            "https://api.groq.com/openai/v1/chat/completions",
            request,
            Map.class
        );

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        return (String) message.get("content");
    }
}