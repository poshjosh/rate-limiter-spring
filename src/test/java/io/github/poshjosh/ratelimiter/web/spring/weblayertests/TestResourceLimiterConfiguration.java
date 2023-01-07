package io.github.poshjosh.ratelimiter.web.spring.weblayertests;

import io.github.poshjosh.ratelimiter.annotation.ElementId;
import io.github.poshjosh.ratelimiter.util.Rate;
import io.github.poshjosh.ratelimiter.util.Rates;
import io.github.poshjosh.ratelimiter.web.core.Registries;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterRegistry;
import io.github.poshjosh.ratelimiter.web.spring.RateLimitPropertiesSpring;
import io.github.poshjosh.ratelimiter.web.spring.SpringRateCache;
import io.github.poshjosh.ratelimiter.web.spring.repository.*;
import io.github.poshjosh.ratelimiter.util.Operator;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterConfigurer;
import io.github.poshjosh.ratelimiter.web.spring.ResourceLimiterConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Collections;

@TestConfiguration
public class TestResourceLimiterConfiguration extends ResourceLimiterConfiguration
        implements ResourceLimiterConfigurer<HttpServletRequest> {

    public static final int LIMIT = 3;

    public static String getMethodNameBoundToPropertyRates() {
        return ElementId.of(methodBoundToPropertyRates());
    }
    private static Method methodBoundToPropertyRates() {
        try {
            return PropertiesBoundLimitTest.Resource.class.getMethod("home", HttpServletRequest.class);
        } catch(NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

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
    public RateRepository<RateEntity<Object>, Object> rateRepository() {
        return new RateRepositoryForCache<>(this.rateCache);
    }

    @Override
    @Bean
    public ResourceLimiterRegistry resourceLimiterRegistry(
            RateLimitPropertiesSpring properties,
            @Autowired(required = false) ResourceLimiterConfigurer<HttpServletRequest> configurer) {
        properties.setResourcePackages(Collections.singletonList(AbstractResourceTest.class.getPackage().getName()));
        properties.setRateLimitConfigs(
                Collections.singletonMap(getMethodNameBoundToPropertyRates(), getRateLimitConfigList()));
        return super.resourceLimiterRegistry(properties, configurer);
    }

    private Rates getRateLimitConfigList() {
        return Rates.of(Operator.OR, getRateLimits());
    }

    private Rate[] getRateLimits() {
        return new Rate[]{Rate.ofSeconds(LIMIT)};
    }
}
