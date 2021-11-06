package com.looseboxes.ratelimiter.spring.web;

import com.looseboxes.ratelimiter.spring.util.ConditionalOnRateLimiterEnabled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConditionalOnRateLimiterEnabled
public class RequestToIdConverterRegistry {

    private final Map<String, RequestToIdConverter> converters;

    public RequestToIdConverterRegistry() {
        converters = new HashMap<>();
    }

    public void setConverter(String rateLimiterName, RequestToIdConverter requestToIdConverter) {
        converters.put(rateLimiterName, requestToIdConverter);
    }

    public RequestToIdConverter getConverter(String rateLimiterName) {
        return converters.get(rateLimiterName);
    }
}
