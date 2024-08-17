package io.github.poshjosh.ratelimiter.web.spring.weblayertests;

import io.github.poshjosh.ratelimiter.annotations.Rate;
import io.github.poshjosh.ratelimiter.web.spring.RateLimitPropertiesSpring;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WebMvcControllersTest(classes = {
        DisabledRateLimitingTest.Resource.class, DisabledRateLimitingTest.TestConfig.class })
class DisabledRateLimitingTest extends AbstractResourceTest{

    @Configuration
    static class TestConfig {
        public TestConfig(RateLimitPropertiesSpring properties) {
            properties.setResourceClasses(Arrays.asList(DisabledRateLimitingTest.Resource.class));
            properties.setDisabled(Boolean.TRUE);
        }
    }

    @RestController
    @RequestMapping(ApiEndpoints.API)
    static class Resource {

        private static final String _HOME = "/disabled-rate-limiting-test/home";

        interface Endpoints {
            String HOME = ApiEndpoints.API + Resource._HOME;
        }

        @RequestMapping(DisabledRateLimitingTest.Resource._HOME)
        @Rate("1/s")
        public String home(HttpServletRequest request) {
            return request.getRequestURI();
        }
    }

    @Autowired
    private RateLimitPropertiesSpring properties;

    @Test
    void shouldSucceedWhenDisabled() throws Exception{
        assertTrue(properties.getDisabled());
        final String endpoint = Resource.Endpoints.HOME;
        shouldReturnDefaultResult(endpoint); // 1 of 1
        shouldReturnDefaultResult(endpoint); // 2 of 1 - Should succeed if rate limiting is disabled
    }
}
