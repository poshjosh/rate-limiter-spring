package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.RateExceededExceptionThrower;
import com.looseboxes.ratelimiter.RateExceededHandler;
import com.looseboxes.ratelimiter.RateLimiter;
import com.looseboxes.ratelimiter.RateSupplier;
import com.looseboxes.ratelimiter.cache.RateCache;
import com.looseboxes.ratelimiter.cache.RateCacheInMemory;
import com.looseboxes.ratelimiter.rates.LimitWithinDuration;
import com.looseboxes.ratelimiter.rates.Rate;
import com.looseboxes.ratelimiter.web.spring.repository.RateRepository;
import com.looseboxes.ratelimiter.web.spring.repository.RateRepositoryForCachedLimitWithinDuration;
import com.looseboxes.ratelimiter.web.core.RateLimiterConfigurer;
import com.looseboxes.ratelimiter.web.core.RateLimiterFromProperties;
import com.looseboxes.ratelimiter.web.core.RequestToIdConverterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;

@Configuration
@ConditionalOnRateLimiterEnabled
public class RateLimiterConfiguration {

    public static final class RateSupplierImpl implements RateSupplier {
        @Override
        public Rate getInitialRate() {
            return new LimitWithinDuration();
        }
    }

    @Bean
    @ConditionalOnRateLimiterEnabled
    public RateLimiter<HttpServletRequest> rateLimiter(RateLimitPropertiesImpl properties,
                                                       RequestToIdConverterRegistry<HttpServletRequest> requestToIdConverterRegistry,
                                                       RateCache<Object> rateCache,
                                                       RateSupplier rateSupplier,
                                                       RateExceededHandler rateExceededHandler) {

        return new RateLimiterFromProperties<>(properties, requestToIdConverterRegistry, rateCache, rateSupplier, rateExceededHandler);
    }

    @Bean
    @ConditionalOnRateLimiterEnabled
    public RateSupplier newRateSupplier() {
        return  new RateSupplierImpl();
    }

    @Bean
    @ConditionalOnRateLimiterEnabled
    public RateCache<Object> rateCache() {
        return new RateCacheInMemory<>();
    }

    @Bean
    @ConditionalOnRateLimiterEnabled
    public RateRepository<Object, ?> rateRepository(RateCache<Object> rateCache) {
        return new RateRepositoryForCachedLimitWithinDuration<>(rateCache);
    }

    @Bean
    @ConditionalOnRateLimiterEnabled
    public RateExceededHandler rateExceededHandler() {
        return new RateExceededExceptionThrower();
    }

    @Bean
    @ConditionalOnRateLimiterEnabled
    public RequestToIdConverterRegistry<HttpServletRequest> requestToIdConverterRegistry(
            @Autowired(required = false) RateLimiterConfigurer<HttpServletRequest> rateLimiterConfigurer) {
        return new RequestToIdConverterRegistryImpl(rateLimiterConfigurer);
    }
}
