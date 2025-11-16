package org.recnos.pg.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@RequiredArgsConstructor
public class RateLimitConfig {

    private final FileUploadProperties fileUploadProperties;
    private final Map<UUID, Bucket> cache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(UUID userId) {
        return cache.computeIfAbsent(userId, this::newBucket);
    }

    private Bucket newBucket(UUID userId) {
        FileUploadProperties.RateLimit rateLimitConfig = fileUploadProperties.getRateLimit();

        Bandwidth limit = Bandwidth.classic(
                rateLimitConfig.getCapacity(),
                Refill.intervally(
                        rateLimitConfig.getRefillTokens(),
                        Duration.ofHours(rateLimitConfig.getRefillPeriod())
                )
        );

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    public void clearUserBucket(UUID userId) {
        cache.remove(userId);
    }

    public void clearAllBuckets() {
        cache.clear();
    }
}
