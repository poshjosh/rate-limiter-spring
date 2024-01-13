package io.github.poshjosh.ratelimiter.web.spring;

import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterConfig;
import io.github.poshjosh.ratelimiter.web.spring.uri.ResourceInfoProviderSpring;

public final class ResourceLimiterConfigSpring {
    private ResourceLimiterConfigSpring() {}
    public static ResourceLimiterConfig.Builder builder() {
        return ResourceLimiterConfig.builder()
            .resourceInfoProvider(new ResourceInfoProviderSpring())
            .classesInPackageFinder(new ClassesInPackageFinderSpring());
    }
}
