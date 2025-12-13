package com.abhi.FitnessTracker.Security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for JWT token generation and validation.
 */
@Component
public class JwtUtil {
    
    @Value("${jwt.secret:your-256-bit-secret-key-for-jwt-signing-needs-to-be-long}")
    private String secretKey;
    
    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private long jwtExpiration;
    
    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * Generate JWT token for a user
     */
    public String generateToken(String userId, String email, boolean isAdmin) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("isAdmin", isAdmin);
        
        return Jwts.builder()
                .claims(claims)
                .subject(userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }
    
    /**
     * Validate token and return claims
     */
    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * Extract user ID from token
     */
    public String getUserId(String token) {
        Claims claims = validateToken(token);
        return claims != null ? claims.getSubject() : null;
    }
    
    /**
     * Extract email from token
     */
    public String getEmail(String token) {
        Claims claims = validateToken(token);
        return claims != null ? claims.get("email", String.class) : null;
    }
    
    /**
     * Check if user is admin from token
     */
    public Boolean isAdmin(String token) {
        Claims claims = validateToken(token);
        return claims != null ? claims.get("isAdmin", Boolean.class) : false;
    }
    
    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        Claims claims = validateToken(token);
        if (claims == null) return true;
        return claims.getExpiration().before(new Date());
    }
}
