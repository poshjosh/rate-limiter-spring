package io.github.poshjosh.ratelimiter.web.spring;

import io.github.poshjosh.ratelimiter.annotations.Rate;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterConfig;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
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
        props.setResourcePackages(Collections.emptyList());
        props.setResourceClasses(Arrays.asList(Resource.class));
        ResourceLimiterConfig<HttpServletRequest> config =
                ResourceLimiterConfigSpring.builder()
                .properties(props)
                .build();
        registries = ResourceLimiterRegistrySpring.of(config);
        registries.createResourceLimiter();
    }

    @Test
    void shouldHaveAMatcherRegisteredForCustomName() {
        assertTrue(registries.hasMatching(NAME));
    }

    @Test
    void shouldBeRateLimited() {
        assertTrue(registries.isRateLimited(NAME));
    }
}
