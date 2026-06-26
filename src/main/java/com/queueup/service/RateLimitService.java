package com.queueup.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    // Allow 1 request per 10 seconds per IP
    private final Bandwidth limit = Bandwidth.classic(
            1,
            Refill.intervally(1, Duration.ofSeconds(10))
    );

    public boolean isAllowed(String clientIp) {
        Bucket bucket = buckets.computeIfAbsent(clientIp, k ->
                Bucket.builder().addLimit(limit).build()
        );

        boolean allowed = bucket.tryConsume(1);

        // Cleanup old entries if map gets too large
        if (buckets.size() > 10000) {
            buckets.clear();
        }

        return allowed;
    }
}