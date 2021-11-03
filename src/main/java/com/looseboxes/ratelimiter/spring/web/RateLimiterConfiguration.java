package com.looseboxes.ratelimiter.spring.web;

import com.looseboxes.ratelimiter.RateExceededExceptionThrower;
import com.looseboxes.ratelimiter.RateExceededHandler;
import com.looseboxes.ratelimiter.RateLimiter;
import com.looseboxes.ratelimiter.RateSupplier;
import com.looseboxes.ratelimiter.cache.RateCache;
import com.looseboxes.ratelimiter.cache.RateCacheInMemory;
import com.looseboxes.ratelimiter.rates.LimitWithinDuration;
import com.looseboxes.ratelimiter.rates.Rate;
import com.looseboxes.ratelimiter.spring.repository.RateRepository;
import com.looseboxes.ratelimiter.spring.repository.RateRepositoryForCachedLimitWithinDuration;
import com.looseboxes.ratelimiter.spring.util.ConditionalOnRateLimiterEnabled;
import com.looseboxes.ratelimiter.spring.util.RateLimitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ConcurrentHashMap;

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
    public RateLimiter<HttpServletRequest> rateLimiter(RateLimitProperties properties,
                                                       RateCache rateCache,
                                                       RateSupplier rateSupplier,
                                                       RateExceededHandler rateExceededHandler) {
        return new RateLimiterHttpServletRequest(properties, rateCache, rateSupplier, rateExceededHandler);
    }

    @Bean
    public RateSupplier newRateSupplier() {
        return  new RateSupplierImpl();
    }

    @Bean
    public RateCache rateCache() {
        return new RateCacheInMemory(new ConcurrentHashMap());
    }

    @Bean
    public RateRepository rateRepository(RateCache rateCache) {
        return new RateRepositoryForCachedLimitWithinDuration(rateCache);
    }

    @Bean
    public RateExceededHandler rateExceededHandler() {
        return new RateExceededExceptionThrower();
    }
}
