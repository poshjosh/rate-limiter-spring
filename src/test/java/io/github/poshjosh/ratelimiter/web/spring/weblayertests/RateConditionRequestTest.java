package io.github.poshjosh.ratelimiter.web.spring.weblayertests;

import io.github.poshjosh.ratelimiter.annotations.Rate;
import io.github.poshjosh.ratelimiter.annotations.RateCondition;
import io.github.poshjosh.ratelimiter.web.core.WebExpressionKey;
import io.github.poshjosh.ratelimiter.web.spring.RateLimitPropertiesSpring;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@WebMvcControllersTest(classes = {
        RateConditionRequestTest.Resource.class, RateConditionRequestTest.TestConfig.class })
class RateConditionRequestTest extends AbstractResourceTest{

    @Configuration
    static class TestConfig {
        public TestConfig(RateLimitPropertiesSpring properties) {
            properties.setResourcePackages(Collections.emptyList());
            properties.setResourceClasses(Arrays.asList(RateConditionRequestTest.Resource.class));
        }
    }

    private static final String ROOT = "/rate-condition-request-test";

    @RestController
    @RequestMapping(ApiEndpoints.API + ROOT)
    public static class Resource { // Has to be public for tests to succeed

        static final String PATH_ONE = "/one";
        static final String PATH_TWO = "/two";
        static final String ENDPOINT_ONE = ApiEndpoints.API + ROOT + PATH_ONE;
        static final String ENDPOINT_TWO = ApiEndpoints.API + ROOT + PATH_TWO;

        @RequestMapping(PATH_ONE)
        @Rate("1/s")
        @RateCondition(WebExpressionKey.SESSION_ID + " !=")
        public String endpointOne(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @GetMapping(PATH_TWO)
        @Rate(rate = "1/m", when = WebExpressionKey.HEADER + "[X-SAMPLE-TRIGGER] = true")
        public String endpointTwoGet(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @PostMapping(PATH_TWO)
        @Rate(rate = "1/m", when = WebExpressionKey.SESSION_ID + " !=")
        public String endpointTwoPost(HttpServletRequest request) {
            return request.getRequestURI();
        }
    }

    private MockHttpSession session;

    @BeforeEach
    void beforeEach() {
        session = new MockHttpSession();
    }

    @Test
    void shouldBeRateLimitedWhenSessionIdExists() throws Exception{
        final String endpoint = Resource.ENDPOINT_ONE;
        shouldReturnDefaultResult(endpoint);
        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    void shouldBeRateLimitedWhenSamePathButDifferentMethodsAndRateConditions() throws Exception{
        shouldReturnDefaultResult(GET, Resource.ENDPOINT_TWO);
        shouldReturnDefaultResult(POST, Resource.ENDPOINT_TWO);
    }

    @Override
    protected MockHttpServletRequestBuilder requestBuilder(HttpMethod method, String endpoint) {
        MockHttpServletRequestBuilder builder = super.requestBuilder(method, endpoint);
        builder.session(session);
        return builder;
    }
}
