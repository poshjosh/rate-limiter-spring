package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.*;
import com.looseboxes.ratelimiter.web.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Configuration
@ConditionalOnProperty(prefix = "rate-limiter", name = "disabled", havingValue = "false", matchIfMissing = true)
public abstract class ResourceLimiterConfiguration implements ResourceLimiterConfigurer<HttpServletRequest> {

    private final RateLimitPropertiesSpring properties;

    protected ResourceLimiterConfiguration(RateLimitPropertiesSpring properties) {
        this.properties = Objects.requireNonNull(properties);
    }

    @Bean
    public ResourceLimiter<HttpServletRequest> resourceLimiter(ResourceLimiterRegistry registry) {
        return registry.createResourceLimiter();
    }

    @Bean
    public ResourceLimiterRegistry resourceLimiterRegistry() {
        return ResourceLimiterRegistry.of(resourceLimiterConfigBuilder().build());
    }

    protected ResourceLimiterConfig.Builder<HttpServletRequest> resourceLimiterConfigBuilder() {
        return ResourceLimiterConfigSpring.builder()
                .configurer(this)
                .properties(properties);
    }
}
