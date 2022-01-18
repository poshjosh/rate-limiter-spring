package com.looseboxes.ratelimiter.web.spring.weblayertests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

@WebMvcControllersTest(controllers = { ResourceWithoutMethodPatterns.class })
public class ResourceWithoutMethodPatternsTest extends AbstractResourceTest {

    @Test
    public void shouldSucceedWhenWithinLimit() throws Exception {
        shouldReturnDefaultResult(ApiEndpoints.NO_METHOD_PATTERNS_LIMIT_1);
    }

    @Test
    public void shouldFailWhenMethodLimitIsExceeded() throws Exception {

        final String endpoint = ApiEndpoints.NO_METHOD_PATTERNS_LIMIT_1;

        shouldReturnDefaultResult(endpoint);

        assertThrows(Exception.class, () -> shouldReturnDefaultResult(endpoint));
    }
}