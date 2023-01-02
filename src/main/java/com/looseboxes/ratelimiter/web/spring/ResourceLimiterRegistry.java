package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.web.core.AbstractResourceLimiterRegistry;
import com.looseboxes.ratelimiter.web.core.WebResourceLimiterConfig;

import javax.servlet.http.HttpServletRequest;

public final class ResourceLimiterRegistry extends
        AbstractResourceLimiterRegistry<HttpServletRequest> {

    public static ResourceLimiterRegistry ofDefaults() {
        return of(WebResourceLimiterConfigSpring.builder().build());
    }

    public static ResourceLimiterRegistry of(
            WebResourceLimiterConfig<HttpServletRequest> webResourceLimiterConfig) {
        return new ResourceLimiterRegistry(webResourceLimiterConfig);
    }

    private ResourceLimiterRegistry(
            WebResourceLimiterConfig<HttpServletRequest> webResourceLimiterConfig) {
        super(webResourceLimiterConfig);
    }
}
