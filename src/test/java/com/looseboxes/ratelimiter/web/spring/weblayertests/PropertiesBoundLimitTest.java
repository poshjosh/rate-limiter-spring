package com.looseboxes.ratelimiter.web.spring.weblayertests;

import com.looseboxes.ratelimiter.annotation.IdProvider;
import com.looseboxes.ratelimiter.annotations.RateLimit;
import com.looseboxes.ratelimiter.web.core.WebRequestRateLimiterConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebMvcControllersTest(classes = { PropertiesBoundLimitTest.Resource.class })
public class PropertiesBoundLimitTest extends AbstractResourceTest{

    @RestController
    @RequestMapping(ApiEndpoints.API)
    @RateLimit(limit = 1, duration = 1, timeUnit = TimeUnit.SECONDS)
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
    WebRequestRateLimiterConfig<HttpServletRequest> webRequestRateLimiterConfig;

    @Test
    public void shouldHaveAMatcher() {
        Object matcher = webRequestRateLimiterConfig.getRegistries().matchers().getOrDefault(
                IdProvider.ofClass().getId(Resource.class), null);
        assertNotNull(matcher);
    }

    @Test
    public void shouldBeRateLimited() throws Exception{
        final String endpoint = Resource.Endpoints.HOME;
        final int limit = TestRateLimiterConfiguration.LIMIT;
        for (int i = 0; i < limit; i++) {
            shouldReturnDefaultResult(endpoint);
        }
        shouldReturnStatusOfTooManyRequests(endpoint);
    }
}
