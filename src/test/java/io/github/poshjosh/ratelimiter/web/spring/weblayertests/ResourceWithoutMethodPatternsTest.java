package io.github.poshjosh.ratelimiter.web.spring.weblayertests;

import io.github.poshjosh.ratelimiter.annotation.Rate;
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
import java.util.concurrent.TimeUnit;

@WebMvcControllersTest(classes = {
        ResourceWithoutMethodPatternsTest.Resource.class, ResourceWithoutMethodPatternsTest.TestConfig.class })
class ResourceWithoutMethodPatternsTest extends AbstractResourceTest {
    @Configuration
    static class TestConfig {
        public TestConfig(RateLimitPropertiesSpring properties) {
            properties.setResourcePackages(Collections.emptyList());
            properties.setResourceClasses(Arrays.asList(ResourceWithoutMethodPatternsTest.Resource.class));
        }
    }

    @RestController
    @RequestMapping(ApiEndpoints.API)
    static class Resource {

        static final String _INTERNAL_LIMIT_1 = "/resource-without-method-patterns-test/limit-1";

        interface Endpoints{
            // No method patterns
            String LIMIT_1 = ApiEndpoints.API + _INTERNAL_LIMIT_1;
        }

        @RequestMapping(Resource._INTERNAL_LIMIT_1)
        @Rate(permits = 1, duration = 3)
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