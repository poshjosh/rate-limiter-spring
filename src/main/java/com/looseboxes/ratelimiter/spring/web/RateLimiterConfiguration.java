package com.looseboxes.ratelimiter.spring.web;

import com.looseboxes.ratelimiter.RateLimiter;
import com.looseboxes.ratelimiter.RateSupplier;
import com.looseboxes.ratelimiter.annotation.AnnotatedElementIdProvider;
import com.looseboxes.ratelimiter.annotation.RateFactoryForClassLevelAnnotation;
import com.looseboxes.ratelimiter.annotation.RateFactoryForMethodLevelAnnotation;
import com.looseboxes.ratelimiter.rates.LimitWithinDuration;
import com.looseboxes.ratelimiter.spring.util.ClassFilterForAnnotation;
import com.looseboxes.ratelimiter.spring.util.ClassesInPackageFinderSpring;
import com.looseboxes.ratelimiter.util.ClassFilter;
import com.looseboxes.ratelimiter.util.ClassesInPackageFinder;
import com.looseboxes.ratelimiter.util.RateFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

@Configuration
public class RateLimiterConfiguration extends WebMvcConfigurationSupport {

    private final String controllerPackageName;

    public RateLimiterConfiguration(@Value("${rate-limiter.controller-package:''}") String controllerPackageName) {
        this.controllerPackageName = controllerPackageName;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        if(StringUtils.hasText(controllerPackageName)) {

            final RateSupplier rateSupplier = () -> new LimitWithinDuration();

            RateLimitingInterceptorForRequest rateLimitingInterceptor = new RateLimitingInterceptorForRequest(
                    rateLimiterForClassLevelAnnotation(rateSupplier), rateLimiterForMethodLevelAnnotation(rateSupplier)
            );

            registry.addInterceptor(rateLimitingInterceptor);
        }
    }

    public RateLimiter<HttpServletRequest> rateLimiterForClassLevelAnnotation(RateSupplier rateSupplier) {
        return new RateLimiterForClassLevelAnnotation(rateSupplier, rateFactoryForClassLevelAnnotation().getRates());
    }

    public RateLimiter<HttpServletRequest> rateLimiterForMethodLevelAnnotation(RateSupplier rateSupplier) {
        return new RateLimiterForMethodLevelAnnotation(rateSupplier, rateFactoryForMethodLevelAnnotation().getRates());
    }

    public RateFactory<AnnotatedRequestMapping> rateFactoryForClassLevelAnnotation() {
        return new RateFactoryForClassLevelAnnotation(getClasses(), annotatedElementIdProviderForClass());
    }

    public RateFactory<AnnotatedRequestMapping> rateFactoryForMethodLevelAnnotation() {
        return new RateFactoryForMethodLevelAnnotation(getClasses(), annotatedElementIdProviderForMethod());
    }

    private List<Class<?>> getClasses() {
        if(!StringUtils.hasText(controllerPackageName)) {
            return Collections.emptyList();
        }
        return classesInPackageFinder().findClasses(controllerPackageName, classFilter());
    }

    public ClassesInPackageFinder classesInPackageFinder() {
        return new ClassesInPackageFinderSpring();
    }

    public ClassFilter classFilter() {
        return new ClassFilterForAnnotation(RestController.class);
    }

    public AnnotatedElementIdProvider<Method, AnnotatedRequestMapping> annotatedElementIdProviderForMethod() {
        return new AnnotatedElementIdProviderForMethodMappings(annotatedElementIdProviderForClass());
    }

    public AnnotatedElementIdProvider<Class<?>, AnnotatedRequestMapping> annotatedElementIdProviderForClass() {
        return new AnnotatedElementIdProviderForRequestMapping();
    }
}
