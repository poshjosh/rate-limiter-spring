package io.github.poshjosh.ratelimiter.web.spring;

import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterConfig;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterRegistry;

import javax.servlet.http.HttpServletRequest;

public final class ResourceLimiterRegistrySpring {

    public static ResourceLimiterRegistry<HttpServletRequest> ofDefaults() {
        return of(ResourceLimiterConfigSpring.builder().build());
    }

    public static ResourceLimiterRegistry<HttpServletRequest> of(
            ResourceLimiterConfig<HttpServletRequest> resourceLimiterConfig) {
        return ResourceLimiterRegistry.of(resourceLimiterConfig);
    }

    private ResourceLimiterRegistrySpring() {}
}
