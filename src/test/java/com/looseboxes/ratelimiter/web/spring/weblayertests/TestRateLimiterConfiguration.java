package com.looseboxes.ratelimiter.web.spring.weblayertests;

import com.looseboxes.ratelimiter.*;
import com.looseboxes.ratelimiter.annotation.IdProvider;
import com.looseboxes.ratelimiter.util.Operator;
import com.looseboxes.ratelimiter.util.Rate;
import com.looseboxes.ratelimiter.util.Rates;
import com.looseboxes.ratelimiter.web.core.WebRequestRateLimiterConfig;
import com.looseboxes.ratelimiter.web.spring.RateLimitPropertiesSpring;
import com.looseboxes.ratelimiter.web.spring.RateLimiterConfiguration;
import com.looseboxes.ratelimiter.web.spring.SpringRateCache;
import com.looseboxes.ratelimiter.web.spring.repository.*;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Collections;

@TestConfiguration
public class TestRateLimiterConfiguration extends RateLimiterConfiguration{

    public static final int LIMIT = 3;
    public static final long DURATION_SECONDS = 1;

    private final String testCacheName = this.getClass().getPackage().getName() + ".cache";
    private final ConcurrentMapCacheManager concurrentMapCacheManager = new ConcurrentMapCacheManager();
    private final RateCacheWithKeys rateCache;

    private static String methodBoundToPropertyRates;

    public static String getMethodBoundToPropertyRates() {
        return methodBoundToPropertyRates;
    }

    public TestRateLimiterConfiguration(RateLimitPropertiesSpring properties) {
        concurrentMapCacheManager.setCacheNames(Collections.singletonList(testCacheName));
        properties.setResourcePackages(Collections.singletonList(AbstractResourceTest.class.getPackage().getName()));
        this.rateCache = new RateCacheWithKeysImpl<>(
                new SpringRateCache<>(concurrentMapCacheManager.getCache(testCacheName))
        );
    }

    @Bean
    public RateRepository<RateEntity<Object>, Object> rateRepository() {
        return new RateRepositoryForCache<>(this.rateCache);
    }

    @Bean
    @Override
    public WebRequestRateLimiterConfig<HttpServletRequest> webRequestRateLimiterConfig(
            WebRequestRateLimiterConfig.Builder<HttpServletRequest> webRequestRateLimiterConfigBuilder) {
        WebRequestRateLimiterConfig<HttpServletRequest> config = webRequestRateLimiterConfigBuilder
            .rateLimiterConfig(RateLimiterConfig.builder().rateCache(this.rateCache).build())
            .build();
        methodBoundToPropertyRates = initMethodBoundToPropertyRates(config);
        ((RateLimitPropertiesSpring)config.getProperties()).setRateLimitConfigs(
                Collections.singletonMap(methodBoundToPropertyRates, getRateLimitConfigList()));
        return config;
    }

    private String initMethodBoundToPropertyRates(WebRequestRateLimiterConfig<HttpServletRequest> config) {
        try {
            return config.getMethodIdProvider()
                    .getId(PropertiesBoundLimitTest.Resource.class.getMethod("home", HttpServletRequest.class));
        } catch(NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private Rates getRateLimitConfigList() {
        return Rates.of(Operator.OR, getRateLimits());
    }

    private Rate[] getRateLimits() {
        return new Rate[]{Rate.of(LIMIT, Duration.ofSeconds(DURATION_SECONDS))};
    }
}
