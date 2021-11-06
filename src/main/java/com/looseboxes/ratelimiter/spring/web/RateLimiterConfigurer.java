package com.looseboxes.ratelimiter.spring.web;

public interface RateLimiterConfigurer {
    void addConverters(RequestToIdConverterRegistry registry);
}
