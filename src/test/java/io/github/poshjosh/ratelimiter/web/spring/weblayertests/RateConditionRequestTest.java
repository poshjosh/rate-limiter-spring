package io.github.poshjosh.ratelimiter.web.spring.weblayertests;

import io.github.poshjosh.ratelimiter.annotations.Rate;
import io.github.poshjosh.ratelimiter.annotations.RateCondition;
import io.github.poshjosh.ratelimiter.web.core.WebExpressionKey;
import io.github.poshjosh.ratelimiter.web.spring.RateLimitPropertiesSpring;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;

@WebMvcControllersTest(classes = {
        RateConditionRequestTest.Resource.class, RateConditionRequestTest.TestConfig.class })
class RateConditionRequestTest extends AbstractResourceTest{

    @Configuration
    static class TestConfig {
        public TestConfig(RateLimitPropertiesSpring properties) {
            properties.setResourcePackages(Collections.emptyList());
            properties.setResourceClasses(Arrays.asList(RateConditionRequestTest.Resource.class));
        }
    }

    private static final String ROOT = "/rate-condition-request-test";

    @RestController
    @RequestMapping(ApiEndpoints.API + ROOT)
    public static class Resource { // Has to be public for tests to succeed

        interface Endpoints{
            String REQUEST_URI_EXISTS = ApiEndpoints.API + ROOT + "/request-uri-exists";
        }

        @RequestMapping("/request-uri-exists")
        @Rate("1/s")
        @RateCondition(WebExpressionKey.REQUEST_URI + " !=")
        public String requestUriExists(HttpServletRequest request) {
            return request.getRequestURI();
        }
    }

    @Test
    void shouldBeRateLimitedWhenRequestUriExists() throws Exception{
        final String endpoint = Resource.Endpoints.REQUEST_URI_EXISTS;
        shouldReturnDefaultResult(endpoint);
        shouldReturnStatusOfTooManyRequests(endpoint);
    }
}
