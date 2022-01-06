package com.looseboxes.ratelimiter.web.spring.weblayertests;

import com.looseboxes.ratelimiter.annotation.AnnotationProcessor;
import com.looseboxes.ratelimiter.annotation.ClassAnnotationProcessor;
import com.looseboxes.ratelimiter.annotation.MethodAnnotationProcessor;
import com.looseboxes.ratelimiter.cache.RateCache;
import com.looseboxes.ratelimiter.rates.Logic;
import com.looseboxes.ratelimiter.util.RateConfig;
import com.looseboxes.ratelimiter.util.RateConfigList;
import com.looseboxes.ratelimiter.web.spring.RateLimitPropertiesSpring;
import com.looseboxes.ratelimiter.web.spring.RateLimiterConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

@SpringBootApplication(scanBasePackageClasses = {
        RateLimiterConfiguration.class, TestWebMvcConfigurer.class,
        ResourceWithClassLimits.class, ResourceWithMethodLimits.class,
})
@EnableConfigurationProperties({ RateLimitPropertiesSpring.class })
public class WebLayerTestConfiguration extends RateLimiterConfiguration{

    private final ConcurrentMapCacheManager concurrentMapCacheManager = new ConcurrentMapCacheManager();

    public WebLayerTestConfiguration(RateLimitPropertiesSpring rateLimitProperties) {
        concurrentMapCacheManager.setCacheNames(Collections.singletonList(DEFAULT_CACHE_NAME));
        rateLimitProperties.setResourcePackages(Collections.singletonList(ResourceWithMethodLimits.class.getPackage().getName()));
        rateLimitProperties.setRateLimitConfigs(Collections.singletonMap("default", getRateLimitConfigList()));
    }

    @Override
    @Bean
    public RateCache<Object, Object> rateCache(CacheManager cacheManager) {
        if(cacheManager == null) {
            cacheManager = concurrentMapCacheManager;
        }
        return super.rateCache(cacheManager);
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

    // For tests we override this to simplify logging
    // This means that during tests we can't have 2 resource classes with the same name, as we use Class.getSimpleName
    // Is this wise
    @Bean
    public AnnotationProcessor<Class<?>> annotationProcessor() {
        return new ClassAnnotationProcessor(Class::getSimpleName, new MethodAnnotationProcessor(Method::getName));
    }
}
