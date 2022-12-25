package com.looseboxes.ratelimiter.web.spring.weblayertests;

import com.looseboxes.ratelimiter.annotations.RateLimit;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@WebMvcControllersTest(classes = { ResourceWithoutClassPatternsTest.Resource.class })
class ResourceWithoutClassPatternsTest extends AbstractResourceTest {

    @RestController
    @RequestMapping("")
    static class Resource {

        static final String _LIMIT_1 = "/limit_1";

        interface Endpoints{
            // This does not have the /api prefix
            String LIMIT_1 = _LIMIT_1;
        }

        @RequestMapping(Resource._LIMIT_1)
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