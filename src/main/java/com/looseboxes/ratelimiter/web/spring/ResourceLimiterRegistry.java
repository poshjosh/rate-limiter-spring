package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.web.core.AbstractResourceLimiterRegistry;
import com.looseboxes.ratelimiter.web.core.ResourceLimiterConfig;

import javax.servlet.http.HttpServletRequest;

public final class ResourceLimiterRegistry extends
        AbstractResourceLimiterRegistry<HttpServletRequest> {

    public static ResourceLimiterRegistry ofDefaults() {
        return of(ResourceLimiterConfigSpring.builder().build());
    }

    public static ResourceLimiterRegistry of(
            ResourceLimiterConfig<HttpServletRequest> resourceLimiterConfig) {
        return new ResourceLimiterRegistry(resourceLimiterConfig);
    }

    private ResourceLimiterRegistry(
            ResourceLimiterConfig<HttpServletRequest> resourceLimiterConfig) {
        super(resourceLimiterConfig);
    }
}
