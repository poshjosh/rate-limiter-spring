package com.looseboxes.ratelimiter.web.spring.weblayertests;

import org.junit.jupiter.api.Test;

@WebMvcControllersTest(classes = { ResourceWithoutMethodPatterns.class })
class ResourceWithoutMethodPatternsTest extends AbstractResourceTest {

    @Test
    void shouldSucceedWhenWithinLimit() throws Exception {
        shouldReturnDefaultResult(ApiEndpoints.NO_METHOD_PATTERNS_LIMIT_1);
    }

    @Test
    void shouldFailWhenMethodLimitIsExceeded() throws Exception {

        final String endpoint = ApiEndpoints.NO_METHOD_PATTERNS_LIMIT_1;

        shouldReturnDefaultResult(endpoint);

        shouldReturnStatusOfTooManyRequests(endpoint);
    }
}