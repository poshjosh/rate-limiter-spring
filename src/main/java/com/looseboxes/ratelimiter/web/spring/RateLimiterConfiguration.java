package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.*;
import com.looseboxes.ratelimiter.annotation.AnnotationProcessor;
import com.looseboxes.ratelimiter.annotation.ClassAnnotationProcessor;
import com.looseboxes.ratelimiter.web.core.*;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class RateLimiterConfiguration {

    public static final class RequestToUriConverter implements RequestToIdConverter<HttpServletRequest, String>{
        @Override
        public String convert(HttpServletRequest request) {
            return request.getRequestURI();
        }
    }

    @Bean
    public RateLimiter<HttpServletRequest> rateLimiter(
            RateLimitProperties properties,
            RateLimiterConfigurationSource<HttpServletRequest> rateLimiterConfigurationSource,
            ResourceClassesSupplier resourceClassesSupplier,
            AnnotationProcessor<Class<?>> annotationProcessor) {
        return new WebRequestRateLimiter<>(
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
                matcherRegistry, new DefaultRateLimiterConfig<>(), rateLimiterFactory, rateLimiterConfigurer);
    }

    @Bean
    public MatcherRegistry<HttpServletRequest> matcherRegistry(
            RequestToIdConverter<HttpServletRequest, String> requestToUriConverter) {
        return new DefaultMatcherRegistry<>(requestToUriConverter, new ClassPathPatternsProvider(), new MethodPathPatternsProvider());
    }

    @Bean
    public RateLimiterFactory<Object> rateLimiterFactory() {
        return new DefaultRateLimiterFactory<>();
    }

    @Bean
    public ResourceClassesSupplier resourceClassesSupplier(RateLimitProperties properties) {
        return new DefaultResourceClassesSupplier(
                new ClassesInPackageFinderSpring(), properties.getResourcePackages(),
                Controller.class, RestController.class);
    }

    @Bean
    public AnnotationProcessor<Class<?>> annotationProcessor() {
        return new ClassAnnotationProcessor();
    }
}
