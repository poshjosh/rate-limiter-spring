package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.web.core.RateLimiterConfigurer;
import com.looseboxes.ratelimiter.web.core.RequestToIdConverterRegistry;

import javax.servlet.http.HttpServletRequest;

public class RequestToIdConverterRegistryImpl extends RequestToIdConverterRegistry<HttpServletRequest>{
    public RequestToIdConverterRegistryImpl(RateLimiterConfigurer<HttpServletRequest> rateLimiterConfigurer) {
        super(rateLimiterConfigurer);
    }
}
