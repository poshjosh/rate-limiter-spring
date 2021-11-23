package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.rates.Rate;
import com.looseboxes.ratelimiter.web.core.util.RateLimitConfigList;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.*;

@ConfigurationProperties(prefix = "rate-limiter", ignoreUnknownFields = false)
public class RateLimitPropertiesImpl implements RateLimitProperties {

    private List<String> resourcePackages;

    private Boolean auto = Boolean.TRUE;

    private Boolean disabled = Boolean.FALSE;

    private Map<String, RateLimitConfigList> rateLimitConfigs;

    @Override
    public List<String> getResourcePackages() {
        return resourcePackages;
    }

    public void setResourcePackages(List<String> resourcePackages) {
        this.resourcePackages = resourcePackages;
    }

    @Override
    public Boolean getAuto() {
        return auto;
    }

    public void setAuto(Boolean auto) {
        this.auto = auto;
    }

    @Override
    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public Map<String, RateLimitConfigList> getRateLimitConfigs() {
        return rateLimitConfigs;
    }

    public void setRateLimitConfigs(Map<String, RateLimitConfigList> rateLimitConfigs) {
        this.rateLimitConfigs = rateLimitConfigs;
    }

    @Override
    public String toString() {
        return "RateLimitPropertiesImpl{" +
                "resourcePackages=" + resourcePackages +
                ", auto=" + auto +
                ", disabled=" + disabled +
                ", rateLimitConfigs=" + rateLimitConfigs +
                '}';
    }
}
