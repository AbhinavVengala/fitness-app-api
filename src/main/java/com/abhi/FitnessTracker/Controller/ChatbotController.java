package com.abhi.FitnessTracker.Controller;

import com.abhi.FitnessTracker.Service.ChatbotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for the AI Fitness Chatbot.
 * Handles chat messages and returns AI-generated responses personalized with user context.
 */
@RestController
@RequestMapping("/api/chat")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    /**
     * Send a message to the AI fitness chatbot.
     * POST /api/chat
     * Body: {
     *   "message": "What should I eat for dinner?",
     *   "history": [{"role": "user", "text": "..."}, {"role": "model", "text": "..."}],
     *   "context": { "profile": {...}, "todayStats": {...} }
     * }
     */
    @SuppressWarnings("unchecked")
    @PostMapping
    public ResponseEntity<?> chat(@RequestBody Map<String, Object> request) {
        String message = (String) request.get("message");

        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Message is required"));
        }

        try {
            List<Map<String, String>> history = (List<Map<String, String>>) request.get("history");
            Map<String, Object> context = (Map<String, Object>) request.get("context");

            String reply = chatbotService.chat(message.trim(), history, context);
            return ResponseEntity.ok(Map.of("reply", reply));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Chat failed: " + e.getMessage()
            ));
        }
    }
}
