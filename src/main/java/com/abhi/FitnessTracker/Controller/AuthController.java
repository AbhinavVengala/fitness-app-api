package com.abhi.FitnessTracker.Controller;

import com.abhi.FitnessTracker.Model.User;
import com.abhi.FitnessTracker.Security.JwtUtil;
import com.abhi.FitnessTracker.Service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for authentication endpoints.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    
    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }
    
    /**
     * Register a new user
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = authService.register(request.email(), request.password(), request.name());
            String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.isAdmin());
            return ResponseEntity.ok(new AuthResponse(user, token));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Login with email and password
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            User user = authService.login(request.email(), request.password());
            String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.isAdmin());
            return ResponseEntity.ok(new AuthResponse(user, token));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Validate current token and get user info
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("error", "No token provided"));
            }
            
            String token = authHeader.substring(7);
            String userId = jwtUtil.getUserId(token);
            
            if (userId == null || jwtUtil.isTokenExpired(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired token"));
            }
            
            User user = authService.getUserById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            }
            
            return ResponseEntity.ok(new AuthResponse(user, token));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid token"));
        }
    }

    /**
     * Request password reset
     * POST /api/auth/forgot-password
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
        }
        authService.forgotPassword(email);
        return ResponseEntity.ok(Map.of("message", "If an account exists, a reset link has been sent."));
    }

    /**
     * Reset password with token
     * POST /api/auth/reset-password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");
        
        if (token == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Token and new password are required"));
        }
        
        try {
            authService.resetPassword(token, newPassword);
            return ResponseEntity.ok(Map.of("message", "Password successfully reset"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // DTOs with validation
    public record RegisterRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,
        
        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,
        
        @NotBlank(message = "Name is required")
        String name
    ) {}
    
    public record LoginRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,
        
        @NotBlank(message = "Password is required")
        String password
    ) {}
    
    public record AuthResponse(String id, String email, boolean isAdmin, String token, java.util.List<ProfileDTO> profiles) {
        public AuthResponse(User user, String token) {
            this(
                user.getId(),
                user.getEmail(),
                user.isAdmin(),
                token,
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

