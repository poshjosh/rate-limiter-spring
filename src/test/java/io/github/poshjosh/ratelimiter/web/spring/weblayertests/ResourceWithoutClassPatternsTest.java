package io.github.poshjosh.ratelimiter.web.spring.weblayertests;

import io.github.poshjosh.ratelimiter.annotations.Rate;
import io.github.poshjosh.ratelimiter.web.spring.RateLimitPropertiesSpring;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@WebMvcControllersTest(classes = {
        ResourceWithoutClassPatternsTest.Resource.class, ResourceWithoutClassPatternsTest.TestConfig.class })
class ResourceWithoutClassPatternsTest extends AbstractResourceTest {

    @Configuration
    static class TestConfig {
        public TestConfig(RateLimitPropertiesSpring properties) {
            properties.setResourcePackages(Collections.emptyList());
            properties.setResourceClasses(Arrays.asList(ResourceWithoutClassPatternsTest.Resource.class));
        }
    }

    @RestController
    @RequestMapping("")
    static class Resource {

        static final String _LIMIT_1 = "/limit_1";

        interface Endpoints{
            // This does not have the /api prefix
            String LIMIT_1 = _LIMIT_1;
        }

        @RequestMapping(Resource._LIMIT_1)
        @Rate(permits = 1, duration = 3, timeUnit = TimeUnit.SECONDS)
        public String limit_1(HttpServletRequest request) {
            return request.getRequestURI();
        }
    }

    @Test
    void shouldSucceedWhenWithinLimit() throws Exception {
        shouldReturnDefaultResult(Resource.Endpoints.LIMIT_1);
    }

    @Test
    void shouldFailWhenMethodLimitIsExceeded() throws Exception {

        final String endpoint = Resource.Endpoints.LIMIT_1;

        shouldReturnDefaultResult(endpoint);

        shouldReturnStatusOfTooManyRequests(endpoint);
    }
}