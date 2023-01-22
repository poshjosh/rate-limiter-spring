package io.github.poshjosh.ratelimiter.web.spring.weblayertests;

import io.github.poshjosh.ratelimiter.annotation.ElementId;
import io.github.poshjosh.ratelimiter.Operator;
import io.github.poshjosh.ratelimiter.util.Rate;
import io.github.poshjosh.ratelimiter.util.Rates;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterRegistry;
import io.github.poshjosh.ratelimiter.web.spring.RateLimitPropertiesSpring;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebMvcControllersTest(classes = {
        PropertiesBoundLimitTest.Resource.class, PropertiesBoundLimitTest.TestConfig.class })
class PropertiesBoundLimitTest extends AbstractResourceTest{

    private static final int LIMIT = 1;

    @Configuration
    static class TestConfig {
        public TestConfig(RateLimitPropertiesSpring properties) {
            properties.setResourcePackages(Collections.emptyList());
            properties.setResourceClasses(Arrays.asList(PropertiesBoundLimitTest.Resource.class));
            properties.setRateLimitConfigs(
                    Collections.singletonMap(Resource.getMethodLimitedViaProperties(), getRateLimitConfigList()));
        }
        private Rates getRateLimitConfigList() {
            return Rates.of(Operator.OR, getRateLimits());
        }
        private Rate[] getRateLimits() {
            return new Rate[]{Rate.ofSeconds(LIMIT)};
        }
    }

    @RestController
    @RequestMapping(ApiEndpoints.API + Resource._BASE)
    static class Resource {

        private static final String _BASE = "/properties-bound-limit-test";
        private static final String _HOME = "/home";

        interface Endpoints {
            String HOME = ApiEndpoints.API + _BASE + _HOME;
        }

        @RequestMapping(_HOME)
        public String home(HttpServletRequest request) {
            return request.getRequestURI();
        }
        private static String getMethodLimitedViaProperties() {
            try {
                return ElementId.of(Resource.class.getMethod("home", HttpServletRequest.class));
            } catch(NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Autowired ResourceLimiterRegistry resourceLimiterRegistry;

    @Test
    void shouldHaveAMatcher() {
        Object matcher = resourceLimiterRegistry.matchers()
                .getOrDefault(Resource.getMethodLimitedViaProperties(), null);
        assertNotNull(matcher);
    }

    @Test
    void shouldBeRateLimited() throws Exception{
        final String endpoint = Resource.Endpoints.HOME;
        for (int i = 0; i < LIMIT; i++) {
            shouldReturnDefaultResult(endpoint);
        }
        shouldReturnStatusOfTooManyRequests(endpoint);
    }
}
