package com.abhi.FitnessTracker.Config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Rate limiting filter for auth endpoints to prevent brute-force attacks.
 * Limits requests per IP address using the token bucket algorithm.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    // Max 20 auth requests per minute per IP
    private static final int AUTH_CAPACITY = 20;
    private static final Duration AUTH_REFILL_DURATION = Duration.ofMinutes(1);

    // Max 5 password reset requests per hour per IP
    private static final int RESET_CAPACITY = 5;
    private static final Duration RESET_REFILL_DURATION = Duration.ofHours(1);

    private final ConcurrentMap<String, Bucket> authBuckets = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Bucket> resetBuckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String clientIp = getClientIp(request);

        if (path.contains("/api/auth/forgot-password") || path.contains("/api/auth/reset-password")) {
            Bucket bucket = resetBuckets.computeIfAbsent(clientIp, k -> createResetBucket());
            if (!bucket.tryConsume(1)) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Too many requests. Please try again later.\"}");
                return;
            }
        } else if (path.startsWith("/api/auth/")) {
            Bucket bucket = authBuckets.computeIfAbsent(clientIp, k -> createAuthBucket());
            if (!bucket.tryConsume(1)) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Too many login attempts. Please try again later.\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private Bucket createAuthBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(AUTH_CAPACITY)
                        .refillGreedy(AUTH_CAPACITY, AUTH_REFILL_DURATION)
                        .build())
                .build();
    }

    private Bucket createResetBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(RESET_CAPACITY)
                        .refillGreedy(RESET_CAPACITY, RESET_REFILL_DURATION)
                        .build())
                .build();
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
