package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.RateLimiter;
import com.looseboxes.ratelimiter.annotation.IdProvider;
import com.looseboxes.ratelimiter.web.core.*;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;

@Configuration
public class RateLimiterWebMvcConfigurer implements WebMvcConfigurer {

    private final HandlerInterceptor handlerInterceptor;

    public RateLimiterWebMvcConfigurer(
            RateLimiter<HttpServletRequest> rateLimiter,
            RateLimiterConfigurationSource<HttpServletRequest> rateLimiterConfigurationSource,
            ResourceClassesSupplier resourceClassesSupplier,
            RateLimitProperties properties) {

        List<Class<?>> classes = resourceClassesSupplier.get();

        RateLimiter<String> classRateLimiter = classes.isEmpty() ? RateLimiter.noop() : new ClassPatternsRateLimiter<>(
                classes, rateLimiterConfigurationSource, classIdProvider());

        RateLimiter<String> methodRateLimiter = classes.isEmpty() ? RateLimiter.noop() : new MethodPatternsRateLimiter<>(
                classes, rateLimiterConfigurationSource, methodIdProvider());

        RateLimitHandler<HttpServletRequest> rateLimitHandler = new RateLimitHandler<>(
                properties,
                rateLimiter,
                rateLimiterConfigurationSource.getDefaultRequestToIdConverter(),
                classRateLimiter, methodRateLimiter
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

    public IdProvider<Method, PathPatterns<String>> methodIdProvider() {
        return new MethodIdProvider(classIdProvider());
    }

    public IdProvider<Class<?>, PathPatterns<String>> classIdProvider() {
        return new ClassIdProvider();
    }
}
