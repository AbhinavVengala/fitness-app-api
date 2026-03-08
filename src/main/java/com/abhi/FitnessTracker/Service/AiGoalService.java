package com.abhi.FitnessTracker.Service;

import com.abhi.FitnessTracker.Model.Goals;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for generating AI-powered daily goal suggestions (macros/water).
 */
@Service
public class AiGoalService {

    @Value("${gemini.api-key:}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    private static final String SYSTEM_PROMPT = """
            You are an expert, certified nutritionist and fitness coach.
            Your job is to receive a user's physical profile and fitness goals, and calculate scientifically-sound daily targets:
            - Daily Calories (kcal)
            - Protein (g)
            - Carbs (g)
            - Fats (g)
            - Water (glasses, assuming 1 glass = 250ml)

            Constraints:
            - Respond ONLY with a valid JSON object matching this exact schema:
            {
              "calories": integer,
              "protein": integer,
              "carbs": integer,
              "fats": integer,
              "water": integer
            }
            - Do not include any conversational text, markdown formatting blocks (like ```json), or explanations. Just the raw JSON object.
            - Ensure macros (Protein*4 + Carbs*4 + Fats*9) roughly equal the total calories.
            """;

    /**
     * Generate suggested goals based on user profile context.
     */
    public Goals suggestGoals(Map<String, Object> profileContext) {
        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            throw new RuntimeException("Gemini API key is not configured.");
        }

        try {
            // 1. Build context string from the request payload
            String userData = String.format("""
                    Please suggest daily targets for the following user:
                    - Age: %s
                    - Gender: %s
                    - Weight: %s kg
                    - Height: %s cm
                    - Primary Fitness Goal: %s
                    - Experience Level: %s
                    """,
                    profileContext.getOrDefault("age", "30"),
                    profileContext.getOrDefault("gender", "male"),
                    profileContext.getOrDefault("weight", "70"),
                    profileContext.getOrDefault("height", "170"),
                    profileContext.getOrDefault("fitnessGoal", "generalFitness"),
                    profileContext.getOrDefault("experienceLevel", "beginner")
            );

            // 2. Call Gemini
            String rawJsonResponse = callGemini(userData);

            // 3. Clean up the response just in case the AI added markdown blocks
            String cleanedJson = rawJsonResponse.replaceAll("(?s)```json\\s*", "")
                                                .replaceAll("(?s)```\\s*", "")
                                                .trim();

            // 4. Parse the JSON strings back into our Goals object
            JsonNode jsonNode = objectMapper.readTree(cleanedJson);
            
            Goals suggestedGoals = new Goals();
            suggestedGoals.setCalories(jsonNode.path("calories").asInt(2000));
            suggestedGoals.setProtein(jsonNode.path("protein").asInt(100));
            suggestedGoals.setCarbs(jsonNode.path("carbs").asInt(200));
            suggestedGoals.setFats(jsonNode.path("fats").asInt(65));
            suggestedGoals.setWater(jsonNode.path("water").asInt(8));

            return suggestedGoals;

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate goal suggestions: " + e.getMessage(), e);
        }
    }

    private String callGemini(String userData) {
        Map<String, Object> systemInstruction = new HashMap<>();
        systemInstruction.put("parts", List.of(Map.of("text", SYSTEM_PROMPT)));

        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("parts", List.of(Map.of("text", userData)));

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.2); // Low temperature for more deterministic/mathematical outputs
        // Optionally enforce JSON response MIME type if supported natively, but prompt instructions usually suffice for Gemini Flash.
        generationConfig.put("responseMimeType", "application/json"); 

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("system_instruction", systemInstruction);
        requestBody.put("contents", List.of(userMessage));
        requestBody.put("generationConfig", generationConfig);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        String url = GEMINI_API_URL + "?key=" + geminiApiKey;
        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.POST, entity, JsonNode.class);

        if (response.getBody() == null) {
            throw new RuntimeException("Empty response from Gemini API");
        }

        return extractContent(response.getBody());
    }

    private String extractContent(JsonNode responseBody) {
        JsonNode candidates = responseBody.path("candidates");
        if (candidates.isArray() && !candidates.isEmpty()) {
            JsonNode content = candidates.get(0).path("content").path("parts");
            if (content.isArray() && !content.isEmpty()) {
                return content.get(0).path("text").asText();
            }
        }
        throw new RuntimeException("Could not extract content from Gemini response");
    }
}
