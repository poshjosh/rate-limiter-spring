package com.looseboxes.ratelimiter.spring.util;

import com.looseboxes.ratelimiter.rates.Rate;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.function.Function;

@ConfigurationProperties(prefix = "rate-limiter", ignoreUnknownFields = false)
public class RateLimitProperties {

    private String controllerPackage;

    private Boolean disabled;

    private Map<String, RateLimitConfig> rateLimits;

    public Optional<Function<HttpServletRequest, Object>> getRequestToIdConverterFunctionInstanceOptional(String name) {
        return rateLimits == null || rateLimits.get(name) == null ? Optional.empty() :
                rateLimits.get(name).getRequestToIdConverterFunctionInstanceOptional();
    }

    public Map<String, Rate> toRates() {
        final Map<String, Rate> rateMap;
        if(Boolean.TRUE.equals(disabled)) {
            rateMap = Collections.emptyMap();
        }else if(rateLimits == null || rateLimits.isEmpty()) {
            rateMap = Collections.emptyMap();
        }else {
            Map<String, Rate> temp = new LinkedHashMap<>(rateLimits.size());
            rateLimits.forEach((name, rateLimitConfig) -> {
                temp.put(name, rateLimitConfig.toRate());
            });
            rateMap = Collections.unmodifiableMap(temp);
        }
        return rateMap;
    }

    public String getControllerPackage() {
        return controllerPackage;
    }

    public void setControllerPackage(String controllerPackage) {
        this.controllerPackage = controllerPackage;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Map<String, RateLimitConfig> getRateLimits() {
        return rateLimits;
    }

    public void setRateLimits(Map<String, RateLimitConfig> rateLimits) {
        this.rateLimits = rateLimits;
    }

    @Override
    public String toString() {
        return "RateLimitProperties{" +
                "controllerPackage='" + controllerPackage + '\'' +
                ", disabled=" + disabled +
                ", rateLimits=" + rateLimits +
                '}';
    }
}
