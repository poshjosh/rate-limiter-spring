package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.*;
import com.looseboxes.ratelimiter.annotation.IdProvider;
import com.looseboxes.ratelimiter.util.ClassesInPackageFinder;
import com.looseboxes.ratelimiter.web.core.*;
import com.looseboxes.ratelimiter.web.core.impl.DefaultWebRequestRateLimiterConfigBuilder;
import com.looseboxes.ratelimiter.web.core.impl.WebRequestRateLimiter;
import com.looseboxes.ratelimiter.web.core.util.PathPatterns;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Configuration
@ConditionalOnProperty(prefix = "rate-limiter", name = "disabled", havingValue = "false", matchIfMissing = true)
public class RateLimiterConfiguration {

    @Bean
    public RateLimiter<HttpServletRequest> rateLimiter(
            WebRequestRateLimiterConfig<HttpServletRequest> webRequestRateLimiterConfig) {
        return new WebRequestRateLimiter<>(webRequestRateLimiterConfig);
    }

    @Bean
    public WebRequestRateLimiterConfig<HttpServletRequest> webRequestRateLimiterConfig(
            WebRequestRateLimiterConfigBuilder<HttpServletRequest> webRequestRateLimiterConfigBuilder) {
        return webRequestRateLimiterConfigBuilder.build();
    }

    @Bean
    public WebRequestRateLimiterConfigBuilder<HttpServletRequest> webRequestRateLimiterConfigBuilder(
            @Autowired(required = false) RateLimiterConfigurer<HttpServletRequest> configurer,
            RateLimitPropertiesSpring properties,
            RequestToIdConverter<HttpServletRequest, String> requestToUriConverter,
            ClassesInPackageFinder classesInPackageFinder) {

        return WebRequestRateLimiterConfig.<HttpServletRequest>builder()
                .configurer(configurer)
                .properties(properties)
                .requestToIdConverter(requestToUriConverter)
                .classesInPackageFinder(classesInPackageFinder)
                .classPathPatternsProvider(classPathPatternsProvider())
                .methodPathPatternsProvider(methodPathPatternsProvider())
                .resourceAnnotationTypes(new Class[]{ Controller.class, RestController.class });
    }

    @Bean
    public RequestToIdConverter<HttpServletRequest, String> requestToIdConverter() {
        return new RequestToUriConverter();
    }

    public IdProvider<Class<?>, PathPatterns<String>> classPathPatternsProvider() {
        return new ClassPathPatternsProvider();
    }

    public IdProvider<Method, PathPatterns<String>> methodPathPatternsProvider() {
        return new MethodPathPatternsProvider();
    }

    @Bean
    public ClassesInPackageFinder classesInPackageFinder() {
        return new ClassesInPackageFinderSpring();
    }
}
