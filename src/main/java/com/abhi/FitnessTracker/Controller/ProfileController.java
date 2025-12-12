package com.abhi.FitnessTracker.Controller;

import com.abhi.FitnessTracker.Model.Goals;
import com.abhi.FitnessTracker.Model.Profile;
import com.abhi.FitnessTracker.Service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for profile management endpoints.
 */
@RestController
@RequestMapping("/api/users/{userId}/profiles")
@CrossOrigin(origins = "http://localhost:5173")
public class ProfileController {
    
    private final ProfileService profileService;
    
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }
    
    /**
     * Get all profiles for a user
     * GET /api/users/{userId}/profiles
     */
    @GetMapping
    public ResponseEntity<List<Profile>> getProfiles(@PathVariable String userId) {
        try {
            return ResponseEntity.ok(profileService.getProfiles(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get a specific profile
     * GET /api/users/{userId}/profiles/{profileId}
     */
    @GetMapping("/{profileId}")
    public ResponseEntity<?> getProfile(@PathVariable String userId, @PathVariable String profileId) {
        try {
            return ResponseEntity.ok(profileService.getProfile(userId, profileId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Create a new profile
     * POST /api/users/{userId}/profiles
     */
    @PostMapping
    public ResponseEntity<?> createProfile(@PathVariable String userId, @RequestBody Profile profile) {
        try {
            return ResponseEntity.ok(profileService.addProfile(userId, profile));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Update a profile
     * PUT /api/users/{userId}/profiles/{profileId}
     */
    @PutMapping("/{profileId}")
    public ResponseEntity<?> updateProfile(
            @PathVariable String userId,
            @PathVariable String profileId,
            @RequestBody Profile profile) {
        try {
            return ResponseEntity.ok(profileService.updateProfile(userId, profileId, profile));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Update goals for a profile
     * PUT /api/users/{userId}/profiles/{profileId}/goals
     */
    @PutMapping("/{profileId}/goals")
    public ResponseEntity<?> updateGoals(
            @PathVariable String userId,
            @PathVariable String profileId,
            @RequestBody Goals goals) {
        try {
            return ResponseEntity.ok(profileService.updateGoals(userId, profileId, goals));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Update water intake for a profile
     * PUT /api/users/{userId}/profiles/{profileId}/water
     */
    @PutMapping("/{profileId}/water")
    public ResponseEntity<?> updateWaterIntake(
            @PathVariable String userId,
            @PathVariable String profileId,
            @RequestBody WaterRequest request) {
        try {
            return ResponseEntity.ok(profileService.updateWaterIntake(userId, profileId, request.waterIntake()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // DTOs
    public record WaterRequest(int waterIntake) {}
}
