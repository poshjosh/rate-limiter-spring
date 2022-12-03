package com.looseboxes.ratelimiter.web.spring.weblayertests;

import org.junit.jupiter.api.Test;

@WebMvcControllersTest(classes = { ResourceWithClassLimits.class })
class ResourceWithClassLimitsTest extends AbstractResourceTest {

    @Test
    void shouldFailWhenClassLimitIsExceeded() throws Exception {

        final String endpoint = ApiEndpoints.CLASS_LIMITS_HOME;

        shouldReturnDefaultResult(endpoint);

        shouldReturnStatusOfTooManyRequests(endpoint);
    }
}