package com.abhi.FitnessTracker.Controller;

import com.abhi.FitnessTracker.Model.Goals;
import com.abhi.FitnessTracker.Service.AiGoalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for AI-suggested goals.
 */
@RestController
@RequestMapping("/api/goals")
public class AiGoalController {

    private final AiGoalService aiGoalService;

    public AiGoalController(AiGoalService aiGoalService) {
        this.aiGoalService = aiGoalService;
    }

    /**
     * Get AI suggested macro/water targets based on profile attributes.
     * POST /api/goals/suggest
     * Expected Body: { "age": 25, "weight": 70, "height": 170, "gender": "male", "fitnessGoal": "weightLoss", "experienceLevel": "beginner" }
     */
    @PostMapping("/suggest")
    public ResponseEntity<?> suggestGoals(@RequestBody Map<String, Object> profileContext) {
        try {
            Goals suggestedGoals = aiGoalService.suggestGoals(profileContext);
            return ResponseEntity.ok(suggestedGoals);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Failed to generate goal suggestions: " + e.getMessage()
            ));
        }
    }
}
