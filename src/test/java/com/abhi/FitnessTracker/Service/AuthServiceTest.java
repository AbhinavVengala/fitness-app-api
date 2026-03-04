package com.abhi.FitnessTracker.Service;

import com.abhi.FitnessTracker.Model.PasswordResetToken;
import com.abhi.FitnessTracker.Model.User;
import com.abhi.FitnessTracker.Repository.PasswordResetTokenRepository;
import com.abhi.FitnessTracker.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("user-1");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("$2a$10$hashedpassword"); // BCrypt hash
    }

    // ========== Register ==========

    @Test
    void register_success_savesUser() {
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId("new-id");
            return u;
        });

        User result = authService.register("new@example.com", "securePass123", "John");

        assertNotNull(result);
        assertEquals("new@example.com", result.getEmail());
        assertNotNull(result.getPasswordHash());
        // Password should be hashed, not plaintext
        assertNotEquals("securePass123", result.getPasswordHash());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_duplicateEmail_throwsException() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.register("test@example.com", "pass", "Test"));

        assertEquals("User with this email already exists", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    // ========== Login ==========

    @Test
    void login_success_returnsUser() {
        // Create a user with a known hashed password
        User user = new User();
        user.setId("user-1");
        user.setEmail("test@example.com");
        user.setPasswordHash(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("correct"));

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        User result = authService.login("test@example.com", "correct");

        assertNotNull(result);
        assertEquals("user-1", result.getId());
    }

    @Test
    void login_wrongPassword_throwsException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("correct"));

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class,
                () -> authService.login("test@example.com", "wrong"));
    }

    @Test
    void login_unknownEmail_throwsException() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.login("unknown@example.com", "pass"));

        assertEquals("Invalid email or password", ex.getMessage());
    }

    // ========== getUserById ==========

    @Test
    void getUserById_found() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(testUser));

        Optional<User> result = authService.getUserById("user-1");

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    void getUserById_notFound() {
        when(userRepository.findById("missing")).thenReturn(Optional.empty());

        Optional<User> result = authService.getUserById("missing");

        assertTrue(result.isEmpty());
    }

    // ========== getUserByEmail ==========

    @Test
    void getUserByEmail_found() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = authService.getUserByEmail("test@example.com");

        assertTrue(result.isPresent());
    }

    // ========== getAllUsers ==========

    @Test
    void getAllUsers_returnsList() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));

        List<User> result = authService.getAllUsers();

        assertEquals(1, result.size());
    }

    // ========== forgotPassword ==========

    @Test
    void forgotPassword_existingUser_createsTokenAndSendsEmail() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        authService.forgotPassword("test@example.com");

        verify(tokenRepository).deleteByEmail("test@example.com");
        verify(tokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).sendPasswordResetEmail(eq("test@example.com"), anyString());
    }

    @Test
    void forgotPassword_unknownEmail_doesNothing() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        authService.forgotPassword("unknown@example.com");

        verify(tokenRepository, never()).save(any());
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString());
    }

    // ========== resetPassword ==========

    @Test
    void resetPassword_validToken_updatesPassword() {
        PasswordResetToken token = new PasswordResetToken();
        token.setToken("valid-token");
        token.setEmail("test@example.com");
        token.setExpiryDate(LocalDateTime.now().plusHours(1));

        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(token));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        authService.resetPassword("valid-token", "newPassword123");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertNotEquals("$2a$10$hashedpassword", userCaptor.getValue().getPasswordHash());
        verify(tokenRepository).delete(token);
    }

    @Test
    void resetPassword_invalidToken_throwsException() {
        when(tokenRepository.findByToken("bad-token")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> authService.resetPassword("bad-token", "newPass"));
    }

    @Test
    void resetPassword_expiredToken_throwsException() {
        PasswordResetToken token = new PasswordResetToken();
        token.setToken("expired-token");
        token.setEmail("test@example.com");
        token.setExpiryDate(LocalDateTime.now().minusHours(2)); // Already expired

        when(tokenRepository.findByToken("expired-token")).thenReturn(Optional.of(token));

        assertThrows(RuntimeException.class,
                () -> authService.resetPassword("expired-token", "newPass"));

        verify(tokenRepository).delete(token);
    }

    // ========== deleteAccount ==========

    @Test
    void deleteAccount_success_deletesTokensAndUser() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(testUser));

        authService.deleteAccount("user-1");

        verify(tokenRepository).deleteByEmail("test@example.com");
        verify(userRepository).deleteById("user-1");
    }

    @Test
    void deleteAccount_userNotFound_throwsException() {
        when(userRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> authService.deleteAccount("missing"));
    }
}
