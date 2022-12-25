package com.looseboxes.ratelimiter.web.spring.weblayertests;

import com.looseboxes.ratelimiter.*;
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
    public static final int LIMIT = 100;
    public static final long DURATION_SECONDS = 60;

    private final String testCacheName = this.getClass().getPackage().getName() + ".cache";
    private final ConcurrentMapCacheManager concurrentMapCacheManager = new ConcurrentMapCacheManager();
    private final RateCacheWithKeys rateCache;

    public TestRateLimiterConfiguration(RateLimitPropertiesSpring properties) {
        concurrentMapCacheManager.setCacheNames(Collections.singletonList(testCacheName));
        properties.setResourcePackages(Collections.singletonList(AbstractResourceTest.class.getPackage().getName()));
        properties.setRateLimitConfigs(Collections.singletonMap("default", getRateLimitConfigList()));
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
        return webRequestRateLimiterConfigBuilder
            .rateLimiterConfig(RateLimiterConfig.builder().rateCache(this.rateCache).build())
            .build();
    }

    private Rates getRateLimitConfigList() {
        return Rates.of(Operator.OR, getRateLimits());
    }

    private Rate[] getRateLimits() {
        Rate config = Rate.of(LIMIT, Duration.ofSeconds(DURATION_SECONDS));
        return new Rate[]{config};
    }
}
