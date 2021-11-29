package com.looseboxes.ratelimiter.web.spring.weblayertests;

import com.looseboxes.ratelimiter.annotation.AnnotationProcessor;
import com.looseboxes.ratelimiter.annotation.ClassAnnotationProcessor;
import com.looseboxes.ratelimiter.annotation.MethodAnnotationProcessor;
import com.looseboxes.ratelimiter.rates.Logic;
import com.looseboxes.ratelimiter.util.RateConfig;
import com.looseboxes.ratelimiter.util.RateLimitConfig;
import com.looseboxes.ratelimiter.web.spring.RateLimitPropertiesImpl;
import com.looseboxes.ratelimiter.web.spring.RateLimiterConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootApplication(scanBasePackageClasses = {
        ResourceWithClassLimits.class,
        ResourceWithMethodLimits.class,
        RateLimiterConfiguration.class
})
@EnableConfigurationProperties({ RateLimitPropertiesImpl.class })
public class WebLayerTestConfiguration extends RateLimiterConfiguration{

    public WebLayerTestConfiguration(RateLimitPropertiesImpl rateLimitProperties) {
        rateLimitProperties.setResourcePackages(Collections.singletonList(ResourceWithMethodLimits.class.getPackage().getName()));
        rateLimitProperties.setRateLimitConfigs(Collections.singletonMap("default", getRateLimitConfigList()));
    }

    private RateLimitConfig getRateLimitConfigList() {
        RateLimitConfig rateLimitConfig = new RateLimitConfig();
        rateLimitConfig.setLimits(getRateLimits());
        rateLimitConfig.setLogic(Logic.OR);
        return rateLimitConfig;
    }

    private List<RateConfig> getRateLimits() {
        RateConfig config = new RateConfig();
        config.setLimit(Constants.OVERALL_LIMIT);
        config.setDuration(Constants.OVERALL_DURATION_SECONDS);
        config.setTimeUnit(TimeUnit.SECONDS);
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
