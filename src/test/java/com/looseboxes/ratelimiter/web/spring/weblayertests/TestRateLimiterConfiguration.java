package com.looseboxes.ratelimiter.web.spring.weblayertests;

import com.looseboxes.ratelimiter.DefaultRateLimiterConfig;
import com.looseboxes.ratelimiter.RateLimiterConfig;
import com.looseboxes.ratelimiter.cache.RateCache;
import com.looseboxes.ratelimiter.rates.Logic;
import com.looseboxes.ratelimiter.util.RateConfig;
import com.looseboxes.ratelimiter.util.RateConfigList;
import com.looseboxes.ratelimiter.web.core.WebRequestRateLimiterConfig;
import com.looseboxes.ratelimiter.web.core.WebRequestRateLimiterConfigBuilder;
import com.looseboxes.ratelimiter.web.spring.RateLimitPropertiesSpring;
import com.looseboxes.ratelimiter.web.spring.RateLimiterConfiguration;
import com.looseboxes.ratelimiter.web.spring.SpringRateCache;
import com.looseboxes.ratelimiter.web.spring.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

@TestConfiguration
public class TestRateLimiterConfiguration extends RateLimiterConfiguration{

    private final String testCacheName = this.getClass().getPackage().getName() + ".cache";
    private final ConcurrentMapCacheManager concurrentMapCacheManager = new ConcurrentMapCacheManager();
    private final RateCache rateCache;
    private final PageSupplier keysSupplier;

    public TestRateLimiterConfiguration(RateLimitPropertiesSpring properties) {
        concurrentMapCacheManager.setCacheNames(Collections.singletonList(testCacheName));
        properties.setResourcePackages(Collections.singletonList(ResourceWithMethodLimits.class.getPackage().getName()));
        properties.setRateLimitConfigs(Collections.singletonMap("default", getRateLimitConfigList()));
        RateCacheWithKeysSupplier rateCacheWithKeysSupplier = new RateCacheWithKeysSupplier<>(
                new SpringRateCache<>(concurrentMapCacheManager.getCache(testCacheName))
        );
        this.rateCache = rateCacheWithKeysSupplier;
        this.keysSupplier = rateCacheWithKeysSupplier.getKeysSupplier();
    }

    @Bean
    public RateRepository<Object, LimitWithinDurationDTO<Object>> rateRepository() {
        return new LimitWithinDurationRepository<>(rateCache, keysSupplier);
    }

    @Bean
    @Override
    public WebRequestRateLimiterConfig<HttpServletRequest> webRequestRateLimiterConfig(
            WebRequestRateLimiterConfigBuilder<HttpServletRequest> webRequestRateLimiterConfigBuilder) {
        return webRequestRateLimiterConfigBuilder
                .rateLimiterConfig(new DefaultRateLimiterConfig<>().rateCache(rateCache))
                .build();
    }

    private RateConfigList getRateLimitConfigList() {
        RateConfigList rateConfigList = new RateConfigList();
        rateConfigList.setLimits(getRateLimits());
        rateConfigList.setLogic(Logic.OR);
        return rateConfigList;
    }

    private List<RateConfig> getRateLimits() {
        RateConfig config = new RateConfig();
        config.setLimit(Constants.OVERALL_LIMIT);
        config.setDuration(Duration.ofSeconds(Constants.OVERALL_DURATION_SECONDS));
        return Collections.singletonList(config);
    }
}
