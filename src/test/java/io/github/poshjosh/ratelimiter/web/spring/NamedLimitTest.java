package io.github.poshjosh.ratelimiter.web.spring;

import io.github.poshjosh.ratelimiter.annotations.Rate;
import io.github.poshjosh.ratelimiter.web.core.WebRateLimiterContext;
import io.github.poshjosh.ratelimiter.web.core.WebRateLimiterRegistries;
import io.github.poshjosh.ratelimiter.web.core.WebRateLimiterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class NamedLimitTest {

    final static String NAME = "rate-limiter-name";

    @Rate(id = NAME, rate = "1/s") // Co-locate with a path related annotation
    @RequestMapping("/named-resource-limiter-test")
    @RestController
    static class Resource{ }

    WebRateLimiterRegistry rateLimiterRegistry;

    @BeforeEach
    void setupRateLimiting() {
        RateLimitPropertiesSpring props = new RateLimitPropertiesSpring();
        props.setResourcePackages(Collections.emptyList());
        props.setResourceClasses(Arrays.asList(Resource.class));
        WebRateLimiterContext context =
                WebRateLimiterContextSpring.builder()
                .properties(props)
                .build();
        rateLimiterRegistry = WebRateLimiterRegistries.of(context);
    }

    @Test
    void shouldHaveAMatcherRegisteredForCustomName() {
        assertTrue(rateLimiterRegistry.hasMatcher(NAME));
    }

    @Test
    void shouldBeRateLimited() {
        assertTrue(rateLimiterRegistry.isRegistered(NAME));
    }
}
