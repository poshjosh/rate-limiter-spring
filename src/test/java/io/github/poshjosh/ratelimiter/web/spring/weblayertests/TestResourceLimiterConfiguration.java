package io.github.poshjosh.ratelimiter.web.spring.weblayertests;

import io.github.poshjosh.ratelimiter.cache.RateCache;
import io.github.poshjosh.ratelimiter.web.core.Registries;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterRegistry;
import io.github.poshjosh.ratelimiter.web.spring.RateLimitPropertiesSpring;
import io.github.poshjosh.ratelimiter.web.spring.SpringRateCache;
import io.github.poshjosh.ratelimiter.web.spring.repository.*;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterConfigurer;
import io.github.poshjosh.ratelimiter.web.spring.ResourceLimiterConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

@TestConfiguration
public class TestResourceLimiterConfiguration extends ResourceLimiterConfiguration
        implements ResourceLimiterConfigurer<HttpServletRequest> {

    private final RateCacheWithKeys<Object> rateCache;

    public TestResourceLimiterConfiguration() {
        String testCacheName = this.getClass().getPackage().getName() + ".cache";
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager(testCacheName);
        this.rateCache = new RateCacheWithKeysImpl<>(
                new SpringRateCache<>(cacheManager.getCache(testCacheName))
        );
    }

    @Override
    public void configure(Registries<HttpServletRequest> registries) {
        registries.caches().register(rateCache);
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
            @Autowired(required = false) ResourceLimiterConfigurer<HttpServletRequest> configurer) {
        // Some test classes initialize resource class/packages as required
        // In which case we do not override
        if (properties.getResourceClasses().isEmpty() && properties.getResourcePackages().isEmpty()) {
            properties.setResourcePackages(
                Collections.singletonList(AbstractResourceTest.class.getPackage().getName()));
        }
        return super.resourceLimiterRegistry(properties, configurer);
    }
}
