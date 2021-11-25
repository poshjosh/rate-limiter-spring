package com.looseboxes.ratelimiter.web.spring.weblayertests;

import com.looseboxes.ratelimiter.rates.Rates;
import com.looseboxes.ratelimiter.util.RateConfig;
import com.looseboxes.ratelimiter.util.RateLimitConfig;
import com.looseboxes.ratelimiter.web.spring.RateLimitPropertiesImpl;
import com.looseboxes.ratelimiter.web.spring.RateLimiterConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

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
        rateLimitConfig.setLogic(Rates.Logic.OR);
        return rateLimitConfig;
    }

    private List<RateConfig> getRateLimits() {
        RateConfig config = new RateConfig();
        config.setDuration(1);
        config.setLimit(200);
        config.setTimeUnit(TimeUnit.MINUTES);
        return Collections.singletonList(config);
    }
}
