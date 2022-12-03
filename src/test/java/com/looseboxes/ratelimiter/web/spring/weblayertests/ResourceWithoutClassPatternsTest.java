package com.looseboxes.ratelimiter.web.spring.weblayertests;

import org.junit.jupiter.api.Test;

@WebMvcControllersTest(classes = { ResourceWithoutClassPatterns.class })
class ResourceWithoutClassPatternsTest extends AbstractResourceTest {

    @Test
    void shouldSucceedWhenWithinLimit() throws Exception {
        shouldReturnDefaultResult(ApiEndpoints.NO_CLASS_PATTERNS_LIMIT_1);
    }

    @Test
    void shouldFailWhenMethodLimitIsExceeded() throws Exception {

        final String endpoint = ApiEndpoints.NO_CLASS_PATTERNS_LIMIT_1;

        shouldReturnDefaultResult(endpoint);

        shouldReturnStatusOfTooManyRequests(endpoint);
    }
}