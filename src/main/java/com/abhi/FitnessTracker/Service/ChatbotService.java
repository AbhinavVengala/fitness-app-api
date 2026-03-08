package com.abhi.FitnessTracker.Service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * AI Fitness Chatbot service using Google Gemini API.
 * Provides personalized fitness coaching with user context awareness.
 */
@Service
public class ChatbotService {

    @Value("${gemini.api-key:}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    private static final String SYSTEM_PROMPT = """
            You are **FitCoach AI**, a friendly, expert personal fitness and nutrition coach embedded in the PacePlate fitness tracking app.

            ## Your Personality
            - Warm, encouraging, and motivational — like a supportive gym buddy who also has a nutrition degree
            - Use emojis sparingly but naturally (💪, 🔥, 🥗, 💧, ⭐)
            - Keep responses concise and actionable (2-4 short paragraphs max)
            - Use bullet points for lists

            ## Your Capabilities
            - Personalized diet advice based on the user's goals, macros, and what they've eaten today
            - Workout suggestions tailored to their experience level and fitness goal
            - Exercise form tips and alternatives
            - Motivation and accountability
            - Answering general fitness/nutrition questions
            - Water intake reminders and recommendations

            ## Context Usage
            You will receive the user's real-time data as context. USE IT to personalize every response:
            - Their profile (name, age, weight, height, gender, fitness goal, experience level)
            - Today's food log with macro totals
            - Today's workout log
            - Their daily goals (calories, protein, carbs, fats, water)
            - Current water intake

            ## Rules
            - ALWAYS reference their actual data when relevant (e.g., "You've had 1200 kcal so far, with 800 left for the day")
            - If they ask what to eat, factor in remaining macros
            - Be aware of Indian foods and cuisine (dal, roti, biryani, dosa, etc.)
            - Never give medical advice — suggest consulting a doctor for health concerns
            - If context data is missing, still give great general advice
            - Format responses in markdown for readability
            """;

    /**
     * Chat with the AI fitness coach.
     *
     * @param message  the user's message
     * @param history  previous messages in the conversation [{role, text}, ...]
     * @param context  user context (profile, todayStats)
     * @return AI response text
     */
    public String chat(String message, List<Map<String, String>> history, Map<String, Object> context) {
        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            throw new RuntimeException("Gemini API key is not configured. Set GEMINI_API_KEY environment variable.");
        }

        try {
            Map<String, Object> requestBody = buildChatRequest(message, history, context);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            String url = GEMINI_API_URL + "?key=" + geminiApiKey;
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.POST, entity, JsonNode.class);

            if (response.getBody() == null) {
                throw new RuntimeException("Empty response from Gemini API");
            }

            return extractContent(response.getBody());

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Chatbot error: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> buildChatRequest(String message, List<Map<String, String>> history, Map<String, Object> context) {
        // Build dynamic system prompt with user context
        String fullSystemPrompt = SYSTEM_PROMPT + "\n\n## Current User Context\n" + formatContext(context);

        Map<String, Object> systemInstruction = new HashMap<>();
        systemInstruction.put("parts", List.of(Map.of("text", fullSystemPrompt)));

        // Build conversation contents (history + current message)
        List<Map<String, Object>> contents = new ArrayList<>();

        // Add history
        if (history != null) {
            for (Map<String, String> msg : history) {
                Map<String, Object> content = new HashMap<>();
                content.put("role", msg.get("role")); // "user" or "model"
                content.put("parts", List.of(Map.of("text", msg.get("text"))));
                contents.add(content);
            }
        }

        // Add current user message
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("parts", List.of(Map.of("text", message)));
        contents.add(userMessage);

        // Generation config
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.7);
        generationConfig.put("maxOutputTokens", 1024);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("system_instruction", systemInstruction);
        requestBody.put("contents", contents);
        requestBody.put("generationConfig", generationConfig);

        return requestBody;
    }

    @SuppressWarnings("unchecked")
    private String formatContext(Map<String, Object> context) {
        if (context == null || context.isEmpty()) {
            return "No user context available — provide general fitness advice.";
        }

        StringBuilder sb = new StringBuilder();

        // Profile info
        Map<String, Object> profile = (Map<String, Object>) context.get("profile");
        if (profile != null) {
            sb.append("### Profile\n");
            sb.append("- Name: ").append(profile.getOrDefault("name", "Unknown")).append("\n");
            sb.append("- Age: ").append(profile.getOrDefault("age", "N/A")).append("\n");
            sb.append("- Weight: ").append(profile.getOrDefault("weight", "N/A")).append(" kg\n");
            sb.append("- Height: ").append(profile.getOrDefault("height", "N/A")).append(" cm\n");
            sb.append("- Gender: ").append(profile.getOrDefault("gender", "N/A")).append("\n");
            sb.append("- Fitness Goal: ").append(profile.getOrDefault("fitnessGoal", "N/A")).append("\n");
            sb.append("- Experience Level: ").append(profile.getOrDefault("experienceLevel", "N/A")).append("\n\n");
        }

        // Today's stats
        Map<String, Object> todayStats = (Map<String, Object>) context.get("todayStats");
        if (todayStats != null) {
            sb.append("### Today's Progress\n");

            Map<String, Object> goals = (Map<String, Object>) todayStats.get("goals");
            if (goals != null) {
                sb.append("**Daily Goals:** ");
                sb.append("Calories: ").append(goals.getOrDefault("calories", "N/A"));
                sb.append(", Protein: ").append(goals.getOrDefault("protein", "N/A")).append("g");
                sb.append(", Carbs: ").append(goals.getOrDefault("carbs", "N/A")).append("g");
                sb.append(", Fats: ").append(goals.getOrDefault("fats", "N/A")).append("g");
                sb.append(", Water: ").append(goals.getOrDefault("water", "N/A")).append(" glasses\n");
            }

            sb.append("**Consumed:** ");
            sb.append("Calories: ").append(todayStats.getOrDefault("totalCalories", 0));
            sb.append(", Protein: ").append(todayStats.getOrDefault("totalProtein", 0)).append("g");
            sb.append(", Carbs: ").append(todayStats.getOrDefault("totalCarbs", 0)).append("g");
            sb.append(", Fats: ").append(todayStats.getOrDefault("totalFats", 0)).append("g\n");

            sb.append("**Water intake:** ").append(todayStats.getOrDefault("waterIntake", 0)).append(" glasses\n");
            sb.append("**Calories burned (workout):** ").append(todayStats.getOrDefault("caloriesBurned", 0)).append(" kcal\n\n");

            // Food log
            List<Map<String, Object>> foodLog = (List<Map<String, Object>>) todayStats.get("foodLog");
            if (foodLog != null && !foodLog.isEmpty()) {
                sb.append("**Foods eaten today:** ");
                List<String> foods = new ArrayList<>();
                for (Map<String, Object> food : foodLog) {
                    foods.add(food.getOrDefault("name", "Unknown") + " (" + food.getOrDefault("calories", 0) + " kcal)");
                }
                sb.append(String.join(", ", foods)).append("\n\n");
            }

            // Workout log
            List<Map<String, Object>> workoutLog = (List<Map<String, Object>>) todayStats.get("workoutLog");
            if (workoutLog != null && !workoutLog.isEmpty()) {
                sb.append("**Workouts today:** ");
                List<String> workouts = new ArrayList<>();
                for (Map<String, Object> w : workoutLog) {
                    workouts.add(w.getOrDefault("exerciseName", "Unknown") + " (" + w.getOrDefault("caloriesBurned", 0) + " kcal burned)");
                }
                sb.append(String.join(", ", workouts)).append("\n");
            }
        }

        return sb.toString();
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
