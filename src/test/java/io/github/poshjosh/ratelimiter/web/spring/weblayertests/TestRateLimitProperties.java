package io.github.poshjosh.ratelimiter.web.spring.weblayertests;

import io.github.poshjosh.ratelimiter.web.spring.RateLimitPropertiesSpring;
import org.springframework.boot.context.properties.ConfigurationProperties;
@ConfigurationProperties(prefix = "rate-limiter", ignoreUnknownFields = false)
public class TestRateLimitProperties extends RateLimitPropertiesSpring { }
