package tn.fst.spring.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Service
public class GeminiService {

    private final WebClient webClient;

    @Value("${gemini.api.key}") // Inject API key from properties
    private String apiKey;

    @Value("${gemini.api.url}") // Inject API URL from properties
    private String apiUrl;

    private static final String SYSTEM_PROMPT = 
        "You are an AI assistant for \"Consommi Tounsi\". Your sole purpose is to answer user questions about Tunisian products based on the provided data and promote them.\n" +
        "Strictly adhere to the following rules:\n" +
        "1. ONLY discuss products available within the Consommi Tounsi context.\n" +
        "2. Use the provided product data to answer questions accurately.\n" +
        "3. Actively promote the products when answering questions.\n" +
        "4. NEVER engage in conversations about topics outside the scope of Consommi Tounsi products. If asked about anything else, politely state that you can only discuss Consommi Tounsi products.\n" +
        "5. Be helpful and informative regarding the products.";

    @Autowired
    public GeminiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String getGeminiResponse(String userQuery, String productData) {
        // Combine system prompt, product data, and user query into a single input text
        // The Generative Language API (v1beta) doesn't have a dedicated system instruction field like Vertex AI SDK
        String combinedInput = SYSTEM_PROMPT + 
                             "\n\n--- Product Data ---\n" + 
                             productData + 
                             "\n\n--- User Question ---\n" + 
                             userQuery;

        GeminiRequestPart part = new GeminiRequestPart(combinedInput);
        GeminiRequestContent content = new GeminiRequestContent(Collections.singletonList(part));
        GeminiRequest requestPayload = new GeminiRequest(Collections.singletonList(content));

        try {
            GeminiResponse response = webClient.post()
                    .uri(apiUrl + "?key=" + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(requestPayload), GeminiRequest.class)
                    .retrieve()
                    .bodyToMono(GeminiResponse.class)
                    .block(); // Using block() for simplicity in a synchronous service method
                               // Consider reactive approaches if used in a fully reactive stack

            // Extract text from response
            if (response != null && response.getCandidates() != null && !response.getCandidates().isEmpty()) {
                GeminiResponseContent candidateContent = response.getCandidates().get(0).getContent();
                if (candidateContent != null && candidateContent.getParts() != null && !candidateContent.getParts().isEmpty()) {
                    return candidateContent.getParts().get(0).getText();
                }
            }
            return "Sorry, I received an empty or invalid response.";

        } catch (Exception e) {
            // Basic error handling, consider more specific logging/exception management
            System.err.println("Error calling Gemini API: " + e.getMessage());
            return "Sorry, there was an error communicating with the AI service.";
        }
    }

    // --- DTO Classes for JSON Mapping ---

    @Data // Lombok annotation for getters, setters, toString, etc.
    @NoArgsConstructor
    @AllArgsConstructor
    private static class GeminiRequest {
        private List<GeminiRequestContent> contents;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class GeminiRequestContent {
        private List<GeminiRequestPart> parts;
        // role is implicitly "user" in this simple structure
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class GeminiRequestPart {
        private String text;
    }

    // --- Response DTOs ---
    // (Simplified based on typical Gemini API responses)
    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class GeminiResponse {
        private List<GeminiCandidate> candidates;
        // Add promptFeedback if needed
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class GeminiCandidate {
        private GeminiResponseContent content;
        private String finishReason;
        // Add index, safetyRatings etc. if needed
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class GeminiResponseContent {
        private List<GeminiResponsePart> parts;
        private String role; // Should be "model"
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class GeminiResponsePart {
        private String text;
    }
} 