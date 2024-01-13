package io.github.poshjosh.ratelimiter.web.spring;

import io.github.poshjosh.ratelimiter.web.core.RateLimiterContext;
import io.github.poshjosh.ratelimiter.web.core.RateLimiterRegistry;

public final class RateLimiterRegistrySpring {

    public static RateLimiterRegistry ofDefaults() {
        return of(RateLimiterContextSpring.builder().build());
    }

    public static RateLimiterRegistry of(RateLimiterContext rateLimiterContext) {
        return RateLimiterRegistry.of(rateLimiterContext);
    }

    private RateLimiterRegistrySpring() {}
}
