package io.github.poshjosh.ratelimiter.web.spring.weblayertests;

import io.github.poshjosh.ratelimiter.store.BandwidthsStore;
import io.github.poshjosh.ratelimiter.web.core.WebRateLimiterRegistry;
import io.github.poshjosh.ratelimiter.web.spring.repository.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;

@TestConfiguration
@EnableConfigurationProperties(TestRateLimitProperties.class)
public class TestRateLimiterConfiguration {

    private final BandwidthsStore<String> bandwidthsStore;

    public TestRateLimiterConfiguration() {
        String testCacheName = this.getClass().getPackage().getName() + ".cache";
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager(testCacheName);
        this.bandwidthsStore = new BandwidthStoreSpring<>(cacheManager.getCache(testCacheName));
    }

    @Bean
    public BandwidthsStore<String> bandwidthStore() {
        return bandwidthsStore;
    }

    @Bean
    public WebRateLimiterRegistry rateLimiterRegistry(TestRateLimitingFilter filter) {
        return filter.getRateLimiterRegistry();
    }
}
