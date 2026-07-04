package com.interview.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.ai.config.GeminiConfig;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Thin wrapper around Google's Gemini "generateContent" REST endpoint.
 */
@Service
public class GeminiService {

    private final RestTemplate restTemplate;
    private final GeminiConfig geminiConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GeminiService(RestTemplate restTemplate, GeminiConfig geminiConfig) {
        this.restTemplate = restTemplate;
        this.geminiConfig = geminiConfig;
    }

    /**
     * Sends a prompt to Gemini and returns the raw text of the model's reply.
     */
    public String generateContent(String prompt) {
        String url = geminiConfig.getApiUrl() + "?key=" + geminiConfig.getApiKey();

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        JsonNode response = restTemplate.postForObject(url, entity, JsonNode.class);

        if (response == null) {
            throw new IllegalStateException("Empty response from Gemini API");
        }

        JsonNode textNode = response
                .path("candidates").path(0)
                .path("content").path("parts").path(0)
                .path("text");

        if (textNode.isMissingNode()) {
            throw new IllegalStateException("Unexpected Gemini API response format: " + response);
        }

        return textNode.asText().trim();
    }

    /** Strips ```json fences if the model wraps its JSON reply in markdown. */
    public String cleanJson(String raw) {
        return raw.replaceAll("(?i)```json", "")
                .replace("```", "")
                .trim();
    }
}
