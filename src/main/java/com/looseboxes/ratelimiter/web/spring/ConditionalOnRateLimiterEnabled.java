package com.looseboxes.ratelimiter.web.spring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.lang.annotation.*;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE, ElementType.METHOD})
@Documented
@ConditionalOnProperty(value="rate-limiter.disabled", havingValue = "false", matchIfMissing = true)
public @interface ConditionalOnRateLimiterEnabled {
}

