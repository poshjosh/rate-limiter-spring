package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.util.Rates;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.*;

@ConfigurationProperties(prefix = "rate-limiter", ignoreUnknownFields = false)
public class RateLimitPropertiesSpring implements RateLimitProperties {

    private List<String> resourcePackages;

    private Boolean disabled = Boolean.FALSE;

    private Map<String, Rates> rateLimitConfigs;

    @Override
    public List<String> getResourcePackages() {
        return resourcePackages;
    }

    public void setResourcePackages(List<String> resourcePackages) {
        this.resourcePackages = resourcePackages;
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
                ", disabled=" + disabled +
                ", rateLimitConfigs=" + rateLimitConfigs +
                '}';
    }
}
