package io.github.poshjosh.ratelimiter.web.spring;

import io.github.poshjosh.ratelimiter.model.Rates;
import io.github.poshjosh.ratelimiter.util.RateLimitProperties;

import java.util.*;

public class RateLimitPropertiesSpring implements RateLimitProperties {

    private List<String> resourcePackages = Collections.emptyList();

    private List<Class<?>> resourceClasses = Collections.emptyList();

    private Boolean disabled = Boolean.FALSE;

    private List<Rates> rateLimitConfigs = Collections.emptyList();

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
    public List<Rates> getRates() {
        return rateLimitConfigs;
    }

    public void setRateLimitConfigs(List<Rates> rateLimitConfigs) {
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
