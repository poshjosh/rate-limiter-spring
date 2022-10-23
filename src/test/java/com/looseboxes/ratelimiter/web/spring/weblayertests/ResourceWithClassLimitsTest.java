package com.looseboxes.ratelimiter.web.spring.weblayertests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

@WebMvcControllersTest(classes = { ResourceWithClassLimits.class })
public class ResourceWithClassLimitsTest extends AbstractResourceTest {

    @Test
    public void shouldFailWhenClassLimitIsExceeded() throws Exception {

        final String endpoint = ApiEndpoints.CLASS_LIMITS_HOME;

        shouldReturnDefaultResult(endpoint);

        assertThrows(Exception.class, () -> shouldReturnDefaultResult(endpoint));
    }
}