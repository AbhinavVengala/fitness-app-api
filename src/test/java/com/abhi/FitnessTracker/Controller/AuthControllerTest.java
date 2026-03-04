package com.abhi.FitnessTracker.Controller;

import com.abhi.FitnessTracker.Model.Profile;
import com.abhi.FitnessTracker.Model.User;
import com.abhi.FitnessTracker.Security.JwtUtil;
import com.abhi.FitnessTracker.Service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("user-1");
        testUser.setEmail("test@example.com");
        testUser.setAdmin(false);
        testUser.setProfiles(new ArrayList<>());
    }

    // ========== Register ==========

    @Test
    void register_success_returnsAuthResponse() {
        when(authService.register("new@test.com", "pass123", "John")).thenReturn(testUser);
        when(jwtUtil.generateToken("user-1", "test@example.com", false)).thenReturn("jwt-token");

        var request = new AuthController.RegisterRequest("new@test.com", "pass123", "John");
        ResponseEntity<?> response = authController.register(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void register_duplicateEmail_returnsBadRequest() {
        when(authService.register(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("User with this email already exists"));

        var request = new AuthController.RegisterRequest("test@test.com", "pass123", "Test");
        ResponseEntity<?> response = authController.register(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // ========== Login ==========

    @Test
    void login_success_returnsAuthResponse() {
        when(authService.login("test@example.com", "correct")).thenReturn(testUser);
        when(jwtUtil.generateToken("user-1", "test@example.com", false)).thenReturn("jwt-token");

        var request = new AuthController.LoginRequest("test@example.com", "correct");
        ResponseEntity<?> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void login_failure_returnsBadRequest() {
        when(authService.login(anyString(), anyString()))
                .thenThrow(new RuntimeException("Invalid email or password"));

        var request = new AuthController.LoginRequest("test@test.com", "wrong");
        ResponseEntity<?> response = authController.login(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // ========== Get Current User (GET /me) ==========

    @Test
    void getCurrentUser_validToken_returnsUser() {
        when(jwtUtil.getUserId("valid-token")).thenReturn("user-1");
        when(jwtUtil.isTokenExpired("valid-token")).thenReturn(false);
        when(authService.getUserById("user-1")).thenReturn(Optional.of(testUser));

        ResponseEntity<?> response = authController.getCurrentUser("Bearer valid-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getCurrentUser_noToken_returns401() {
        ResponseEntity<?> response = authController.getCurrentUser("InvalidHeader");

        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void getCurrentUser_expiredToken_returns401() {
        when(jwtUtil.getUserId("expired")).thenReturn("user-1");
        when(jwtUtil.isTokenExpired("expired")).thenReturn(true);

        ResponseEntity<?> response = authController.getCurrentUser("Bearer expired");

        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void getCurrentUser_nullUserId_returns401() {
        when(jwtUtil.getUserId("bad")).thenReturn(null);

        ResponseEntity<?> response = authController.getCurrentUser("Bearer bad");

        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void getCurrentUser_userNotFound_returns401() {
        when(jwtUtil.getUserId("valid")).thenReturn("user-1");
        when(jwtUtil.isTokenExpired("valid")).thenReturn(false);
        when(authService.getUserById("user-1")).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.getCurrentUser("Bearer valid");

        assertEquals(401, response.getStatusCode().value());
    }

    // ========== Forgot Password ==========

    @Test
    void forgotPassword_validEmail_returnsOk() {
        ResponseEntity<?> response = authController.forgotPassword(Map.of("email", "test@example.com"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authService).forgotPassword("test@example.com");
    }

    @Test
    void forgotPassword_missingEmail_returnsBadRequest() {
        ResponseEntity<?> response = authController.forgotPassword(Map.of("email", ""));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // ========== Reset Password ==========

    @Test
    void resetPassword_success_returnsOk() {
        ResponseEntity<?> response = authController.resetPassword(
                Map.of("token", "valid-token", "newPassword", "newPass"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authService).resetPassword("valid-token", "newPass");
    }

    @Test
    void resetPassword_missingFields_returnsBadRequest() {
        ResponseEntity<?> response = authController.resetPassword(Map.of("token", "tok"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void resetPassword_failure_returnsBadRequest() {
        doThrow(new RuntimeException("Invalid token"))
                .when(authService).resetPassword(anyString(), anyString());

        ResponseEntity<?> response = authController.resetPassword(
                Map.of("token", "bad-token", "newPassword", "newPass"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // ========== Delete Account ==========

    @Test
    void deleteAccount_success_returnsOk() {
        when(jwtUtil.getUserId("valid")).thenReturn("user-1");
        when(jwtUtil.isTokenExpired("valid")).thenReturn(false);

        ResponseEntity<?> response = authController.deleteAccount("Bearer valid");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authService).deleteAccount("user-1");
    }

    @Test
    void deleteAccount_noToken_returns401() {
        ResponseEntity<?> response = authController.deleteAccount("InvalidHeader");

        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void deleteAccount_failure_returnsBadRequest() {
        when(jwtUtil.getUserId("valid")).thenReturn("user-1");
        when(jwtUtil.isTokenExpired("valid")).thenReturn(false);
        doThrow(new RuntimeException("User not found"))
                .when(authService).deleteAccount("user-1");

        ResponseEntity<?> response = authController.deleteAccount("Bearer valid");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
