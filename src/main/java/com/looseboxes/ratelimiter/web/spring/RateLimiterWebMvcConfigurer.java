package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.RateLimiter;
import com.looseboxes.ratelimiter.annotation.*;
import com.looseboxes.ratelimiter.util.RateLimitGroupData;
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
import java.util.Map;

@Configuration
public class RateLimiterWebMvcConfigurer implements WebMvcConfigurer {

    private final HandlerInterceptor handlerInterceptor;

    public RateLimiterWebMvcConfigurer(
            RateLimitProperties properties,
            RateLimiter<HttpServletRequest> rateLimiter,
            RateLimiterConfigurationSource<HttpServletRequest> rateLimiterConfigurationSource,
            ResourceClassesSupplier resourceClassesSupplier) {

        RateLimitHandler<HttpServletRequest> rateLimitHandler = new RateLimitHandler<>(
                properties,
                rateLimiter,
                rateLimiterConfigurationSource,
                resourceClassesSupplier,
                classAnnotationProcessor(),
                methodAnnotationProcessor(),
                classAnnotationCollector(),
                methodAnnotationCollector(),
                classIdProvider(),
                methodIdProvider()
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

    private AnnotationProcessor<Class<?>> classAnnotationProcessor() {
        return new ClassAnnotationProcessor();
    }

    private AnnotationProcessor<Method> methodAnnotationProcessor() {
        return new MethodAnnotationProcessor();
    }

    private AnnotationCollector<Class<?>, Map<String, RateLimitGroupData<Class<?>>>> classAnnotationCollector() {
        return new ClassAnnotationCollector();
    }

    private AnnotationCollector<Method, Map<String, RateLimitGroupData<Method>>> methodAnnotationCollector() {
        return new MethodAnnotationCollector();
    }

    public IdProvider<Class<?>, PathPatterns<String>> classIdProvider() {
        return new ClassIdProvider();
    }

    public IdProvider<Method, PathPatterns<String>> methodIdProvider() {
        return new MethodIdProvider(classIdProvider());
    }
}
