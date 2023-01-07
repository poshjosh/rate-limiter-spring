package io.github.poshjosh.ratelimiter.web.spring.weblayertests;

import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebMvcControllersTest(classes = { PropertiesBoundLimitTest.Resource.class })
public class PropertiesBoundLimitTest extends AbstractResourceTest{

    @RestController
    @RequestMapping(ApiEndpoints.API)
    static class Resource {

        private static final String _HOME = "/propeties-bound-limit-test/home";

        interface Endpoints {
            String HOME = ApiEndpoints.API + Resource._HOME;
        }

        @RequestMapping(Resource._HOME)
        public String home(HttpServletRequest request) {
            return request.getRequestURI();
        }
    }

    @Autowired ResourceLimiterRegistry resourceLimiterRegistry;

    @Test
    public void shouldHaveAMatcher() {
        Object matcher = resourceLimiterRegistry.matchers().getOrDefault(
                TestResourceLimiterConfiguration.getMethodNameBoundToPropertyRates(), null);
        assertNotNull(matcher);
    }

    @Test
    public void shouldBeRateLimited() throws Exception{
        final String endpoint = Resource.Endpoints.HOME;
        final int limit = TestResourceLimiterConfiguration.LIMIT;
        for (int i = 0; i < limit; i++) {
            shouldReturnDefaultResult(endpoint);
        }
        shouldReturnStatusOfTooManyRequests(endpoint);
    }
}
