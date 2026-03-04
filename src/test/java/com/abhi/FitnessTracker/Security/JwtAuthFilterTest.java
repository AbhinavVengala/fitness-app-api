package com.abhi.FitnessTracker.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthFilter jwtAuthFilter;

    @BeforeEach
    void setUp() {
        jwtAuthFilter = new JwtAuthFilter(jwtUtil);
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_noAuthHeader_continuesChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_invalidBearerPrefix_continuesChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic some-token");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_validToken_setsAuthentication() throws Exception {
        String token = "valid-jwt-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        // Mock Claims
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("user-1");
        when(claims.get("email", String.class)).thenReturn("test@test.com");
        when(claims.get("isAdmin", Boolean.class)).thenReturn(false);

        when(jwtUtil.validateToken(token)).thenReturn(claims);
        when(jwtUtil.isTokenExpired(token)).thenReturn(false);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        assertEquals("user-1", principal.getUserId());
        assertEquals("test@test.com", principal.getEmail());
        assertFalse(principal.isAdmin());
    }

    @Test
    void doFilterInternal_validAdminToken_setsAdminAuthority() throws Exception {
        String token = "admin-jwt-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("admin-1");
        when(claims.get("email", String.class)).thenReturn("admin@test.com");
        when(claims.get("isAdmin", Boolean.class)).thenReturn(true);

        when(jwtUtil.validateToken(token)).thenReturn(claims);
        when(jwtUtil.isTokenExpired(token)).thenReturn(false);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void doFilterInternal_invalidToken_noAuthentication() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");
        when(jwtUtil.validateToken("invalid-token")).thenReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_expiredToken_noAuthentication() throws Exception {
        String token = "expired-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        Claims claims = mock(Claims.class);
        when(jwtUtil.validateToken(token)).thenReturn(claims);
        when(jwtUtil.isTokenExpired(token)).thenReturn(true);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
