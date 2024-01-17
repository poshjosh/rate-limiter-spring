package io.github.poshjosh.ratelimiter.web.spring;

import io.github.poshjosh.ratelimiter.web.core.WebRateLimiterContext;
import io.github.poshjosh.ratelimiter.web.core.WebRateLimiterRegistry;

public final class RateLimiterRegistrySpring {

    public static WebRateLimiterRegistry ofDefaults() {
        return of(RateLimiterWebContextSpring.builder().build());
    }

    public static WebRateLimiterRegistry of(WebRateLimiterContext webRateLimiterContext) {
        return WebRateLimiterRegistry.of(webRateLimiterContext);
    }

    private RateLimiterRegistrySpring() {}
}
