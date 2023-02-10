package io.github.poshjosh.ratelimiter.web.spring;

import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterConfig;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterRegistry;

public final class ResourceLimiterRegistrySpring {

    public static ResourceLimiterRegistry ofDefaults() {
        return of(ResourceLimiterConfigSpring.builder().build());
    }

    public static ResourceLimiterRegistry of(ResourceLimiterConfig resourceLimiterConfig) {
        return ResourceLimiterRegistry.of(resourceLimiterConfig);
    }

    private ResourceLimiterRegistrySpring() {}
}
