package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.annotations.Rate;
import com.looseboxes.ratelimiter.web.core.ResourceLimiterConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class NamedLimitTest {

    final static String NAME = "rate-limiter-name";

    @Rate(name = NAME) // Co-locate with a path related annotation
    @RequestMapping("/named-resource-limiter-test")
    @RestController
    static class Resource{ }

    ResourceLimiterRegistry registries;

    @BeforeEach
    void setupRateLimiting() {
        RateLimitPropertiesSpring props = new RateLimitPropertiesSpring();
        props.setResourcePackages(Collections.singletonList(this.getClass().getPackage().getName()));
        ResourceLimiterConfig<HttpServletRequest> config =
                ResourceLimiterConfigSpring.builder()
                .properties(props)
                .build();
        registries = ResourceLimiterRegistry.of(config);
    }

    @Test
    void shouldHaveAResourceLimiterRegisteredForCustomName() {
        assertNotNull(registries.limiters().getOrDefault(NAME, null));
    }

    @Test
    void shouldBeRateLimited() {
        assertTrue(registries.isRateLimited(NAME));
    }
}
