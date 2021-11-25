package com.looseboxes.ratelimiter.web.spring.weblayertests;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

@WebMvcTest(ResourceWithClassLimits.class)
public class ResourceWithClassLimitsTest extends AbstractResourceTest {

    @Test
    public void shouldFailWhenClassLimitIsExceeded() throws Exception {

        final String endpoint = ApiEndpoints.CLASS_LIMITS;

        shouldReturnDefaultResult(endpoint);

        assertThrows(Exception.class, () -> shouldReturnDefaultResult(endpoint));
    }
}