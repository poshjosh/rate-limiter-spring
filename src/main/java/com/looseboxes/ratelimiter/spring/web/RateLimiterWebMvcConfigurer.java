package com.looseboxes.ratelimiter.spring.web;

import com.looseboxes.ratelimiter.RateExceededHandler;
import com.looseboxes.ratelimiter.RateLimiter;
import com.looseboxes.ratelimiter.RateSupplier;
import com.looseboxes.ratelimiter.annotation.AnnotatedElementIdProvider;
import com.looseboxes.ratelimiter.annotation.RateFactoryForClassLevelAnnotation;
import com.looseboxes.ratelimiter.annotation.RateFactoryForMethodLevelAnnotation;
import com.looseboxes.ratelimiter.spring.util.ClassFilterForAnnotations;
import com.looseboxes.ratelimiter.spring.util.ClassesInPackageFinderSpring;
import com.looseboxes.ratelimiter.spring.util.ConditionalOnRateLimiterEnabled;
import com.looseboxes.ratelimiter.spring.util.RateLimitProperties;
import com.looseboxes.ratelimiter.util.ClassFilter;
import com.looseboxes.ratelimiter.util.ClassesInPackageFinder;
import com.looseboxes.ratelimiter.util.RateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@ConditionalOnRateLimiterEnabled
public class RateLimiterWebMvcConfigurer implements WebMvcConfigurer {

    private final Logger log = LoggerFactory.getLogger(RateLimiterWebMvcConfigurer.class);

    private final List<String> controllerPackages;

    private final RateSupplier rateSupplier;

    private final RateExceededHandler rateExceededHandler;

    public RateLimiterWebMvcConfigurer(
            @Autowired(required = false) RateLimitProperties properties,
            @Autowired(required = false) RateSupplier rateSupplier,
            @Autowired(required = false) RateExceededHandler rateExceededHandler,
            @Autowired(required = false) RateLimiterConfigurer rateLimiterConfigurer,
            @Autowired(required = false) RequestToIdConverterRegistry requestToIdConverterRegistry) {
        this.controllerPackages = properties.getControllerPackages();
        this.rateSupplier = rateSupplier;
        this.rateExceededHandler = rateExceededHandler;
        if(rateLimiterConfigurer != null && requestToIdConverterRegistry != null) {
            log.debug("Adding Converters to registry");
            rateLimiterConfigurer.addConverters(requestToIdConverterRegistry);
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        if(controllerPackages == null || controllerPackages.isEmpty()) {
            return;
        }

        RateLimitingInterceptorForRequest rateLimitingInterceptor = new RateLimitingInterceptorForRequest(
                rateLimiterForClassLevelAnnotation(), rateLimiterForMethodLevelAnnotation()
        );

        registry.addInterceptor(rateLimitingInterceptor);
    }

    public RateLimiter<HttpServletRequest> rateLimiterForClassLevelAnnotation() {
        return new RateLimiterForClassLevelAnnotation(
                rateSupplier, rateFactoryForClassLevelAnnotation().getRates(), rateExceededHandler);
    }

    public RateLimiter<HttpServletRequest> rateLimiterForMethodLevelAnnotation() {
        return new RateLimiterForMethodLevelAnnotation(
                rateSupplier, rateFactoryForMethodLevelAnnotation().getRates(), rateExceededHandler);
    }

    public RateFactory<AnnotatedRequestMapping> rateFactoryForClassLevelAnnotation() {
        return new RateFactoryForClassLevelAnnotation(getClasses(), annotatedElementIdProviderForClass());
    }

    public RateFactory<AnnotatedRequestMapping> rateFactoryForMethodLevelAnnotation() {
        return new RateFactoryForMethodLevelAnnotation(getClasses(), annotatedElementIdProviderForMethod());
    }

    private List<Class<?>> getClasses() {
        if(controllerPackages == null || controllerPackages.isEmpty()) {
            return Collections.emptyList();
        }
        List<Class<?>> result = new ArrayList<>();
        ClassesInPackageFinder classesInPackageFinder = classesInPackageFinder();
        ClassFilter classFilter = classFilter();
        for(String controllerPackage : controllerPackages) {
            List<Class<?>> packageClasses = classesInPackageFinder.findClasses(controllerPackage, classFilter);
            log.debug("Controller package: {}, classes: {}", controllerPackage);
            result.addAll(packageClasses);
        }
        return result;
    }

    public ClassesInPackageFinder classesInPackageFinder() {
        return new ClassesInPackageFinderSpring();
    }

    public ClassFilter classFilter() {
        return new ClassFilterForAnnotations(Controller.class, RestController.class);
    }

    public AnnotatedElementIdProvider<Method, AnnotatedRequestMapping> annotatedElementIdProviderForMethod() {
        return new AnnotatedElementIdProviderForMethodMappings(annotatedElementIdProviderForClass());
    }

    public AnnotatedElementIdProvider<Class<?>, AnnotatedRequestMapping> annotatedElementIdProviderForClass() {
        return new AnnotatedElementIdProviderForRequestMapping();
    }
}
