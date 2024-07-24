package io.github.poshjosh.ratelimiter.web.spring;

import io.github.poshjosh.ratelimiter.web.core.WebRateLimiterContext;
import io.github.poshjosh.ratelimiter.web.spring.uri.ResourceInfoProviderSpring;

final class WebRateLimiterContextSpring {
    private WebRateLimiterContextSpring() {}
    static WebRateLimiterContext.Builder builder() {
        return WebRateLimiterContext.builder()
            .resourceInfoProvider(new ResourceInfoProviderSpring())
            .classesInPackageFinder(new ClassesInPackageFinderSpring());
    }
}
