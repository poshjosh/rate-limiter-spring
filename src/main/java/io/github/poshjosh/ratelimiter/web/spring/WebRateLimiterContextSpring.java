package io.github.poshjosh.ratelimiter.web.spring;

import io.github.poshjosh.ratelimiter.web.core.WebRateLimiterContext;
import io.github.poshjosh.ratelimiter.web.spring.uri.ResourceInfoProviderSpring;

public final class WebRateLimiterContextSpring {
    private WebRateLimiterContextSpring() {}
    public static WebRateLimiterContext.Builder builder() {
        return WebRateLimiterContext.builder()
            .resourceInfoProvider(new ResourceInfoProviderSpring())
            .classesInPackageFinder(new ClassesInPackageFinderSpring());
    }
}
