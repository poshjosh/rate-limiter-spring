package io.github.poshjosh.ratelimiter.web.spring.weblayertests;

import io.github.poshjosh.ratelimiter.annotations.Rate;
import io.github.poshjosh.ratelimiter.annotations.RateCondition;
import io.github.poshjosh.ratelimiter.web.core.WebExpressionKey;
import io.github.poshjosh.ratelimiter.web.spring.RateLimitPropertiesSpring;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcControllersTest(classes = {
        RateConditionHeaderTest.Resource.class, RateConditionHeaderTest.TestConfig.class })
class RateConditionHeaderTest extends AbstractResourceTest{

    @Configuration
    static class TestConfig {
        public TestConfig(RateLimitPropertiesSpring properties) {
            properties.setResourcePackages(Collections.emptyList());
            properties.setResourceClasses(Arrays.asList(RateConditionHeaderTest.Resource.class));
        }
    }

    private static final String ROOT = "/rate-condition-header-test";

    private static final String headerName = "test-header-name";
    private static final String headerValue = "test-header-value";

    @RestController
    @RequestMapping(ApiEndpoints.API + ROOT)
    public static class Resource { // Has to be public for tests to succeed

        interface Endpoints{
            String HEADER_NO_MATCH = ApiEndpoints.API + ROOT + "/header-no-match";
            String HEADER_NEGATE_NO_MATCH = ApiEndpoints.API + ROOT + "/header-negate-no-match";
            String HEADER_MATCH = ApiEndpoints.API + ROOT + "/header-match";
            String HEADER_MATCH_NAME_ONLY = ApiEndpoints.API + ROOT + "/header-match-name-only";
            String HEADER_NEGATE_MATCH_NAME_ONLY = ApiEndpoints.API + ROOT + "/header-negate-match-name-only";
            String HEADER_MATCH_OR = ApiEndpoints.API + ROOT + "/header-match-or";
            String HEADER_NO_MATCH_BAD_OR = ApiEndpoints.API + ROOT + "/header-no-match-bad-or";
        }

        @RequestMapping("/header-no-match")
        @Rate(1)
        @RateCondition(WebExpressionKey.HEADER + " = {invalid-header-name = invalid-header-value}")
        public String headerNoMatch(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/header-negate-no-match")
        @Rate(1)
        @RateCondition(WebExpressionKey.HEADER + " != {invalid-header-name = invalid-header-value}")
        public String headerNegateNoMatch(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/header-match")
        @Rate(1)
        @RateCondition(WebExpressionKey.HEADER + " = {"+headerName+" = "+headerValue+"}")
        public String headerMatch(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/header-match-name-only")
        @Rate(1)
        @RateCondition(WebExpressionKey.HEADER + " = " + headerName)
        public String headerMatchNameOnly(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/header-negate-match-name-only")
        @Rate(1)
        @RateCondition(WebExpressionKey.HEADER + " != " + headerName)
        public String headerNegateMatchNameOnly(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/header-match-or")
        @Rate(1)
        @RateCondition(WebExpressionKey.HEADER + " = {" + headerName + " = [invalid-header-value | " + headerValue + "]}")
        public String headerMatchOr(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/header-no-match-bad-or")
        @Rate(1)
        // Badly formatted, should be {header={name=[A|B]}}, but the second equals sign is missing
        @RateCondition(WebExpressionKey.HEADER + " = {" + headerName + "[invalid-header-value | " + headerValue + "]}")
        public String headerNoMatchBadOr(HttpServletRequest request) {
            return request.getRequestURI();
        }
    }

    @Test
    void shouldNotBeRateLimitedWhenHeaderNoMatch() throws Exception{
        final String endpoint = Resource.Endpoints.HEADER_NO_MATCH;
        shouldReturnDefaultResult(endpoint);
        shouldReturnDefaultResult(endpoint);
    }

    @Test
    void shouldBeRateLimitedWhenHeaderNegateNoMatch() throws Exception{
        final String endpoint = Resource.Endpoints.HEADER_NEGATE_NO_MATCH;
        shouldReturnDefaultResult(endpoint);
        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    void shouldBeRateLimitedWhenHeaderMatch() throws Exception{
        final String endpoint = Resource.Endpoints.HEADER_MATCH;
        shouldReturnDefaultResult(endpoint);
        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    void shouldBeRateLimitedWhenHeaderMatchNameOnly() throws Exception{
        final String endpoint = Resource.Endpoints.HEADER_MATCH_NAME_ONLY;
        shouldReturnDefaultResult(endpoint);
        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    void shouldNotBeRateLimitedWhenHeaderNegateMatchNameOnly() throws Exception{
        final String endpoint = Resource.Endpoints.HEADER_NEGATE_MATCH_NAME_ONLY;
        shouldReturnDefaultResult(endpoint);
        shouldReturnDefaultResult(endpoint);
    }

    @Test
    void shouldBeRateLimitedWhenHeaderMatchOr() throws Exception{
        final String endpoint = Resource.Endpoints.HEADER_MATCH_OR;
        shouldReturnDefaultResult(endpoint);
        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    void shouldNotBeRateLimitedWhenHeaderNoMatch_givenBadOr() throws Exception{
        final String endpoint = Resource.Endpoints.HEADER_NO_MATCH_BAD_OR;
        shouldReturnDefaultResult(endpoint);
        shouldReturnDefaultResult(endpoint);
    }

    @Override
    protected MockHttpServletRequestBuilder requestBuilder(HttpMethod method, String endpoint) {
        MockHttpServletRequestBuilder builder = super.requestBuilder(method, endpoint);
        builder.header(headerName, headerValue);
        return builder;
    }
}
