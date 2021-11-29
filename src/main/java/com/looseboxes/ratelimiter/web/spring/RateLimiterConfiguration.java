package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.*;
import com.looseboxes.ratelimiter.annotation.AnnotationProcessor;
import com.looseboxes.ratelimiter.annotation.DefaultAnnotationProcessor;
import com.looseboxes.ratelimiter.cache.RateCache;
import com.looseboxes.ratelimiter.cache.InMemoryRateCache;
import com.looseboxes.ratelimiter.util.Experimental;
import com.looseboxes.ratelimiter.web.core.*;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import com.looseboxes.ratelimiter.web.spring.repository.CachingRateRecordedListener;
import com.looseboxes.ratelimiter.web.spring.repository.RateRepository;
import com.looseboxes.ratelimiter.web.spring.repository.LimitWithinDurationRepository;
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

    @Experimental
    private final RateCache<Object> rateCacheForRepository;

    public RateLimiterConfiguration() {
        this.rateCacheForRepository = getGlobalRateCache();
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
    public RateRecordedListener rateRecordedListener() {
        return new RateExceededExceptionThrower();
    }

    @Bean
    public RateLimiterConfigurationSource<HttpServletRequest> rateLimiterConfigurationSource(
            RequestToIdConverter<HttpServletRequest, String> requestToUriConverter,
            RateCache<Object> rateCache,
            RateFactory rateFactory,
            RateRecordedListener rateRecordedListener,
            @Autowired(required = false) RateLimiterConfigurer<HttpServletRequest> rateLimiterConfigurer) {

        RateLimiterConfigurationSource<HttpServletRequest> configurationSource = new RateLimiterConfigurationSource<>(
                requestToUriConverter, rateCache, rateFactory, rateRecordedListener, rateLimiterConfigurer,
                new ClassIdProvider(), new MethodIdProvider());

        // This listener caches rates for use by RateRepository - currently an experimental feature
        configurationSource.registerRootRateRecordedListener(new CachingRateRecordedListener(rateCacheForRepository));

        return configurationSource;
    }

    @Bean
    @Experimental
    public RateRepository<Object, ?> rateRepository() {
        return new LimitWithinDurationRepository<>(rateCacheForRepository);
    }

    /**
     * Experimental feature.
     * @return A cache for caching all rates recorded by this application.
     */
    @Experimental
    protected RateCache<Object> getGlobalRateCache() {
        return new InMemoryRateCache<>();
    }

    @Bean
    public ResourceClassesSupplier resourceClassesSupplier(RateLimitProperties properties) {
        return new ResourceClassesSupplierImpl(
                new ClassesInPackageFinderSpring(), properties.getResourcePackages(),
                Controller.class, RestController.class);
    }

    @Bean
    public AnnotationProcessor<Class<?>> annotationProcessor() {
        return new DefaultAnnotationProcessor();
    }
}
