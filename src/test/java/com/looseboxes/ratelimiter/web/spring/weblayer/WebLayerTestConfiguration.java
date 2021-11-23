package com.looseboxes.ratelimiter.web.spring.weblayer;

import com.looseboxes.ratelimiter.rates.Rates;
import com.looseboxes.ratelimiter.web.core.util.RateLimitConfig;
import com.looseboxes.ratelimiter.web.core.util.RateLimitConfigList;
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

    private RateLimitConfigList getRateLimitConfigList() {
        RateLimitConfigList rateLimitConfigList = new RateLimitConfigList();
        rateLimitConfigList.setLimits(getRateLimits());
        rateLimitConfigList.setLogic(Rates.Logic.OR);
        return rateLimitConfigList;
    }

    private List<RateLimitConfig> getRateLimits() {
        RateLimitConfig config = new RateLimitConfig();
        config.setDuration(1);
        config.setLimit(200);
        config.setTimeUnit(TimeUnit.MINUTES);
        return Collections.singletonList(config);
    }
}
