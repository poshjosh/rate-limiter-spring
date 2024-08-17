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

@WebMvcControllersTest(classes = {
        ResourceWithClassLimitTest.Resource.class, ResourceWithClassLimitTest.TestConfig.class })
class ResourceWithClassLimitTest extends AbstractResourceTest{

    @Configuration
    static class TestConfig {
        public TestConfig(RateLimitPropertiesSpring properties) {
            properties.setResourcePackages(Collections.emptyList());
            properties.setResourceClasses(Arrays.asList(ResourceWithClassLimitTest.Resource.class));
        }
    }

    @RestController
    @RequestMapping(ApiEndpoints.API + Resource._BASE)
    @Rate("1/s")
    static class Resource {

        private static final String _BASE = "/resource-with-class-limit-test";
        private static final String _HOME = "/home";

        interface Endpoints {
            String HOME = ApiEndpoints.API + _BASE + _HOME;
        }

        @RequestMapping(Resource._HOME)
        public String home(HttpServletRequest request) {
            return request.getRequestURI();
        }
    }

    @Test
    void shouldFailWhenClassLimitIsExceeded() throws Exception {

        final String endpoint = Resource.Endpoints.HOME;

        //System.out.println();
        shouldReturnDefaultResult(endpoint);

        //System.out.println();
        shouldReturnStatusOfTooManyRequests(endpoint);
    }
}
