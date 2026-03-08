package com.abhi.FitnessTracker.Service;

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
 * Service for generating AI-powered workout plans based on profile and recent activity.
 */
@Service
public class AiWorkoutPlanService {

    @Value("${gemini.api-key:}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    private static final String SYSTEM_PROMPT = """
            You are an elite personal trainer and strength coach.
            The user is asking for a personalized workout routine based on their fitness profile and preferences.
            
            Guidelines for your response:
            1. Recommend an effective workout routine containing 4 to 7 exercises.
            2. For each exercise, explicitly specify the recommended Sets, Reps (or duration), and Rest times.
            3. Take into account their experience level and primary fitness goal.
            4. If they provided specific equipment or constraints in their prompt (e.g., "dumbbells only", "30 mins"), adhere strictly to them.
            5. Keep your response concise, motivating, and format it beautifully in Markdown using emojis, headers, and bullet points for easy readability on a mobile screen.
            """;

    /**
     * Generate a workout plan.
     */
    public String generateWorkoutPlan(Map<String, Object> requestPayload) {
        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            throw new RuntimeException("Gemini API key is not configured.");
        }

        try {
            // Build context string from the request payload
            Map<String, Object> profile = (Map<String, Object>) requestPayload.getOrDefault("profile", new HashMap<>());
            String userQuery = (String) requestPayload.getOrDefault("query", "Suggest a full body workout");

            String userData = String.format("""
                    --- USER CONTEXT ---
                    Profile: %s, %s, %s kg, %s cm
                    Fitness Goal: %s
                    Experience Level: %s
                    
                    --- USER REQUEST ---
                    "%s"
                    """,
                    profile.get("age"), profile.get("gender"), profile.get("weight"), profile.get("height"),
                    profile.get("fitnessGoal"), profile.get("experienceLevel"),
                    userQuery
            );

            return callGemini(userData);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate workout plan: " + e.getMessage(), e);
        }
    }

    private String callGemini(String userData) {
        Map<String, Object> systemInstruction = new HashMap<>();
        systemInstruction.put("parts", List.of(Map.of("text", SYSTEM_PROMPT)));

        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("parts", List.of(Map.of("text", userData)));

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.6); // slight creativity for variety

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
