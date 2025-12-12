package com.abhi.FitnessTracker.Controller;

import com.abhi.FitnessTracker.Model.Exercise;
import com.abhi.FitnessTracker.Repository.ExerciseRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST controller for exercise management.
 * Supports system exercises and user custom exercises.
 */
@RestController
@RequestMapping("/api/exercises")
@CrossOrigin(origins = "http://localhost:5173")
public class ExerciseController {
    
    private final ExerciseRepository exerciseRepository;
    
    public ExerciseController(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }
    
    /**
     * Get all exercises (system + user's custom)
     * GET /api/exercises?userId={userId}
     */
    @GetMapping
    public ResponseEntity<List<Exercise>> getAllExercises(@RequestParam(required = false) String userId) {
        List<Exercise> exercises = exerciseRepository.findAll();
        
        // Filter: show system exercises (createdByUserId = null) + user's own custom exercises
        if (userId != null) {
            exercises = exercises.stream()
                .filter(e -> e.getCreatedByUserId() == null || e.getCreatedByUserId().equals(userId))
                .collect(Collectors.toList());
        } else {
            // Only show system exercises if no userId provided
            exercises = exercises.stream()
                .filter(e -> e.getCreatedByUserId() == null)
                .collect(Collectors.toList());
        }
        
        return ResponseEntity.ok(exercises);
    }
    
    /**
     * Get exercises by category
     * GET /api/exercises/category/{category}?userId={userId}
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Exercise>> getByCategory(
            @PathVariable String category,
            @RequestParam(required = false) String userId) {
        List<Exercise> exercises = exerciseRepository.findByCategory(category);
        
        if (userId != null) {
            exercises = exercises.stream()
                .filter(e -> e.getCreatedByUserId() == null || e.getCreatedByUserId().equals(userId))
                .collect(Collectors.toList());
        } else {
            exercises = exercises.stream()
                .filter(e -> e.getCreatedByUserId() == null)
                .collect(Collectors.toList());
        }
        
        return ResponseEntity.ok(exercises);
    }
    
    /**
     * Search exercises by name
     * GET /api/exercises/search?q={query}&userId={userId}
     */
    @GetMapping("/search")
    public ResponseEntity<List<Exercise>> searchExercises(
            @RequestParam("q") String query,
            @RequestParam(required = false) String userId) {
        List<Exercise> allExercises = exerciseRepository.findAll();
        String lowerQuery = query.toLowerCase();
        
        List<Exercise> results = allExercises.stream()
            .filter(e -> e.getName().toLowerCase().contains(lowerQuery))
            .filter(e -> e.getCreatedByUserId() == null || 
                        (userId != null && e.getCreatedByUserId().equals(userId)))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(results);
    }
    
    /**
     * Create a new custom exercise (user or admin)
     * POST /api/exercises
     */
    @PostMapping
    public ResponseEntity<?> createExercise(@RequestBody Exercise exercise) {
        try {
            Exercise saved = exerciseRepository.save(exercise);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Update an exercise
     * PUT /api/exercises/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateExercise(@PathVariable String id, @RequestBody Exercise exercise) {
        try {
            if (!exerciseRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            exercise.setId(id);
            Exercise updated = exerciseRepository.save(exercise);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Delete an exercise
     * DELETE /api/exercises/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExercise(@PathVariable String id) {
        try {
            if (!exerciseRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            exerciseRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Exercise deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get all categories
     * GET /api/exercises/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(List.of("home", "gym", "yoga", "hiit", "sports"));
    }
}
