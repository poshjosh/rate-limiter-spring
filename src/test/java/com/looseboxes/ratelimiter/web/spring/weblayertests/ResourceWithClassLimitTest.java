package com.looseboxes.ratelimiter.web.spring.weblayertests;

import com.looseboxes.ratelimiter.annotations.RateLimit;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@WebMvcControllersTest(classes = { ResourceWithClassLimitTest.Resource.class })
public class ResourceWithClassLimitTest extends AbstractResourceTest{

    @RestController
    @RequestMapping(ApiEndpoints.API + Resource.ROOT)
    @RateLimit(limit = 1, duration = 1, timeUnit = TimeUnit.SECONDS)
    static class Resource {

        private static final String ROOT = "/resource-with-class-limit-test";

        private static final String _HOME = "/home";

        interface Endpoints {
            String HOME = ApiEndpoints.API + ROOT + _HOME;
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
