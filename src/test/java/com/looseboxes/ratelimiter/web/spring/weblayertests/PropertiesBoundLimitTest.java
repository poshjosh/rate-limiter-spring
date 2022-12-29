package com.looseboxes.ratelimiter.web.spring.weblayertests;

import com.looseboxes.ratelimiter.web.core.WebResourceLimiterConfig;
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

    @Autowired
    WebResourceLimiterConfig<HttpServletRequest> webResourceLimiterConfig;

    @Test
    public void shouldHaveAMatcher() {
        Object matcher = webResourceLimiterConfig.getRegistries().matchers().getOrDefault(
                TestResourceLimiterConfiguration.getMethodBoundToPropertyRates(), null);
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
