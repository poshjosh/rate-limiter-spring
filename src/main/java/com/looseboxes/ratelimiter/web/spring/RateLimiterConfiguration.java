package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.*;
import com.looseboxes.ratelimiter.annotation.AnnotationProcessor;
import com.looseboxes.ratelimiter.annotation.ClassAnnotationProcessor;
import com.looseboxes.ratelimiter.annotation.IdProvider;
import com.looseboxes.ratelimiter.util.ClassesInPackageFinder;
import com.looseboxes.ratelimiter.web.core.*;
import com.looseboxes.ratelimiter.web.core.util.PathPatterns;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
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

    public static final class RequestToUriConverter implements RequestToIdConverter<HttpServletRequest, String>{
        @Override
        public String convert(HttpServletRequest request) {
            return request.getRequestURI();
        }
    }

    @Bean
    public RateLimiter<HttpServletRequest> rateLimiter(
            RateLimiterConfigurationSource<HttpServletRequest> rateLimiterConfigurationSource,
            RateLimiterNodeContext<HttpServletRequest, ?> rateLimiterNodeContext) {
        return new WebRequestRateLimiter<>(rateLimiterConfigurationSource, rateLimiterNodeContext);
    }

    @Bean
    public RateLimiterNodeContext<HttpServletRequest, ?> rateLimiterNodeContext(
            RateLimitPropertiesSpring properties,
            RateLimiterConfigurationSource<HttpServletRequest> rateLimiterConfigurationSource,
            ResourceClassesSupplier resourceClassesSupplier,
            AnnotationProcessor<Class<?>> annotationProcessor) {
        return new RateLimiterNodeContext<>(
                properties, rateLimiterConfigurationSource, resourceClassesSupplier.get(), annotationProcessor);
    }

    @Bean
    public RequestToIdConverter<HttpServletRequest, String> requestToIdConverter() {
        return new RequestToUriConverter();
    }

    @Bean
    public RateLimiterConfigurationSource<HttpServletRequest> rateLimiterConfigurationSource(
            MatcherRegistry<HttpServletRequest> matcherRegistry,
            RateLimiterFactory<Object> rateLimiterFactory,
            @Autowired(required = false) RateLimiterConfigurer<HttpServletRequest> rateLimiterConfigurer) {
        return new RateLimiterConfigurationSource<>(
                matcherRegistry, newRateLimiterConfig(), rateLimiterFactory, rateLimiterConfigurer);
    }

    protected RateLimiterConfig<Object, Object> newRateLimiterConfig() {
        return new DefaultRateLimiterConfig<>();
    }

    @Bean
    public MatcherRegistry<HttpServletRequest> matcherRegistry(
            RequestToIdConverter<HttpServletRequest, String> requestToUriConverter) {
        return new DefaultMatcherRegistry<>(requestToUriConverter, newClassPathPatternsProvider(), newMethodPathPatternsProvider());
    }

    protected IdProvider<Class<?>, PathPatterns<String>> newClassPathPatternsProvider() {
        return new ClassPathPatternsProvider();
    }

    protected IdProvider<Method, PathPatterns<String>> newMethodPathPatternsProvider() {
        return new MethodPathPatternsProvider();
    }

    @Bean
    public RateLimiterFactory<Object> rateLimiterFactory() {
        return new DefaultRateLimiterFactory<>();
    }

    @Bean
    public ResourceClassesSupplier resourceClassesSupplier(RateLimitProperties properties, ClassesInPackageFinder classesInPackageFinder) {
        return new DefaultResourceClassesSupplier(
                classesInPackageFinder, properties.getResourcePackages(),
                Controller.class, RestController.class);
    }

    @Bean
    public ClassesInPackageFinder classesInPackageFinder() {
        return new ClassesInPackageFinderSpring();
    }

    @Bean
    public AnnotationProcessor<Class<?>> annotationProcessor() {
        return new ClassAnnotationProcessor();
    }
}
