package com.abhi.FitnessTracker.Controller;

import com.abhi.FitnessTracker.Model.WorkoutEntry;
import com.abhi.FitnessTracker.Model.WorkoutLog;
import com.abhi.FitnessTracker.Service.WorkoutLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for workout log endpoints.
 */
@RestController
@RequestMapping("/api/profiles/{profileId}/workout-log")
@CrossOrigin(origins = "http://localhost:5173")
public class WorkoutLogController {
    
    private final WorkoutLogService workoutLogService;
    
    public WorkoutLogController(WorkoutLogService workoutLogService) {
        this.workoutLogService = workoutLogService;
    }
    
    /**
     * Get today's workout log
     * GET /api/profiles/{profileId}/workout-log
     */
    @GetMapping
    public ResponseEntity<WorkoutLog> getTodayLog(@PathVariable String profileId) {
        return ResponseEntity.ok(workoutLogService.getTodayLog(profileId));
    }
    
    /**
     * Get workout log by date
     * GET /api/profiles/{profileId}/workout-log/{date}
     */
    @GetMapping("/{date}")
    public ResponseEntity<WorkoutLog> getLogByDate(
            @PathVariable String profileId,
            @PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        return ResponseEntity.ok(workoutLogService.getLogByDate(profileId, localDate));
    }
    
    /**
     * Add a workout entry to today's log
     * POST /api/profiles/{profileId}/workout-log
     */
    @PostMapping
    public ResponseEntity<WorkoutLog> addWorkout(
            @PathVariable String profileId,
            @RequestBody WorkoutEntry workout) {
        return ResponseEntity.ok(workoutLogService.addWorkout(profileId, workout));
    }
    
    /**
     * Update an existing workout entry
     * PUT /api/profiles/{profileId}/workout-log/{workoutId}
     */
    @PutMapping("/{workoutId}")
    public ResponseEntity<?> updateWorkout(
            @PathVariable String profileId,
            @PathVariable String workoutId,
            @RequestBody WorkoutEntry workout) {
        try {
            return ResponseEntity.ok(workoutLogService.updateWorkout(profileId, workoutId, workout));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Remove a workout entry from today's log
     * DELETE /api/profiles/{profileId}/workout-log/{workoutId}
     */
    @DeleteMapping("/{workoutId}")
    public ResponseEntity<WorkoutLog> removeWorkout(
            @PathVariable String profileId,
            @PathVariable String workoutId) {
        return ResponseEntity.ok(workoutLogService.removeWorkout(profileId, workoutId));
    }
    
    /**
     * Get all workout logs for a profile
     * GET /api/profiles/{profileId}/workout-log/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<WorkoutLog>> getAllLogs(@PathVariable String profileId) {
        return ResponseEntity.ok(workoutLogService.getAllLogs(profileId));
    }
    
    /**
     * Get workout logs between dates
     * GET /api/profiles/{profileId}/workout-log/range?start={date}&end={date}
     */
    @GetMapping("/range")
    public ResponseEntity<List<WorkoutLog>> getLogsBetweenDates(
            @PathVariable String profileId,
            @RequestParam String start,
            @RequestParam String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        return ResponseEntity.ok(workoutLogService.getLogsBetweenDates(profileId, startDate, endDate));
    }
    
    /**
     * Get total calories burned today
     * GET /api/profiles/{profileId}/workout-log/calories-burned
     */
    @GetMapping("/calories-burned")
    public ResponseEntity<Map<String, Double>> getCaloriesBurned(@PathVariable String profileId) {
        WorkoutLog log = workoutLogService.getTodayLog(profileId);
        return ResponseEntity.ok(Map.of("caloriesBurned", log.getTotalCaloriesBurned()));
    }
}
