package io.github.poshjosh.ratelimiter.web.spring.weblayertests;

import io.github.poshjosh.ratelimiter.web.core.WebRateLimiterRegistry;
import io.github.poshjosh.ratelimiter.web.spring.repository.RateCache;
import io.github.poshjosh.ratelimiter.web.spring.repository.RateCacheSpring;
import io.github.poshjosh.ratelimiter.web.spring.repository.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;

@TestConfiguration
@EnableConfigurationProperties(TestRateLimitProperties.class)
public class TestRateLimiterConfiguration {

    private final RateCache<Object> rateCache;

    public TestRateLimiterConfiguration() {
        String testCacheName = this.getClass().getPackage().getName() + ".cache";
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager(testCacheName);
        this.rateCache = new RateCacheSpring<>(cacheManager.getCache(testCacheName));
    }

    @Bean
    public RateCache<Object> rateCache() {
        return rateCache;
    }

    @Bean
    public RateRepository<RateEntity<Object>, Object> rateRepository() {
        return new RateRepositoryForCache<>(this.rateCache);
    }

    @Bean
    public WebRateLimiterRegistry rateLimiterRegistry(TestRateLimitingFilter filter) {
        return filter.getRateLimiterRegistry();
    }
}
