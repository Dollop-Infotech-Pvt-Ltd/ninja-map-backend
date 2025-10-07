package com.ninjamap.app.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

/**
 * Configuration for IP-based rate limiting using Bucket4j and Caffeine cache.
 * Each IP + URL combination has its own bucket to track requests.
 * The refill strategy uses intervally refill to strictly enforce request limits.
 */
@Configuration
public class RateLimiterConfig {

    private static final Duration ONE_MINUTE = Duration.ofMinutes(1);

    /**
     * Defines the rate limits for each URL or URL pattern.
     * 
     * @return Map of URL -> Bandwidth (rate limit)
     */
    @Bean
    Map<String, Bandwidth> urlRateLimits() {
        Map<String, Bandwidth> map = new HashMap<>();

        // ====================== LOGIN / AUTH ======================
        // Limit login requests to 5 per minute per IP
        map.put("/api/auth/login", intervallyLimit(5));
        map.put("/api/admin/auth/login", intervallyLimit(5));

        // Limit registration requests to 5 per minute per IP
        map.put("/api/auth/register", intervallyLimit(5));

        // Limit forgot-password requests to 5 per minute per IP
        map.put("/api/auth/forget-password", intervallyLimit(5));
        map.put("/api/admin/auth/forget-password", intervallyLimit(5));

        // OTP verification requests: 10 per minute per IP
        map.put("/api/auth/verify-otp", intervallyLimit(10));
        map.put("/api/admin/auth/verify-otp", intervallyLimit(10));

        // Resend OTP requests: 5 per minute per IP
        map.put("/api/auth/resend-otp", intervallyLimit(5));
        map.put("/api/admin/auth/resend-otp", intervallyLimit(5));

        // Reset password requests: 5 per minute per IP
        map.put("/api/auth/reset-password", intervallyLimit(5));
        map.put("/api/admin/auth/reset-password", intervallyLimit(5));

        // Refresh token requests: 10 per minute per IP
        map.put("/api/auth/refresh-token", intervallyLimit(10));
        map.put("/api/admin/auth/refresh-token", intervallyLimit(10));

        return map;
    }

    /**
     * Creates a Caffeine cache to store IP-specific buckets.
     * Each IP + URL combination has its own bucket, automatically removed after inactivity.
     * 
     * @return Cache<String, Bucket>
     */
    @Bean
    Cache<String, Bucket> ipBucketCache() {
        return Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofMinutes(15)) // Remove inactive entries after 15 min
                .maximumSize(10_000)                      // Max 10k IP+URL combinations
                .build();
    }

    /**
     * Helper method to create a new Bucket with intervally refill strategy.
     * 
     * @param capacity Maximum tokens (requests) allowed
     * @return Bucket instance
     */
    public static Bucket newBucket(Bandwidth bandwidth) {
        return Bucket.builder().addLimit(bandwidth).build();
    }

    /**
     * Helper method to create Bandwidth with intervally refill.
     * 
     * @param capacity Max requests per minute
     * @return Bandwidth instance
     */
    private Bandwidth intervallyLimit(long capacity) {
        return Bandwidth.classic(capacity, Refill.intervally(capacity, ONE_MINUTE));
    }
}
