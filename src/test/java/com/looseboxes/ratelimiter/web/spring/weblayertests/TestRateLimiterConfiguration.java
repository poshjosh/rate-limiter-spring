package com.looseboxes.ratelimiter.web.spring.weblayertests;

import com.looseboxes.ratelimiter.*;
import com.looseboxes.ratelimiter.bandwidths.Bandwidths;
import com.looseboxes.ratelimiter.util.Operator;
import com.looseboxes.ratelimiter.web.core.util.RateConfig;
import com.looseboxes.ratelimiter.web.core.util.RateLimitConfig;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

@TestConfiguration
public class TestRateLimiterConfiguration extends RateLimiterConfiguration{

    private final String testCacheName = this.getClass().getPackage().getName() + ".cache";
    private final ConcurrentMapCacheManager concurrentMapCacheManager = new ConcurrentMapCacheManager();
    private final RateCacheWithKeys rateCache;

    public TestRateLimiterConfiguration(RateLimitPropertiesSpring properties) {
        concurrentMapCacheManager.setCacheNames(Collections.singletonList(testCacheName));
        properties.setResourcePackages(Collections.singletonList(ResourceWithMethodLimits.class.getPackage().getName()));
        properties.setRateLimitConfigs(Collections.singletonMap("default", getRateLimitConfigList()));
        this.rateCache = new RateCacheWithKeysImpl<>(
                new SpringRateCache<>(concurrentMapCacheManager.getCache(testCacheName))
        );
    }

    @Bean
    public RateRepository<RateEntity<Object>, Object> rateRepository() {
        return new RateRepositoryForCache<>(this.rateCache);
    }

    public static boolean useInterval = false;
    private static class TestRateLimiter extends DefaultRateLimiter {
        private final Bandwidths bandwidths;
        private TestRateLimiter(RateLimiterConfig rateLimiterConfig, Bandwidths bandwidths) {
            super(rateLimiterConfig, bandwidths);
            this.bandwidths = bandwidths;
        }
        @Override
        public boolean tryConsume(Object context, Object resourceId, int permits, long timeout, TimeUnit unit) {
            System.out.printf("\n= = = = = = =\nResource: %s, permits: %d, timeout: %d %s\n= = = = = = =\n",
                    resourceId, permits, timeout, unit);
            if (useInterval && timeout == 0) {
                final long interval = bandwidths.getStableIntervalMicros();
                System.out.println("Using stable interval as timeout: " + ((double)interval/1_000_000) + " seconds");
                return super.tryConsume(context, resourceId, permits, interval, TimeUnit.MICROSECONDS);
            }
            return super.tryConsume(context, resourceId, permits, timeout, unit);
        }
    }

    private static class TestRateLimiterFactory implements RateLimiterFactory<HttpServletRequest>{
        @Override
        public RateLimiter<HttpServletRequest> createRateLimiter(
                RateLimiterConfig<HttpServletRequest, ?> rateLimiterConfig, Bandwidths bandwidths) {
            return new TestRateLimiter(rateLimiterConfig, bandwidths);
        }
    }

    @Bean
    @Override
    public WebRequestRateLimiterConfig<HttpServletRequest> webRequestRateLimiterConfig(
            WebRequestRateLimiterConfig.Builder<HttpServletRequest> webRequestRateLimiterConfigBuilder) {
        return webRequestRateLimiterConfigBuilder
            .rateLimiterConfig(RateLimiterConfig.builder().rateCache(this.rateCache).build())
            .rateLimiterFactory(new TestRateLimiterFactory())
            .build();
    }

    private RateLimitConfig getRateLimitConfigList() {
        RateLimitConfig rateLimitConfig = new RateLimitConfig();
        rateLimitConfig.setLimits(getRateLimits());
        rateLimitConfig.setOperator(Operator.OR);
        return rateLimitConfig;
    }

    private List<RateConfig> getRateLimits() {
        RateConfig config = RateConfig.of(
                Constants.OVERALL_LIMIT, Duration.ofSeconds(Constants.OVERALL_DURATION_SECONDS));
        return Collections.singletonList(config);
    }
}
