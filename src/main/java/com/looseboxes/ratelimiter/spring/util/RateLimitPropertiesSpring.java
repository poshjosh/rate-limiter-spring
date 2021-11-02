package com.looseboxes.ratelimiter.spring.util;

import com.looseboxes.ratelimiter.config.RateLimitProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rate-limiter", ignoreUnknownFields = false)
public class RateLimitPropertiesSpring extends RateLimitProperties {

}
