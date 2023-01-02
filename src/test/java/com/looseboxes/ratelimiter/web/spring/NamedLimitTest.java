package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.annotation.ElementId;
import com.looseboxes.ratelimiter.annotations.RateLimit;
import com.looseboxes.ratelimiter.web.core.Registries;
import com.looseboxes.ratelimiter.web.core.WebResourceLimiterConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class NamedLimitTest {

    final static String NAME = "rate-limiter-name";

    @RateLimit(name = NAME) // Co-locate with a path related annotation
    @RequestMapping("/named-resource-limiter-test")
    @RestController
    static class Resource{ }

    Registries<HttpServletRequest> registries;

    @BeforeEach
    void setupRateLimiting() {
        RateLimitPropertiesSpring props = new RateLimitPropertiesSpring();
        props.setResourcePackages(Collections.singletonList(this.getClass().getPackage().getName()));
        WebResourceLimiterConfig<HttpServletRequest> config =
                WebResourceLimiterConfigSpring.builder()
                .properties(props)
                .build();
        registries = ResourceLimiterRegistry.of(config).init();
    }

    @Test
    void shouldHaveAResourceLimiterRegisteredForCustomName() {
        assertNotNull(registries.resourceLimiters().getOrDefault(NAME, null));
    }

    @Test
    void shouldNotHaveAResourceLimiterRegisteredForDefaultName() {
        String defaultName = ElementId.of(Resource.class);
        assertNull(registries.resourceLimiters().getOrDefault(defaultName, null));
    }
}
