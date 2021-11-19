package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.RateExceededHandler;
import com.looseboxes.ratelimiter.RateLimiter;
import com.looseboxes.ratelimiter.RateSupplier;
import com.looseboxes.ratelimiter.annotation.AnnotatedElementIdProvider;
import com.looseboxes.ratelimiter.util.*;
import com.looseboxes.ratelimiter.web.core.PathPatterns;
import com.looseboxes.ratelimiter.web.core.RateLimiterFromClassLevelAnnotations;
import com.looseboxes.ratelimiter.web.core.RateLimiterFromMethodLevelAnnotations;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

@Configuration
@ConditionalOnRateLimiterEnabled
public class RateLimiterWebMvcConfigurer implements WebMvcConfigurer {

    private final RateLimiter<String> rateLimiterForClassLevelAnnotations;

    private final RateLimiter<String> rateLimiterForMethodLevelAnnotations;

    public RateLimiterWebMvcConfigurer(
            RateSupplier rateSupplier,
            RateExceededHandler rateExceededHandler,
            RateLimitPropertiesImpl properties) {

        final List<Class<?>> classes = getClasses(properties);

        this.rateLimiterForClassLevelAnnotations = classes.isEmpty() ? RateLimiter.noop() :
                new RateLimiterFromClassLevelAnnotations<>(
                        rateSupplier, rateExceededHandler, classes,
                        annotatedElementIdProviderForClass()
                );

        this.rateLimiterForMethodLevelAnnotations = classes.isEmpty() ? RateLimiter.noop() :
                new RateLimiterFromMethodLevelAnnotations<>(
                        rateSupplier, rateExceededHandler, classes,
                        annotatedElementIdProviderForMethod()
                );
    }

    private List<Class<?>> getClasses(RateLimitProperties properties) {
        final List<String> resourcePackages = properties.getResourcePackages();
        final List<Class<?>> classes;
        if(resourcePackages == null || resourcePackages.isEmpty()) {
            classes = Collections.emptyList();
        }else{
            classes = new ClassesInPackageFinderSpring().findClasses(resourcePackages, new ClassFilterForAnnotations(Controller.class, RestController.class));
        }
        return classes;
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {

        RateLimitingInterceptorForRequest rateLimitingInterceptor = new RateLimitingInterceptorForRequest(
                rateLimiterForClassLevelAnnotations, rateLimiterForMethodLevelAnnotations
        );

        registry.addInterceptor(rateLimitingInterceptor);
    }

    public AnnotatedElementIdProvider<Method, PathPatterns<String>> annotatedElementIdProviderForMethod() {
        return new AnnotatedElementIdProviderForMethod(annotatedElementIdProviderForClass());
    }

    public AnnotatedElementIdProvider<Class<?>, PathPatterns<String>> annotatedElementIdProviderForClass() {
        return new AnnotatedElementIdProviderForClass();
    }
}
