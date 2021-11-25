package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.*;
import com.looseboxes.ratelimiter.cache.RateCache;
import com.looseboxes.ratelimiter.cache.InMemoryRateCache;
import com.looseboxes.ratelimiter.web.core.*;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
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

    public static final class RequestToUriConverter implements RequestToIdConverter<HttpServletRequest>{
        @Override
        public Object convert(HttpServletRequest request) {
            return request.getRequestURI();
        }
    }

    @Bean
    public RequestToIdConverter<HttpServletRequest> requestToIdConverter() {
        return new RequestToUriConverter();
    }

    @Bean
    public RateLimiter<HttpServletRequest> rateLimiter(RateLimitProperties properties,
                                                       RateLimiterConfigurationRegistry<HttpServletRequest> rateLimiterConfigurationRegistry) {

        return new RateLimiterImpl<>(properties.getRateLimitConfigs(), rateLimiterConfigurationRegistry);
    }

    @Bean
    public RateSupplier newRateSupplier() {
        return  new LimitWithinDurationSupplier();
    }

    @Bean
    public RateCache<Object> rateCache() {
        return new InMemoryRateCache<>();
    }

    @Bean
    public RateRepository<Object, ?> rateRepository(RateCache<Object> rateCache) {
        return new LimitWithinDurationRepository<>(rateCache);
    }

    @Bean
    public RateExceededHandler rateExceededHandler() {
        return new RateExceededExceptionThrower();
    }

    @Bean
    public RateLimiterConfigurationRegistry<HttpServletRequest> requestToIdConverterRegistry(
            RequestToIdConverter<HttpServletRequest> defaultRequestToIdConverter,
            RateCache<Object> rateCache,
            RateSupplier rateSupplier,
            RateExceededHandler rateExceededHandler,
            @Autowired(required = false) RateLimiterConfigurer<HttpServletRequest> rateLimiterConfigurer) {
        return new RateLimiterConfigurationRegistry<>(
                defaultRequestToIdConverter, rateCache, rateSupplier, rateExceededHandler, rateLimiterConfigurer);
    }

    @Bean
    public ResourceClassesSupplier resourceClassesSupplier(RateLimitProperties properties) {
        return new ResourceClassesSupplierImpl(
                new ClassesInPackageFinderSpring(), properties.getResourcePackages(),
                Controller.class, RestController.class);
    }
}
