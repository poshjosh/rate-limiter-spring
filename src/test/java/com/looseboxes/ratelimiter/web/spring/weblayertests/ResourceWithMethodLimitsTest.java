package com.looseboxes.ratelimiter.web.spring.weblayertests;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

@WebMvcControllersTest(classes = { ResourceWithMethodLimits.class })
class ResourceWithMethodLimitsTest extends AbstractResourceTest {

    @Test
    void homePageShouldReturnDefaultResult() throws Exception {
        shouldReturnDefaultResult(ApiEndpoints.METHOD_LIMITS_HOME);
    }

    @Test
    void shouldSucceedWhenWithinLimit() throws Exception {
        shouldReturnDefaultResult(ApiEndpoints.METHOD_LIMIT_1);
    }

    @Test
    void shouldFailWhenMethodLimitIsExceeded() throws Exception {

        final String endpoint = ApiEndpoints.METHOD_LIMIT_1;

        shouldReturnDefaultResult(endpoint);

        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    void orLimitGroupShouldFailWhenOneOfManyLimitsIsExceeded() throws Exception {

        final String endpoint = ApiEndpoints.METHOD_LIMIT_1_OR_5;

        shouldReturnDefaultResult(endpoint);

        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    void orLimitGroupShouldFailWhenOneOfManyLimitsIsExceededAfterADelay() throws Exception {

        final String endpoint = ApiEndpoints.METHOD_LIMIT_1_OR_5;

        shouldReturnDefaultResult(endpoint);

        Thread.sleep(TimeUnit.SECONDS.toMillis(Constants.DURATION_SECONDS + 1));

        shouldReturnDefaultResult(endpoint);

        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    void andLimitGroupShouldSucceedWhenOneOfManyLimitsIsExceeded() throws Exception {

        final String endpoint = ApiEndpoints.METHOD_LIMIT_1_AND_5;

        shouldReturnDefaultResult(endpoint);

        shouldReturnDefaultResult(endpoint);
    }

    @Test
    void andLimitGroupShouldFailWhenAllOfManyLimitsIsExceeded() throws Exception {

        shouldFailWhenMaxLimitIsExceeded(ApiEndpoints.METHOD_LIMIT_1_AND_5, Constants.LIMIT_5);
    }
}