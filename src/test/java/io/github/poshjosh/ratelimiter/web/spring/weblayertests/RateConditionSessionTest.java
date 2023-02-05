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
        RateConditionSessionTest.Resource.class, RateConditionSessionTest.TestConfig.class })
class RateConditionSessionTest extends AbstractResourceTest{

    @Configuration
    static class TestConfig {
        public TestConfig(RateLimitPropertiesSpring properties) {
            properties.setResourcePackages(Collections.emptyList());
            properties.setResourceClasses(Arrays.asList(RateConditionSessionTest.Resource.class));
        }
    }

    private static final String ROOT = "/rate-condition-session-test";

    @RestController
    @RequestMapping(ApiEndpoints.API + ROOT)
    public static class Resource { // Has to be public for tests to succeed

        interface Endpoints{
            String SESSION_ID_EXISTS = ApiEndpoints.API + ROOT + "/session-id-exists";
        }

        @RequestMapping("/session-id-exists")
        @Rate(1)
        @RateCondition(WebExpressionKey.SESSION_ID + "!=0") // TODO - Change this to:  !=null
        public String headerNoMatch(HttpServletRequest request) {
            return request.getRequestURI();
        }
    }

    @Test
    void shouldBeRateLimited_whenSessionIdExists() throws Exception{
        final String endpoint = Resource.Endpoints.SESSION_ID_EXISTS;
        shouldReturnDefaultResult(endpoint);
        shouldReturnStatusOfTooManyRequests(endpoint);
    }
}
