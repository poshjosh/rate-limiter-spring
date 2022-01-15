package com.looseboxes.ratelimiter.web.spring.weblayertests;

import com.looseboxes.ratelimiter.RateLimiterFactory;
import com.looseboxes.ratelimiter.cache.RateCache;
import com.looseboxes.ratelimiter.rates.Logic;
import com.looseboxes.ratelimiter.util.RateConfig;
import com.looseboxes.ratelimiter.util.RateConfigList;
import com.looseboxes.ratelimiter.web.core.MatcherRegistry;
import com.looseboxes.ratelimiter.web.core.RateLimiterConfigurationSource;
import com.looseboxes.ratelimiter.web.core.RateLimiterConfigurer;
import com.looseboxes.ratelimiter.web.spring.RateLimitPropertiesSpring;
import com.looseboxes.ratelimiter.web.spring.RateLimiterConfiguration;
import com.looseboxes.ratelimiter.web.spring.SpringRateCache;
import com.looseboxes.ratelimiter.web.spring.repository.LimitWithinDurationDTO;
import com.looseboxes.ratelimiter.web.spring.repository.LimitWithinDurationRepository;
import com.looseboxes.ratelimiter.web.spring.repository.RateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

@SpringBootApplication(scanBasePackageClasses = {
        RateLimiterConfiguration.class, TestWebMvcConfigurer.class,
        ResourceWithClassLimits.class, ResourceWithMethodLimits.class,
})
@EnableConfigurationProperties({ RateLimitPropertiesSpring.class })
public class WebLayerTestConfiguration extends RateLimiterConfiguration{

    private final String testCacheName = this.getClass().getPackage().getName() + ".cache";
    private final ConcurrentMapCacheManager concurrentMapCacheManager = new ConcurrentMapCacheManager();
    private final RateCache rateCache;

    public WebLayerTestConfiguration(RateLimitPropertiesSpring rateLimitProperties) {
        concurrentMapCacheManager.setCacheNames(Collections.singletonList(testCacheName));
        rateLimitProperties.setResourcePackages(Collections.singletonList(ResourceWithMethodLimits.class.getPackage().getName()));
        rateLimitProperties.setRateLimitConfigs(Collections.singletonMap("default", getRateLimitConfigList()));
        rateCache = new SpringRateCache<>(concurrentMapCacheManager.getCache(testCacheName));
    }

    @Bean
    public RateRepository<Object, LimitWithinDurationDTO<Object>> rateRepository() {
        return new LimitWithinDurationRepository<>(rateCache);
    }

    @Bean
    @Override
    public RateLimiterConfigurationSource<HttpServletRequest> rateLimiterConfigurationSource(
            MatcherRegistry<HttpServletRequest> matcherRegistry,
            RateLimiterFactory<Object> rateLimiterFactory,
            @Autowired(required = false) RateLimiterConfigurer<HttpServletRequest> rateLimiterConfigurer) {
        return super.rateLimiterConfigurationSource(matcherRegistry, rateLimiterFactory, rateLimiterConfigurer)
                .registerRateCache(rateCache);
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
