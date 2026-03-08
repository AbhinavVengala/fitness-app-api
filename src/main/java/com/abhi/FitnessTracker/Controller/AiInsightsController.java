package com.abhi.FitnessTracker.Controller;

import com.abhi.FitnessTracker.Service.AiInsightsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * REST Controller for AI-generated insights.
 */
@RestController
@RequestMapping("/api/profiles/{profileId}/insights")
public class AiInsightsController {

    private final AiInsightsService aiInsightsService;

    public AiInsightsController(AiInsightsService aiInsightsService) {
        this.aiInsightsService = aiInsightsService;
    }

    /**
     * Get a weekly AI insight summary.
     * GET /api/profiles/{profileId}/insights/weekly?start={date}&end={date}
     */
    @GetMapping("/weekly")
    public ResponseEntity<Map<String, String>> getWeeklyInsight(
            @PathVariable String profileId,
            @RequestParam String start,
            @RequestParam String end) {
        
        try {
            LocalDate startDate = LocalDate.parse(start);
            LocalDate endDate = LocalDate.parse(end);
            
            String insightText = aiInsightsService.generateWeeklyInsight(profileId, startDate, endDate);
            
            return ResponseEntity.ok(Map.of("insight", insightText));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Failed to generate insight: " + e.getMessage()
            ));
        }
    }
}
