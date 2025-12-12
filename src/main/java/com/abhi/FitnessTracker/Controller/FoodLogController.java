package com.abhi.FitnessTracker.Controller;

import com.abhi.FitnessTracker.Model.FoodItem;
import com.abhi.FitnessTracker.Model.FoodLog;
import com.abhi.FitnessTracker.Service.FoodLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for food log endpoints.
 */
@RestController
@RequestMapping("/api/profiles/{profileId}/food-log")
@CrossOrigin(origins = "http://localhost:5173")
public class FoodLogController {
    
    private final FoodLogService foodLogService;
    
    public FoodLogController(FoodLogService foodLogService) {
        this.foodLogService = foodLogService;
    }
    
    /**
     * Get today's food log
     * GET /api/profiles/{profileId}/food-log
     */
    @GetMapping
    public ResponseEntity<FoodLog> getTodayLog(@PathVariable String profileId) {
        return ResponseEntity.ok(foodLogService.getTodayLog(profileId));
    }
    
    /**
     * Get food log by date
     * GET /api/profiles/{profileId}/food-log/{date}
     */
    @GetMapping("/{date}")
    public ResponseEntity<FoodLog> getLogByDate(
            @PathVariable String profileId,
            @PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        return ResponseEntity.ok(foodLogService.getLogByDate(profileId, localDate));
    }
    
    /**
     * Add a food item to today's log
     * POST /api/profiles/{profileId}/food-log
     */
    @PostMapping
    public ResponseEntity<FoodLog> addFoodItem(
            @PathVariable String profileId,
            @RequestBody FoodItem item) {
        return ResponseEntity.ok(foodLogService.addFoodItem(profileId, item));
    }
    
    /**
     * Remove a food item from today's log
     * DELETE /api/profiles/{profileId}/food-log/{itemId}
     */
    @DeleteMapping("/{itemId}")
    public ResponseEntity<FoodLog> removeFoodItem(
            @PathVariable String profileId,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(foodLogService.removeFoodItem(profileId, itemId));
    }
    
    /**
     * Get all food logs for a profile
     * GET /api/profiles/{profileId}/food-log/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<FoodLog>> getAllLogs(@PathVariable String profileId) {
        return ResponseEntity.ok(foodLogService.getAllLogs(profileId));
    }
    
    /**
     * Get food logs between dates
     * GET /api/profiles/{profileId}/food-log/range?start={date}&end={date}
     */
    @GetMapping("/range")
    public ResponseEntity<List<FoodLog>> getLogsBetweenDates(
            @PathVariable String profileId,
            @RequestParam String start,
            @RequestParam String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        return ResponseEntity.ok(foodLogService.getLogsBetweenDates(profileId, startDate, endDate));
    }
    
    /**
     * Get daily totals summary
     * GET /api/profiles/{profileId}/food-log/totals
     */
    @GetMapping("/totals")
    public ResponseEntity<Map<String, Double>> getDailyTotals(@PathVariable String profileId) {
        FoodLog log = foodLogService.getTodayLog(profileId);
        return ResponseEntity.ok(Map.of(
            "calories", log.getTotalCalories(),
            "protein", log.getTotalProtein(),
            "carbs", log.getTotalCarbs(),
            "fats", log.getTotalFats()
        ));
    }
}
