package com.looseboxes.ratelimiter.web.spring.weblayertests;

import com.looseboxes.ratelimiter.annotation.ElementId;
import com.looseboxes.ratelimiter.util.Operator;
import com.looseboxes.ratelimiter.util.Rate;
import com.looseboxes.ratelimiter.util.Rates;
import com.looseboxes.ratelimiter.web.core.Registries;
import com.looseboxes.ratelimiter.web.core.ResourceLimiterConfigurer;
import com.looseboxes.ratelimiter.web.core.WebResourceLimiterConfig;
import com.looseboxes.ratelimiter.web.spring.RateLimitPropertiesSpring;
import com.looseboxes.ratelimiter.web.spring.ResourceLimiterConfiguration;
import com.looseboxes.ratelimiter.web.spring.SpringRateCache;
import com.looseboxes.ratelimiter.web.spring.repository.*;
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

    private final String testCacheName = this.getClass().getPackage().getName() + ".cache";
    private final ConcurrentMapCacheManager concurrentMapCacheManager = new ConcurrentMapCacheManager();
    private final RateCacheWithKeys rateCache;

    private static String methodNameBoundToPropertyRates;

    public static String getMethodNameBoundToPropertyRates() {
        return methodNameBoundToPropertyRates;
    }

    public TestResourceLimiterConfiguration(RateLimitPropertiesSpring properties) {
        concurrentMapCacheManager.setCacheNames(Collections.singletonList(testCacheName));
        properties.setResourcePackages(Collections.singletonList(AbstractResourceTest.class.getPackage().getName()));
        this.rateCache = new RateCacheWithKeysImpl<>(
                new SpringRateCache<>(concurrentMapCacheManager.getCache(testCacheName))
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

    @Bean
    @Override
    public WebResourceLimiterConfig<HttpServletRequest> webRequestRateLimiterConfig(
            WebResourceLimiterConfig.Builder<HttpServletRequest> webRequestRateLimiterConfigBuilder) {
        WebResourceLimiterConfig<HttpServletRequest> config = webRequestRateLimiterConfigBuilder
            // TODO - Find a way to support caches,listeners etc set via this factory
            //.resourceLimiterFactory(rates -> ResourceLimiter.of(rates).cache(rateCache))
            .build();
        methodNameBoundToPropertyRates = initMethodBoundToPropertyRates(config);
        ((RateLimitPropertiesSpring)config.getProperties()).setRateLimitConfigs(
                Collections.singletonMap(methodNameBoundToPropertyRates, getRateLimitConfigList()));
        return config;
    }

    private String initMethodBoundToPropertyRates(
            WebResourceLimiterConfig<HttpServletRequest> config) {
        return ElementId.of(methodBoundToPropertyRates());
    }
    private Method methodBoundToPropertyRates() {
        try {
            return PropertiesBoundLimitTest.Resource.class.getMethod("home", HttpServletRequest.class);
        } catch(NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private Rates getRateLimitConfigList() {
        return Rates.of(Operator.OR, getRateLimits());
    }

    private Rate[] getRateLimits() {
        return new Rate[]{Rate.ofSeconds(LIMIT)};
    }
}
