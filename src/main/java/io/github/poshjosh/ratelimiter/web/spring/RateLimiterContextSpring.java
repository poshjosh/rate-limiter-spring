package io.github.poshjosh.ratelimiter.web.spring;

import io.github.poshjosh.ratelimiter.web.core.RateLimiterContext;
import io.github.poshjosh.ratelimiter.web.spring.uri.ResourceInfoProviderSpring;

public final class RateLimiterContextSpring {
    private RateLimiterContextSpring() {}
    public static RateLimiterContext.Builder builder() {
        return RateLimiterContext.builder()
            .resourceInfoProvider(new ResourceInfoProviderSpring())
            .classesInPackageFinder(new ClassesInPackageFinderSpring());
    }
}
