package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.*;
import com.looseboxes.ratelimiter.web.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;

@Configuration
@ConditionalOnProperty(prefix = "rate-limiter", name = "disabled", havingValue = "false", matchIfMissing = true)
@EnableConfigurationProperties(RateLimitPropertiesSpring.class)
public class ResourceLimiterConfiguration {

    public ResourceLimiterConfiguration() {}

    @Bean
    public ResourceLimiter<HttpServletRequest> resourceLimiter(ResourceLimiterRegistry registry) {
        return registry.createResourceLimiter();
    }

    @Bean
    public ResourceLimiterRegistry resourceLimiterRegistry(
            RateLimitPropertiesSpring properties,
            @Autowired(required = false) ResourceLimiterConfigurer<HttpServletRequest> configurer) {
        return ResourceLimiterRegistry
                .of(resourceLimiterConfigBuilder().properties(properties).configurer(configurer).build());
    }

    protected ResourceLimiterConfig.Builder<HttpServletRequest> resourceLimiterConfigBuilder() {
        return ResourceLimiterConfigSpring.builder();
    }
}
