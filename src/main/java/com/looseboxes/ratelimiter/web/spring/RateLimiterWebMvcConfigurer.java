package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.annotation.AnnotationProcessor;
import com.looseboxes.ratelimiter.web.core.*;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class RateLimiterWebMvcConfigurer implements WebMvcConfigurer {

    private final HandlerInterceptor handlerInterceptor;

    public RateLimiterWebMvcConfigurer(
            RateLimitProperties properties,
            RateLimiterConfigurationSource<HttpServletRequest> rateLimiterConfigurationSource,
            ResourceClassesSupplier resourceClassesSupplier,
            AnnotationProcessor<Class<?>> annotationProcessor) {

        RateLimitHandler<HttpServletRequest> rateLimitHandler = new RateLimitHandler<>(
                properties, rateLimiterConfigurationSource, resourceClassesSupplier.get(), annotationProcessor
        );

        handlerInterceptor = new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                rateLimitHandler.handleRequest(request);
                return true;
            }
        };
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(handlerInterceptor);
    }
}
