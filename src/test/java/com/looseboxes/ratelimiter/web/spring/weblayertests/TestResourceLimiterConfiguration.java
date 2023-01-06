package com.looseboxes.ratelimiter.web.spring.weblayertests;

import com.looseboxes.ratelimiter.annotation.ElementId;
import com.looseboxes.ratelimiter.util.Operator;
import com.looseboxes.ratelimiter.util.Rate;
import com.looseboxes.ratelimiter.util.Rates;
import com.looseboxes.ratelimiter.web.core.Registries;
import com.looseboxes.ratelimiter.web.core.ResourceLimiterConfigurer;
import com.looseboxes.ratelimiter.web.spring.RateLimitPropertiesSpring;
import com.looseboxes.ratelimiter.web.spring.ResourceLimiterConfiguration;
import com.looseboxes.ratelimiter.web.spring.ResourceLimiterRegistry;
import com.looseboxes.ratelimiter.web.spring.SpringRateCache;
import com.looseboxes.ratelimiter.web.spring.repository.*;
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
