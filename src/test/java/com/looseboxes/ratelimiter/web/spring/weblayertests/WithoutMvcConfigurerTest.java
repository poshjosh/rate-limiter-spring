package com.looseboxes.ratelimiter.web.spring.weblayertests;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@WebMvcControllersTest(
        contextConfiguration = { WebLayerTestConfiguration.class, WithoutMvcConfigurerTest.NoOpWebMvcConfigurer.class },
        controllers = { ResourceWithoutClassPatterns.class }
)
public class WithoutMvcConfigurerTest extends AbstractResourceTest {

    @Configuration
    static class NoOpWebMvcConfigurer implements WebMvcConfigurer{ }

    @Test
    public void shouldSucceedWhenMethodLimitIsExceeded() throws Exception {

        final String endpoint = ApiEndpoints.NO_CLASS_PATTERNS_LIMIT_1;

        shouldReturnDefaultResult(endpoint);

        // Should not fail because rate limiting is not configured via TestWebMvcConfigurer
        shouldReturnDefaultResult(endpoint);
    }
}