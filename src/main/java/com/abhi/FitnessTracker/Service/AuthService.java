package com.abhi.FitnessTracker.Service;

import com.abhi.FitnessTracker.Model.Goals;
import com.abhi.FitnessTracker.Model.Profile;
import com.abhi.FitnessTracker.Model.User;
import com.abhi.FitnessTracker.Repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for user authentication and profile management.
 */
@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final com.abhi.FitnessTracker.Repository.PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;
    
    public AuthService(UserRepository userRepository, 
                       com.abhi.FitnessTracker.Repository.PasswordResetTokenRepository tokenRepository,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    
    /**
     * Register a new user with email and password
     * Note: Profile is NOT auto-created - user goes through profile setup wizard
     */
    public User register(String email, String password, String name) {
        // Check if user already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("User with this email already exists");
        }
        
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        
        // No default profile - user will create via profile setup wizard
        
        return userRepository.save(user);
    }
    
    /**
     * Login with email and password
     */
    public User login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }
        
        User user = userOpt.get();
        
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }
        
        return user;
    }
    
    /**
     * Get user by ID
     */
    public Optional<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }
    
    /**
     * Get user by email
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Initiate password reset
     */
    public void forgotPassword(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            // Security: Don't reveal if user exists
            return; 
        }
        
        // Clean up old tokens
        tokenRepository.deleteByEmail(email);
        
        // Create new token
        String token = UUID.randomUUID().toString();
        com.abhi.FitnessTracker.Model.PasswordResetToken resetToken = new com.abhi.FitnessTracker.Model.PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setEmail(email);
        resetToken.setExpiryDate(java.time.LocalDateTime.now().plusHours(1));
        tokenRepository.save(resetToken);
        
        // Send email
        emailService.sendPasswordResetEmail(email, token);
    }

    /**
     * Complete password reset
     */
    public void resetPassword(String token, String newPassword) {
        Optional<com.abhi.FitnessTracker.Model.PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        
        if (tokenOpt.isEmpty()) {
            throw new RuntimeException("Invalid or expired password reset token");
        }
        
        com.abhi.FitnessTracker.Model.PasswordResetToken resetToken = tokenOpt.get();
        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("Token has expired");
        }
        
        User user = userRepository.findByEmail(resetToken.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        tokenRepository.delete(resetToken);
    }
}
