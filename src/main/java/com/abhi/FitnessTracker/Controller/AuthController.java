package com.abhi.FitnessTracker.Controller;

import com.abhi.FitnessTracker.Model.User;
import com.abhi.FitnessTracker.Service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for authentication endpoints.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {
    
    private final AuthService authService;
    
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    /**
     * Register a new user
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = authService.register(request.email(), request.password(), request.name());
            return ResponseEntity.ok(new AuthResponse(user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Login with email and password
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = authService.login(request.email(), request.password());
            return ResponseEntity.ok(new AuthResponse(user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // DTOs
    public record RegisterRequest(String email, String password, String name) {}
    public record LoginRequest(String email, String password) {}
    
    public record AuthResponse(String id, String email, boolean isAdmin, java.util.List<ProfileDTO> profiles) {
        public AuthResponse(User user) {
            this(
                user.getId(),
                user.getEmail(),
                user.isAdmin(),
                user.getProfiles().stream()
                    .map(p -> new ProfileDTO(
                        p.getId(), p.getName(), p.getAge(), p.getWeight(), p.getHeight(),
                        p.getGender(), p.getFitnessGoal(), p.getExperienceLevel(),
                        p.getGoals(), p.getWaterIntake()
                    ))
                    .toList()
            );
        }
    }
    
    public record ProfileDTO(
        String id, String name, int age, double weight, double height,
        String gender, String fitnessGoal, String experienceLevel,
        com.abhi.FitnessTracker.Model.Goals goals, int waterIntake
    ) {}
}
