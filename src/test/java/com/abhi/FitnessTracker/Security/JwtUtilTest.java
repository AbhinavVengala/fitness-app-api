package com.abhi.FitnessTracker.Security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Inject values that would normally come from application.yaml
        ReflectionTestUtils.setField(jwtUtil, "secretKey",
                "test-secret-key-that-is-at-least-256-bits-long-for-hmac-sha256");
        ReflectionTestUtils.setField(jwtUtil, "jwtExpiration", 86400000L); // 24 hours
    }

    // ========== generateToken ==========

    @Test
    void generateToken_returnsNonNullToken() {
        String token = jwtUtil.generateToken("user-1", "test@test.com", false);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void generateToken_differentUsersProduceDifferentTokens() {
        String token1 = jwtUtil.generateToken("user-1", "a@test.com", false);
        String token2 = jwtUtil.generateToken("user-2", "b@test.com", true);

        assertNotEquals(token1, token2);
    }

    // ========== validateToken ==========

    @Test
    void validateToken_validToken_returnsClaims() {
        String token = jwtUtil.generateToken("user-1", "test@test.com", false);

        Claims claims = jwtUtil.validateToken(token);

        assertNotNull(claims);
        assertEquals("user-1", claims.getSubject());
    }

    @Test
    void validateToken_invalidToken_returnsNull() {
        Claims claims = jwtUtil.validateToken("invalid.jwt.token");

        assertNull(claims);
    }

    @Test
    void validateToken_tamperedToken_returnsNull() {
        String token = jwtUtil.generateToken("user-1", "test@test.com", false);
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";

        Claims claims = jwtUtil.validateToken(tampered);

        assertNull(claims);
    }

    // ========== getUserId ==========

    @Test
    void getUserId_returnsCorrectId() {
        String token = jwtUtil.generateToken("user-42", "test@test.com", false);

        String userId = jwtUtil.getUserId(token);

        assertEquals("user-42", userId);
    }

    @Test
    void getUserId_invalidToken_returnsNull() {
        String userId = jwtUtil.getUserId("garbage");

        assertNull(userId);
    }

    // ========== getEmail ==========

    @Test
    void getEmail_returnsCorrectEmail() {
        String token = jwtUtil.generateToken("user-1", "hello@world.com", false);

        String email = jwtUtil.getEmail(token);

        assertEquals("hello@world.com", email);
    }

    @Test
    void getEmail_invalidToken_returnsNull() {
        String email = jwtUtil.getEmail("garbage");

        assertNull(email);
    }

    // ========== isAdmin ==========

    @Test
    void isAdmin_admin_returnsTrue() {
        String token = jwtUtil.generateToken("user-1", "admin@test.com", true);

        assertTrue(jwtUtil.isAdmin(token));
    }

    @Test
    void isAdmin_regularUser_returnsFalse() {
        String token = jwtUtil.generateToken("user-1", "user@test.com", false);

        assertFalse(jwtUtil.isAdmin(token));
    }

    // ========== isTokenExpired ==========

    @Test
    void isTokenExpired_freshToken_returnsFalse() {
        String token = jwtUtil.generateToken("user-1", "test@test.com", false);

        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    void isTokenExpired_expiredToken_returnsTrue() {
        JwtUtil expiredJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(expiredJwtUtil, "secretKey",
                "test-secret-key-that-is-at-least-256-bits-long-for-hmac-sha256");
        ReflectionTestUtils.setField(expiredJwtUtil, "jwtExpiration", 0L);

        String token = expiredJwtUtil.generateToken("user-1", "test@test.com", false);

        assertTrue(expiredJwtUtil.isTokenExpired(token));
    }

    @Test
    void isTokenExpired_invalidToken_returnsTrue() {
        assertTrue(jwtUtil.isTokenExpired("garbage"));
    }
}
