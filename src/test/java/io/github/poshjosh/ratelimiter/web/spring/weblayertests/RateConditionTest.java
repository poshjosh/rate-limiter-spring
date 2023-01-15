package io.github.poshjosh.ratelimiter.web.spring.weblayertests;

import io.github.poshjosh.ratelimiter.annotations.Rate;
import io.github.poshjosh.ratelimiter.annotations.RateCondition;
import io.github.poshjosh.ratelimiter.web.core.WebExpressionKey;
import io.github.poshjosh.ratelimiter.web.spring.RateLimitPropertiesSpring;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.nio.file.attribute.UserPrincipal;
import java.util.Arrays;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcControllersTest(classes = {
        RateConditionTest.Resource.class, RateConditionTest.TestConfig.class })
class RateConditionTest extends AbstractResourceTest{

    @Configuration
    static class TestConfig {
        public TestConfig(RateLimitPropertiesSpring properties) {
            properties.setResourcePackages(Collections.emptyList());
            properties.setResourceClasses(Arrays.asList(RateConditionTest.Resource.class));
        }
    }

    private static final String ROOT = "/rate-condition-test";

    private static final String validUserRole = TestWebSecurityConfigurer.TEST_USER_ROLE;
    private static final String invalidUserRole = "invalid-" + validUserRole;
    private static final String invalidUserRole2 = invalidUserRole + "2";

    private static final String headerName = "test-header-name";
    private static final String headerValue = "test-header-value";

    private static final String cookieName = "text-cookie-name";
    private static final String cookieValue = "text-cookie-value";

    private static final String acceptLang1 = "en-US";
    private static final String acceptLang2 = "en-UK";
    private static final String noAcceptLang1 = "fr-FR";
    private static final String noAcceptLang2 = "fr-CA";

    @RestController
    @RequestMapping(ApiEndpoints.API + ROOT)
    public static class Resource { // Has to be public for tests to succeed

        interface Endpoints{
            String ROLE_NO_MATCH = ApiEndpoints.API + ROOT + "/role-no-match";
            String ROLE_MATCH = ApiEndpoints.API + ROOT + "/role-match";
            String COOKIE_NO_MATCH = ApiEndpoints.API + ROOT + "/cookie-no-match";
            String COOKIE_MATCH = ApiEndpoints.API + ROOT + "/cookie-match";
            String COOKIE_MATCH_NAME_ONLY = ApiEndpoints.API + ROOT + "/cookie-match-name-only";
            String COOKIE_NEGATE_MATCH_NAME_ONLY = ApiEndpoints.API + ROOT + "/cookie-negate-match-name-only";
            String HEADER_NO_MATCH = ApiEndpoints.API + ROOT + "/header-no-match";
            String HEADER_MATCH = ApiEndpoints.API + ROOT + "/header-match";
            String HEADER_MATCH_NAME_ONLY = ApiEndpoints.API + ROOT + "/header-match-name-only";
            String LANG_NO_MATCH_OR = ApiEndpoints.API + ROOT + "/lang-no-match-or";
            String LANG_MATCH_OR = ApiEndpoints.API + ROOT + "/lang-match-or";
            String LANG_NO_MATCH_AND = ApiEndpoints.API + ROOT + "/lang-no-match-and";
            String LANG_MATCH_AND = ApiEndpoints.API + ROOT + "/lang-match-and";
        }

        @RequestMapping("/role-no-match")
        @Rate(1)
        @RateCondition(WebExpressionKey.USER_ROLE + "=" + invalidUserRole)
        public String roleNoMatch(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/role-no-match-or")
        @Rate(1)
        @RateCondition(WebExpressionKey.USER_ROLE + "=[" + invalidUserRole + "|" + invalidUserRole2+"]")
        public String roleNoMatch_or(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/role-match")
        @Rate(1)
        @RateCondition(WebExpressionKey.USER_ROLE + "=" + validUserRole)
        public String roleMatch(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/cookie-no-match")
        @Rate(1)
        @RateCondition(WebExpressionKey.COOKIE + "={"+cookieName+"=invalid-value}")
        public String cookieNoMatch(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/cookie-match")
        @Rate(1)
        @RateCondition(WebExpressionKey.COOKIE + "={"+cookieName+"="+cookieValue+"}")
        public String cookieMatch(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/cookie-match-name-only")
        @Rate(1)
        @RateCondition(WebExpressionKey.COOKIE + "=" + cookieName)
        public String cookieMatchNameOnly(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/cookie-negate-match-name-only")
        @Rate(1)
        @RateCondition(WebExpressionKey.COOKIE + "!=" + cookieName)
        public String cookieNegateMatchNameOnly(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/header-no-match")
        @Rate(1)
        @RateCondition(WebExpressionKey.HEADER + "={invalid-header-name=invalid-header-value}")
        public String headerNoMatch(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/header-match")
        @Rate(1)
        @RateCondition(WebExpressionKey.HEADER + "={"+headerName+"="+headerValue+"}")
        public String headerMatch(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/header-match-name-only")
        @Rate(1)
        @RateCondition(WebExpressionKey.HEADER + "=" + headerName)
        public String headerMatchNameOnly(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/lang-no-match-or")
        @Rate(1)
        @RateCondition(WebExpressionKey.LOCALE + "=[" + noAcceptLang1 + "|" + noAcceptLang2 + "]")
        public String langNoMatch_or(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/lang-match-or")
        @Rate(1)
        @RateCondition(WebExpressionKey.LOCALE + "=[" + acceptLang1 + "|" + noAcceptLang1 + "]")
        public String langMatch_or(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/lang-no-match-and")
        @Rate(1)
        @RateCondition(WebExpressionKey.LOCALE + "=[" + acceptLang1 + "&" + noAcceptLang1 + "]")
        public String langNoMatch_and(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/lang-match-and")
        @Rate(1)
        @RateCondition(WebExpressionKey.LOCALE + "=[" + acceptLang1 + "&" + acceptLang1 + "]")
        public String langMatch_and(HttpServletRequest request) {
            return request.getRequestURI();
        }
    }

    @Test
    @WithMockUser(roles = validUserRole)
    void shouldNotBeRateLimitedWhenRoleNoMatch() throws Exception{
        final String endpoint = Resource.Endpoints.ROLE_NO_MATCH;
        shouldReturnDefaultResult(endpoint);
        shouldReturnDefaultResult(endpoint);
    }

    @Test
    @WithMockUser(roles = validUserRole)
    void shouldBeRateLimitedWhenRoleMatch() throws Exception{
        final String endpoint = Resource.Endpoints.ROLE_MATCH;
        shouldReturnDefaultResult(endpoint);
        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    void shouldNotBeRateLimitedWhenCookieNoMatch() throws Exception{
        final String endpoint = Resource.Endpoints.COOKIE_NO_MATCH;
        shouldReturnDefaultResult(endpoint);
        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    void shouldBeRateLimitedWhenCookieMatch() throws Exception{
        final String endpoint = Resource.Endpoints.COOKIE_MATCH;
        shouldReturnDefaultResult(endpoint);
        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    void shouldBeRateLimitedWhenCookieMatchNameOnly() throws Exception{
        final String endpoint = Resource.Endpoints.COOKIE_MATCH_NAME_ONLY;
        shouldReturnDefaultResult(endpoint);
        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    void shouldNotBeRateLimitedWhenCookieNegateMatchNameOnly() throws Exception{
        final String endpoint = Resource.Endpoints.COOKIE_NEGATE_MATCH_NAME_ONLY;
        shouldReturnDefaultResult(endpoint);
        shouldReturnDefaultResult(endpoint);
    }

    @Test
    void shouldNotBeRateLimitedWhenHeaderNoMatch() throws Exception{
        final String endpoint = Resource.Endpoints.HEADER_NO_MATCH;
        shouldReturnDefaultResult(endpoint);
        shouldReturnDefaultResult(endpoint);
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
    void shouldNotBeRateLimited_givenOr_andNoneOfManyLangMatch() throws Exception{
        final String endpoint = Resource.Endpoints.LANG_NO_MATCH_OR;
        shouldReturnDefaultResult(endpoint);
        shouldReturnDefaultResult(endpoint);
    }

    @Test
    void shouldBeRateLimited_givenOr_andOneOfManyLangMatch() throws Exception{
        final String endpoint = Resource.Endpoints.LANG_MATCH_OR;
        shouldReturnDefaultResult(endpoint);
        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    void shouldNotBeRateLimited_givenAnd_andOnlySomeOfManyLangMatch() throws Exception{
        final String endpoint = Resource.Endpoints.LANG_NO_MATCH_AND;
        shouldReturnDefaultResult(endpoint);
        shouldReturnDefaultResult(endpoint);
    }

    @Test
    void shouldBeRateLimited_givenAnd_andAllOfManyLangMatch() throws Exception{
        final String endpoint = Resource.Endpoints.LANG_MATCH_AND;
        shouldReturnDefaultResult(endpoint);
        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Override
    protected MockHttpServletRequestBuilder doGet(String endpoint) {
        MockHttpServletRequestBuilder builder = get(endpoint);
        builder.header("Accept-Language", acceptLang1 + ',' + acceptLang2);
        builder.header(headerName, headerValue);
        builder.with(request -> {
            request.setUserPrincipal((UserPrincipal)() -> TestWebSecurityConfigurer.TEST_USER_NAME);
            request.addUserRole(validUserRole);
            request.setCookies(new Cookie(cookieName, cookieValue));
            return request;
        });
        return builder;
    }
}
