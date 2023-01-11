package io.github.poshjosh.ratelimiter.web.spring;

import io.github.poshjosh.ratelimiter.util.Rates;
import io.github.poshjosh.ratelimiter.web.core.util.RateLimitProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.*;

@ConfigurationProperties(prefix = "rate-limiter", ignoreUnknownFields = false)
public class RateLimitPropertiesSpring implements RateLimitProperties {

    private List<String> resourcePackages = Collections.emptyList();

    private List<Class<?>> resourceClasses = Collections.emptyList();

    private Boolean disabled = Boolean.FALSE;

    private Map<String, Rates> rateLimitConfigs = Collections.emptyMap();

    @Override
    public List<String> getResourcePackages() {
        return resourcePackages;
    }

    public void setResourcePackages(List<String> resourcePackages) {
        this.resourcePackages = resourcePackages;
    }

    @Override
    public List<Class<?>> getResourceClasses() {
        return resourceClasses;
    }

    public void setResourceClasses(List<Class<?>> resourceClasses) {
        this.resourceClasses = resourceClasses;
    }

    @Override
    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public Map<String, Rates> getRateLimitConfigs() {
        return rateLimitConfigs;
    }

    public void setRateLimitConfigs(Map<String, Rates> rateLimitConfigs) {
        this.rateLimitConfigs = rateLimitConfigs;
    }

    @Override
    public String toString() {
        return "RateLimitPropertiesSpring{" +
                "resourcePackages=" + resourcePackages +
                ", resourceClasses=" + resourceClasses +
                ", disabled=" + disabled +
                ", rateLimitConfigs=" + rateLimitConfigs +
                '}';
    }
}
