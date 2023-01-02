package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.*;
import com.looseboxes.ratelimiter.web.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;

@Configuration
@ConditionalOnProperty(prefix = "rate-limiter", name = "disabled", havingValue = "false", matchIfMissing = true)
public class ResourceLimiterConfiguration {

    @Bean
    public ResourceLimiter<HttpServletRequest> resourceLimiter(
            WebResourceLimiterConfig<HttpServletRequest> webResourceLimiterConfig) {
        return ResourceLimiterRegistry.of(webResourceLimiterConfig).createResourceLimiter();
    }

    @Bean
    public WebResourceLimiterConfig<HttpServletRequest> webRequestRateLimiterConfig(
            WebResourceLimiterConfig.Builder<HttpServletRequest> webRequestRateLimiterConfigBuilder) {
        return webRequestRateLimiterConfigBuilder.build();
    }

    @Bean
    public WebResourceLimiterConfig.Builder<HttpServletRequest> webRequestRateLimiterConfigBuilder(
            @Autowired(required = false) ResourceLimiterConfigurer<HttpServletRequest> configurer,
            RateLimitPropertiesSpring properties) {
        return WebResourceLimiterConfigSpring.builder()
                .configurer(configurer)
                .properties(properties);
    }
}
