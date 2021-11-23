package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.RateExceededHandler;
import com.looseboxes.ratelimiter.RateLimiter;
import com.looseboxes.ratelimiter.RateSupplier;
import com.looseboxes.ratelimiter.annotation.AnnotatedElementIdProvider;
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

@Configuration
public class RateLimiterWebMvcConfigurer implements WebMvcConfigurer {

    private final HandlerInterceptor handlerInterceptor;

    public RateLimiterWebMvcConfigurer(
            RateSupplier rateSupplier,
            RateExceededHandler rateExceededHandler,
            RateLimiter<HttpServletRequest> rateLimiter,
            RequestToIdConverter<HttpServletRequest> requestToIdConverter,
            ResourceClassesSupplier resourceClassesSupplier,
            RateLimitProperties properties) {

        RateLimitHandler<HttpServletRequest> rateLimitHandler = new RateLimitHandler<>(
                properties,
                rateSupplier,
                rateExceededHandler,
                rateLimiter,
                requestToIdConverter,
                annotatedElementIdProviderForClass(),
                annotatedElementIdProviderForMethod(),
                resourceClassesSupplier.get()
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

    public AnnotatedElementIdProvider<Method, PathPatterns<String>> annotatedElementIdProviderForMethod() {
        return new MethodIdProvider(annotatedElementIdProviderForClass());
    }

    public AnnotatedElementIdProvider<Class<?>, PathPatterns<String>> annotatedElementIdProviderForClass() {
        return new ClassIdProvider();
    }
}
