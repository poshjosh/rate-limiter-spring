package io.github.poshjosh.ratelimiter.web.spring;

import io.github.poshjosh.ratelimiter.ResourceLimiter;
import io.github.poshjosh.ratelimiter.web.core.*;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterConfig;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger LOG = LoggerFactory.getLogger(ResourceLimiterConfiguration.class);

    public ResourceLimiterConfiguration() {}

    @Bean
    public ResourceLimiter<HttpServletRequest> resourceLimiter(
            ResourceLimiterRegistry<HttpServletRequest> resourceLimiterRegistry) {
        LOG.info(resourceLimiterRegistry.isRateLimitingEnabled()
                ? "Completed setup of automatic rate limiting" : "Rate limiting is disabled");
        return resourceLimiterRegistry.createResourceLimiter();
    }

    @Bean
    public ResourceLimiterRegistry<HttpServletRequest> resourceLimiterRegistry(
            RateLimitPropertiesSpring properties,
            @Autowired(required = false) ResourceLimiterConfigurer<HttpServletRequest> configurer) {
        return ResourceLimiterRegistrySpring
                .of(resourceLimiterConfigBuilder().properties(properties).configurer(configurer).build());
    }

    protected ResourceLimiterConfig.Builder<HttpServletRequest> resourceLimiterConfigBuilder() {
        return ResourceLimiterConfigSpring.builder();
    }
}
