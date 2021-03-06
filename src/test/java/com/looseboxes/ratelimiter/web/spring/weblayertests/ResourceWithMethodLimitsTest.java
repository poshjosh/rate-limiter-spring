package com.looseboxes.ratelimiter.web.spring.weblayertests;

import com.looseboxes.ratelimiter.web.spring.RateLimitPropertiesSpring;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;

@WebMvcControllersTest(controllers = { ResourceWithMethodLimits.class })
public class ResourceWithMethodLimitsTest extends AbstractResourceTest {

    @Autowired
    private RateLimitPropertiesSpring properties;

    @Test
    public void shouldSucceedWhenDisabled() throws Exception{
        Boolean disabled = properties.getDisabled();
        try {
            properties.setDisabled(Boolean.TRUE);
            final String endpoint = ApiEndpoints.METHOD_LIMIT_1;
            shouldReturnDefaultResult(endpoint); // 1 of 1
            shouldReturnDefaultResult(endpoint); // 2 of 1 - Should throw exception if rate limiting is disabled
        }finally{
            properties.setDisabled(disabled);
        }
    }

    @Test
    public void homePageShouldReturnDefaultResult() throws Exception {
        shouldReturnDefaultResult(ApiEndpoints.METHOD_LIMITS_HOME);
    }

    @Test
    public void shouldSucceedWhenWithinLimit() throws Exception {
        shouldReturnDefaultResult(ApiEndpoints.METHOD_LIMIT_1);
    }

    @Test
    public void shouldFailWhenMethodLimitIsExceeded() throws Exception {

        final String endpoint = ApiEndpoints.METHOD_LIMIT_1;

        shouldReturnDefaultResult(endpoint);

        assertThrows(Exception.class, () -> shouldReturnDefaultResult(endpoint));
    }

    @Test
    public void orLimitGroupShouldFailWhenOneOfManyLimitsIsExceeded() throws Exception {

        final String endpoint = ApiEndpoints.METHOD_LIMIT_1_OR_5;

        shouldReturnDefaultResult(endpoint);

        assertThrows(Exception.class, () -> shouldReturnDefaultResult(endpoint));
    }

    @Test
    public void orLimitGroupShouldFailWhenOneOfManyLimitsIsExceededAfterADelay() throws Exception {

        final String endpoint = ApiEndpoints.METHOD_LIMIT_1_OR_5;

        shouldReturnDefaultResult(endpoint);

        Thread.sleep(TimeUnit.SECONDS.toMillis(Constants.DURATION_SECONDS + 1));

        shouldReturnDefaultResult(endpoint);

        assertThrows(Exception.class, () -> shouldReturnDefaultResult(endpoint));
    }

    @Test
    public void andLimitGroupShouldSucceedWhenOneOfManyLimitsIsExceeded() throws Exception {

        final String endpoint = ApiEndpoints.METHOD_LIMIT_1_AND_5;

        shouldReturnDefaultResult(endpoint);

        shouldReturnDefaultResult(endpoint);
    }

    @Test
    public void andLimitGroupShouldFailWhenAllOfManyLimitsIsExceeded() throws Exception {

        shouldFailWhenMaxLimitIsExceeded(ApiEndpoints.METHOD_LIMIT_1_AND_5, Constants.LIMIT_5);
    }
}