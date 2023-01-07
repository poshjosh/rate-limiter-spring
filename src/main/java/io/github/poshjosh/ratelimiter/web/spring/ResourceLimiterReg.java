package io.github.poshjosh.ratelimiter.web.spring;

import io.github.poshjosh.ratelimiter.ResourceLimiter;
import io.github.poshjosh.ratelimiter.UsageListener;
import io.github.poshjosh.ratelimiter.cache.RateCache;
import io.github.poshjosh.ratelimiter.util.Matcher;
import io.github.poshjosh.ratelimiter.web.core.Registry;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterRegistry;
import io.github.poshjosh.ratelimiter.web.core.util.RateLimitProperties;

import javax.servlet.http.HttpServletRequest;

public class ResourceLimiterReg implements ResourceLimiterRegistry<HttpServletRequest> {

    public static ResourceLimiterReg spring() {
        return new ResourceLimiterReg();
    }

    @Override public Registry<ResourceLimiter<?>> limiters() {
        return null;
    }

    @Override public Registry<Matcher<HttpServletRequest, ?>> matchers() {
        return null;
    }

    @Override public Registry<RateCache<?>> caches() {
        return null;
    }

    @Override public Registry<UsageListener> listeners() {
        return null;
    }

    @Override public ResourceLimiter<HttpServletRequest> createResourceLimiter() {
        return null;
    }

    @Override public boolean isRateLimited(String id) {
        return false;
    }

    @Override public RateLimitProperties properties() {
        return null;
    }
}
