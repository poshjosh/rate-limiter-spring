package com.looseboxes.ratelimiter.web.spring.weblayertests;

import com.looseboxes.ratelimiter.annotations.RateLimit;
import com.looseboxes.ratelimiter.web.spring.RateLimitPropertiesSpring;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WebMvcControllersTest(classes = { DisabledRateLimitingTest.Resource.class })
class DisabledRateLimitingTest extends AbstractResourceTest{

    @Autowired
    RateLimitPropertiesSpring rateLimitProperties;

    @RestController
    @RequestMapping(ApiEndpoints.API)
    static class Resource {

        private static final String _HOME = "/diabled-rate-limiting-test/home";

        interface Endpoints {
            String HOME = ApiEndpoints.API + Resource._HOME;
        }

        @RequestMapping(DisabledRateLimitingTest.Resource._HOME)
        @RateLimit(limit = 1, duration = 1, timeUnit = TimeUnit.SECONDS)
        public String home(HttpServletRequest request) {
            return request.getRequestURI();
        }
    }

    private Boolean originallyDisabled;

    @BeforeEach
    void init() {
        originallyDisabled = getProperties().getDisabled();
        assertFalse(originallyDisabled);
        getProperties().setDisabled(Boolean.TRUE);
    }

    @Test
    public void shouldSucceedWhenDisabled() throws Exception{
        assertFalse(originallyDisabled);
        assertTrue(getProperties().getDisabled());
        try {
            final String endpoint = Resource.Endpoints.HOME;
            shouldReturnDefaultResult(endpoint); // 1 of 1
            shouldReturnDefaultResult(endpoint); // 2 of 1 - Should succeed if rate limiting is disabled
        }finally{
            getProperties().setDisabled(originallyDisabled);

        }
    }

    private RateLimitPropertiesSpring getProperties() {
        return rateLimitProperties;
    }
}
