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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcControllersTest(classes = {
        RateConditionCookieTest.Resource.class, RateConditionCookieTest.TestConfig.class })
class RateConditionCookieTest extends AbstractResourceTest{

    @Configuration
    static class TestConfig {
        public TestConfig(RateLimitPropertiesSpring properties) {
            properties.setResourcePackages(Collections.emptyList());
            properties.setResourceClasses(Arrays.asList(RateConditionCookieTest.Resource.class));
        }
    }

    private static final String ROOT = "/rate-condition-cookie-test";

    private static final String cookieName = "text-cookie-name";
    private static final String cookieValue = "text-cookie-value";

    @RestController
    @RequestMapping(ApiEndpoints.API + ROOT)
    public static class Resource { // Has to be public for tests to succeed

        interface Endpoints{
            String COOKIE_NO_MATCH = ApiEndpoints.API + ROOT + "/cookie-no-match";
            String COOKIE_MATCH = ApiEndpoints.API + ROOT + "/cookie-match";
            String COOKIE_MATCH_NAME_ONLY = ApiEndpoints.API + ROOT + "/cookie-match-name-only";
            String COOKIE_NEGATE_MATCH_NAME_ONLY = ApiEndpoints.API + ROOT + "/cookie-negate-match-name-only";
            String COOKIE_MATCH_OR = ApiEndpoints.API + ROOT + "/cookie-match-or";
            String COOKIE_NO_MATCH_BAD_OR = ApiEndpoints.API + ROOT + "/cookie-no-match-bad-or";
        }

        @RequestMapping("/cookie-no-match")
        @Rate("1/s")
        @RateCondition(WebExpressionKey.COOKIE + "["+cookieName+"] = invalid-value")
        public String cookieNoMatch(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/cookie-match")
        @Rate("1/s")
        @RateCondition(WebExpressionKey.COOKIE + "["+cookieName+"] = "+cookieValue)
        public String cookieMatch(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/cookie-match-name-only")
        @Rate("1/s")
        @RateCondition(WebExpressionKey.COOKIE + "[" + cookieName + "] =")
        public String cookieMatchNameOnly(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/cookie-negate-match-name-only")
        @Rate("1/s")
        @RateCondition(WebExpressionKey.COOKIE + "[" + cookieName + "] !=")
        public String cookieNegateMatchNameOnly(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/cookie-match-or")
        @Rate("1/s")
        @RateCondition(WebExpressionKey.COOKIE + "[" + cookieName + "] = [invalid-cookie-value | " + cookieValue + "]")
        public String cookieMatchOr(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/cookie-no-match-bad-or")
        @Rate("1/s")
        // Badly formatted
        @RateCondition(WebExpressionKey.COOKIE + " = " + cookieName + "[invalid-cookie-value | " + cookieValue + "]")
        public String cookieNoMatchBadOr(HttpServletRequest request) {
            return request.getRequestURI();
        }
    }

    @Test
    void shouldNotBeRateLimitedWhenCookieNoMatch() throws Exception{
        final String endpoint = Resource.Endpoints.COOKIE_NO_MATCH;
        shouldReturnDefaultResult(endpoint);
        shouldReturnDefaultResult(endpoint);
    }

    @Test
    void shouldBeRateLimitedWhenCookieMatch() throws Exception{
        final String endpoint = Resource.Endpoints.COOKIE_MATCH;
        shouldReturnDefaultResult(endpoint);
        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    void shouldNotBeRateLimitedWhenCookieMatchNameOnly() throws Exception{
        final String endpoint = Resource.Endpoints.COOKIE_MATCH_NAME_ONLY;
        shouldReturnDefaultResult(endpoint);
        shouldReturnDefaultResult(endpoint);
    }

    @Test
    void shouldBeRateLimitedWhenCookieNegateMatchNameOnly() throws Exception{
        final String endpoint = Resource.Endpoints.COOKIE_NEGATE_MATCH_NAME_ONLY;
        shouldReturnDefaultResult(endpoint);
        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    void shouldBeRateLimitedWhenCookieMatchOr() throws Exception{
        final String endpoint = Resource.Endpoints.COOKIE_MATCH_OR;
        shouldReturnDefaultResult(endpoint);
        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    void shouldThrowException_givenBadCondition() {
        final String endpoint = Resource.Endpoints.COOKIE_NO_MATCH_BAD_OR;
        assertThrows(UnsupportedOperationException.class, () -> shouldReturnDefaultResult(endpoint));
    }

    @Override
    protected MockHttpServletRequestBuilder requestBuilder(HttpMethod method, String endpoint) {
        MockHttpServletRequestBuilder builder = super.requestBuilder(method, endpoint);
        builder.with(request -> {
            request.setCookies(new Cookie(cookieName, cookieValue));
            return request;
        });
        return builder;
    }
}
