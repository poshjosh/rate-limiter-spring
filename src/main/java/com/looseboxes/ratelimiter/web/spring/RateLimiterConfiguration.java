package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.*;
import com.looseboxes.ratelimiter.annotation.AnnotationProcessor;
import com.looseboxes.ratelimiter.annotation.ClassAnnotationProcessor;
import com.looseboxes.ratelimiter.cache.RateCache;
import com.looseboxes.ratelimiter.cache.InMemoryRateCache;
import com.looseboxes.ratelimiter.util.Experimental;
import com.looseboxes.ratelimiter.web.core.*;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import com.looseboxes.ratelimiter.web.spring.repository.LimitWithinDurationDTO;
import com.looseboxes.ratelimiter.web.spring.repository.LimitWithinDurationRepository;
import com.looseboxes.ratelimiter.web.spring.repository.RateRepository;
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
    public RequestToIdConverter<HttpServletRequest, String> requestToIdConverter() {
        return new RequestToUriConverter();
    }

    @Bean
    public RateFactory rateFactory() {
        return  new LimitWithinDurationFactory();
    }

    @Bean
    public RateCache<Object> rateCache() {
        return new InMemoryRateCache<>();
    }

    @Bean
    public RateExceededListener rateRecordedListener() {
        return new RateExceededExceptionThrower();
    }

    @Bean
    public RateLimiterConfigurationSource<HttpServletRequest> rateLimiterConfigurationSource(
            RequestToIdConverter<HttpServletRequest, String> requestToUriConverter,
            RateCache<Object> rateCache,
            RateFactory rateFactory,
            RateExceededListener rateExceededListener,
            RateLimiterProvider rateLimiterProvider,
            @Autowired(required = false) RateLimiterConfigurer<HttpServletRequest> rateLimiterConfigurer) {

        return new RateLimiterConfigurationSource<>(
                requestToUriConverter, rateCache, rateFactory, rateExceededListener, rateLimiterProvider,
                rateLimiterConfigurer, new ClassIdProvider(), new MethodIdProvider());
    }

    @Bean
    public RateLimiterProvider rateLimiterProvider() {
        return new DefaultRateLimiterProvider();
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

    @Bean
    @Experimental
    public RateRepository<Object, LimitWithinDurationDTO<Object>> rateRepository(RateCache<Object> rateCache) {
        // @TODO This will not work if the user over rides the default case where we use only once cache
        // This override could be done by registering one or more other caches
        return new LimitWithinDurationRepository<>(rateCache);
    }
}
