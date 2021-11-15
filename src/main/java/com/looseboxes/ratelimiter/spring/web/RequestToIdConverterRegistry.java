package com.looseboxes.ratelimiter.spring.web;

import java.util.HashMap;
import java.util.Map;

public class RequestToIdConverterRegistry {

    private final Map<String, RequestToIdConverter> converters;

    public RequestToIdConverterRegistry(RateLimiterConfigurer rateLimiterConfigurer) {
        converters = new HashMap<>();
        if(rateLimiterConfigurer != null) {
            rateLimiterConfigurer.addConverters(this);
        }
    }

    public void setConverter(String rateLimiterName, RequestToIdConverter requestToIdConverter) {
        converters.put(rateLimiterName, requestToIdConverter);
    }

    public RequestToIdConverter getConverter(String rateLimiterName) {
        return converters.get(rateLimiterName);
    }
}
