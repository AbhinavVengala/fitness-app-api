package com.abhi.FitnessTracker.Security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * JWT Authentication Filter - intercepts requests and validates JWT tokens.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    
    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String token = authHeader.substring(7);
        Claims claims = jwtUtil.validateToken(token);
        
        if (claims != null && !jwtUtil.isTokenExpired(token)) {
            String userId = claims.getSubject();
            String email = claims.get("email", String.class);
            Boolean isAdmin = claims.get("isAdmin", Boolean.class);
            
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            if (Boolean.TRUE.equals(isAdmin)) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
            
            // Create authentication token with user details
            UserPrincipal principal = new UserPrincipal(userId, email, isAdmin != null && isAdmin);
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(principal, null, authorities);
            
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        
        filterChain.doFilter(request, response);
    }
}
