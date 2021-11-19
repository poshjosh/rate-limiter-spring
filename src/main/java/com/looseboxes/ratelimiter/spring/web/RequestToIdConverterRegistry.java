package com.looseboxes.ratelimiter.spring.web;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class RequestToIdConverterRegistry {

    private static final class DefaultRequestToIdConverter implements RequestToIdConverter{
        @Override
        public Object convert(HttpServletRequest request) {
            return request.getRequestURI();
        }
    }

    private final Map<String, RequestToIdConverter> converters;

    public RequestToIdConverterRegistry(RateLimiterConfigurer rateLimiterConfigurer) {
        converters = new HashMap<>();
        if(rateLimiterConfigurer != null) {
            rateLimiterConfigurer.addConverters(this);
        }
    }

    public void registerDefaultConverter(String rateLimiterName) {
        registerConverter(rateLimiterName, new DefaultRequestToIdConverter());
    }

    public void registerConverter(String rateLimiterName, RequestToIdConverter requestToIdConverter) {
        converters.put(rateLimiterName, requestToIdConverter);
    }

    public RequestToIdConverter getConverter(String rateLimiterName) {
        return converters.get(rateLimiterName);
    }
}
