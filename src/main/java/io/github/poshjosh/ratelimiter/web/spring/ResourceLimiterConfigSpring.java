package io.github.poshjosh.ratelimiter.web.spring;

import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterConfig;
import io.github.poshjosh.ratelimiter.web.spring.uri.PathPatternsProviderSpring;

import javax.servlet.http.HttpServletRequest;

public final class ResourceLimiterConfigSpring {
    private ResourceLimiterConfigSpring() {}
    public static ResourceLimiterConfig.Builder<HttpServletRequest> builder() {
        return ResourceLimiterConfig.builder(HttpServletRequest.class)
            .pathPatternsProvider(new PathPatternsProviderSpring())
            .classesInPackageFinder(new ClassesInPackageFinderSpring());
    }
}
