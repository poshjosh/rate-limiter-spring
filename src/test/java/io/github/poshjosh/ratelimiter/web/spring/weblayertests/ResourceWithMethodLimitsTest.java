package io.github.poshjosh.ratelimiter.web.spring.weblayertests;

import io.github.poshjosh.ratelimiter.annotations.Rate;
import io.github.poshjosh.ratelimiter.annotations.RateGroup;
import io.github.poshjosh.ratelimiter.model.Operator;
import io.github.poshjosh.ratelimiter.web.spring.RateLimitPropertiesSpring;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@WebMvcControllersTest(classes = {
        ResourceWithMethodLimitsTest.Resource.class, ResourceWithMethodLimitsTest.TestConfig.class })
class ResourceWithMethodLimitsTest extends AbstractResourceTest {

    @Configuration
    static class TestConfig {
        public TestConfig(RateLimitPropertiesSpring properties) {
            properties.setResourcePackages(Collections.emptyList());
            properties.setResourceClasses(Arrays.asList(ResourceWithMethodLimitsTest.Resource.class));
        }
    }

    @RateGroup(id = "resource-with-method-limit-group", operator = Operator.AND)
    @Rate(permits = 1, duration = Resource.DURATION_SECONDS)
    @Rate(permits = 5, duration = Resource.DURATION_SECONDS)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.METHOD})
    private @interface MyRateGroup{ }

    @RestController
    @RequestMapping(ApiEndpoints.API)
    static class Resource {

        static final int DURATION_SECONDS = 3;

        private static final String ROOT = "/resource-with-method-limits-test";

        interface InternalEndpoints {
            String HOME = ROOT + "/home";
            String LIMIT_1 = ROOT + "/limit_1";
            String LIMIT_1_OR_5 = ROOT + "/limit_1_or_5";
            String LIMIT_1_AND_5 = ROOT + "/limit_1_and_5";
        }

        interface Endpoints {
            String METHOD_LIMITS_HOME = ApiEndpoints.API + InternalEndpoints.HOME;
            String METHOD_LIMIT_1 = ApiEndpoints.API + InternalEndpoints.LIMIT_1;
            String METHOD_LIMIT_1_OR_5 = ApiEndpoints.API + InternalEndpoints.LIMIT_1_OR_5;
            String METHOD_LIMIT_1_AND_5 = ApiEndpoints.API + InternalEndpoints.LIMIT_1_AND_5;
        }

        @RequestMapping(InternalEndpoints.HOME)
        public String home(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping(InternalEndpoints.LIMIT_1)
        @Rate(permits = 1, duration = Resource.DURATION_SECONDS, timeUnit = TimeUnit.SECONDS)
        public String limit_1(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping(InternalEndpoints.LIMIT_1_OR_5)
        @Rate(permits = 1, duration = Resource.DURATION_SECONDS, timeUnit = TimeUnit.SECONDS)
        @Rate(permits = 5, duration = Resource.DURATION_SECONDS, timeUnit = TimeUnit.SECONDS)
        public String limit_1_or_5(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping(InternalEndpoints.LIMIT_1_AND_5)
        @MyRateGroup
        public String limit_1_and_5(HttpServletRequest request) {
            return request.getRequestURI();
        }
    }

    @Test
    void homePageShouldReturnDefaultResult() throws Exception {
        shouldReturnDefaultResult(Resource.Endpoints.METHOD_LIMITS_HOME);
    }

    @Test
    void shouldSucceedWhenWithinLimit() throws Exception {
        shouldReturnDefaultResult(Resource.Endpoints.METHOD_LIMIT_1);
    }

    @Test
    void shouldFailWhenMethodLimitIsExceeded() throws Exception {

        final String endpoint = Resource.Endpoints.METHOD_LIMIT_1;

        shouldReturnDefaultResult(endpoint);

        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    void orLimitGroupShouldFailWhenOneOfManyLimitsIsExceeded() throws Exception {

        final String endpoint = Resource.Endpoints.METHOD_LIMIT_1_OR_5;

        shouldReturnDefaultResult(endpoint);

        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    void orLimitGroupShouldFailWhenOneOfManyLimitsIsExceededAfterADelay() throws Exception {

        final String endpoint = Resource.Endpoints.METHOD_LIMIT_1_OR_5;

        shouldReturnDefaultResult(endpoint);

        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    void andLimitGroupShouldSucceedWhenOnlyOneLimitIsExceeded() throws Exception {

        final String endpoint = Resource.Endpoints.METHOD_LIMIT_1_AND_5;

        shouldReturnDefaultResult(endpoint);

        shouldReturnDefaultResult(endpoint);
    }

    @Test
    void andLimitGroupShouldFailWhenAllLimitsAreExceeded() throws Exception {

        shouldFailWhenMaxLimitIsExceeded(Resource.Endpoints.METHOD_LIMIT_1_AND_5, 5);
    }
}