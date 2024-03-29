package io.github.poshjosh.ratelimiter.web.spring;

import io.github.poshjosh.ratelimiter.annotations.Rate;
import io.github.poshjosh.ratelimiter.web.core.WebRateLimiterContext;
import io.github.poshjosh.ratelimiter.web.core.WebRateLimiterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.*;

class NamedLimitTest {

    final static String NAME = "rate-limiter-name";

    @Rate(id = NAME) // Co-locate with a path related annotation
    @RequestMapping("/named-resource-limiter-test")
    @RestController
    static class Resource{ }

    WebRateLimiterRegistry registries;

    @BeforeEach
    void setupRateLimiting() {
        RateLimitPropertiesSpring props = new RateLimitPropertiesSpring();
        props.setResourcePackages(Collections.emptyList());
        props.setResourceClasses(Arrays.asList(Resource.class));
        WebRateLimiterContext config =
                WebRateLimiterContextSpring.builder()
                .properties(props)
                .build();
        registries = WebRateLimiterRegistrySpring.of(config);
        registries.createRateLimiterFactory();
    }

    @Test
    void shouldHaveAMatcherRegisteredForCustomName() {
        assertTrue(registries.hasMatcher(NAME));
    }

    @Test
    void shouldBeRateLimited() {
        assertTrue(registries.isRegistered(NAME));
    }
}
