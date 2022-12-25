package com.looseboxes.ratelimiter.web.spring.weblayertests;

import com.looseboxes.ratelimiter.annotations.RateLimit;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@WebMvcControllersTest(classes = { ResourceWithoutMethodPatternsTest.Resource.class })
class ResourceWithoutMethodPatternsTest extends AbstractResourceTest {

    @RestController
    @RequestMapping(ApiEndpoints.API)
    static class Resource {

        static final String _INTERNAL_LIMIT_1 = "/resource-without-method-patterns-test/limit-1";

        interface Endpoints{
            // No method patterns
            String LIMIT_1 = ApiEndpoints.API + _INTERNAL_LIMIT_1;
        }

        @RequestMapping(Resource._INTERNAL_LIMIT_1)
        @RateLimit(limit = 1, duration = 3, timeUnit = TimeUnit.SECONDS)
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