package io.github.poshjosh.ratelimiter.web.spring.weblayertests;

import io.github.poshjosh.ratelimiter.UsageListener;
import io.github.poshjosh.ratelimiter.util.LimiterConfig;
import io.github.poshjosh.ratelimiter.web.core.Registries;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterRegistry;
import io.github.poshjosh.ratelimiter.web.spring.repository.RateCache;
import io.github.poshjosh.ratelimiter.web.spring.RateLimitPropertiesSpring;
import io.github.poshjosh.ratelimiter.web.spring.repository.RateCacheSpring;
import io.github.poshjosh.ratelimiter.web.spring.repository.*;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterConfigurer;
import io.github.poshjosh.ratelimiter.web.spring.ResourceLimiterConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;

import java.util.Collections;

@TestConfiguration
public class TestResourceLimiterConfiguration extends ResourceLimiterConfiguration
        implements ResourceLimiterConfigurer {

    private final RateCache<Object> rateCache;

    public TestResourceLimiterConfiguration() {
        String testCacheName = this.getClass().getPackage().getName() + ".cache";
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager(testCacheName);
        this.rateCache = new RateCacheSpring<>(cacheManager.getCache(testCacheName));
    }

    @Override
    public void configure(Registries registries) {
        registries.registerStore(rateCache);
        registries.addListener(new UsageListener() {
            @Override public void onConsumed(Object request, String resourceId, int permits,
                    LimiterConfig<?> config) {
                //System.out.println("TestResourceLimiterConfiguration#onConsumed" + resourceId + ", " + config.getRates());
            }
            @Override public void onRejected(Object request, String resourceId, int permits,
                    LimiterConfig<?> config) {
                //System.out.println("TestResourceLimiterConfiguration#onRejected" + resourceId + ", " + config.getRates());
            }
        });
    }

    @Bean
    public RateCache<Object> rateCache() {
        return rateCache;
    }

    @Bean
    public RateRepository<RateEntity<Object>, Object> rateRepository() {
        return new RateRepositoryForCache<>(this.rateCache);
    }

    @Override
    @Bean
    public ResourceLimiterRegistry resourceLimiterRegistry(
            RateLimitPropertiesSpring properties,
            @Autowired(required = false) ResourceLimiterConfigurer configurer) {
        // Some test classes initialize resource class/packages as required
        // In which case we do not override
        if (properties.getResourceClasses().isEmpty() && properties.getResourcePackages().isEmpty()) {
            properties.setResourcePackages(
                Collections.singletonList(AbstractResourceTest.class.getPackage().getName()));
        }
        return super.resourceLimiterRegistry(properties, configurer);
    }
}
