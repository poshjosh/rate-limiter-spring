package io.github.poshjosh.ratelimiter.web.spring.weblayertests;

import io.github.poshjosh.ratelimiter.annotation.Rate;
import io.github.poshjosh.ratelimiter.util.Operator;
import io.github.poshjosh.ratelimiter.web.core.annotation.RateRequestIf;
import io.github.poshjosh.ratelimiter.web.core.util.MatchType;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import java.nio.file.attribute.UserPrincipal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcControllersTest(classes = { RequestRateTest.Resource.class })
class RequestRateTest extends AbstractResourceTest{

    private static final String ROOT = "/request-rate-test";

    private static final String invalidUserRole = TestWebSecurityConfigurer.TEST_USER_ROLE + "-INVALIDATED";

    private static final String headerName = "test-header-name";
    private static final String headerValue = "test-header-value";

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
        @RateRequestIf(matchType = MatchType.USER_ROLE, values = invalidUserRole)
        public String roleNoMatch(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/role-no-match-or")
        @Rate(1)
        @RateRequestIf(matchType = MatchType.USER_ROLE, operator = Operator.OR,
                values = {invalidUserRole, invalidUserRole + "2"})
        public String roleNoMatch_or(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/role-match")
        @Rate(1)
        @RateRequestIf(matchType = MatchType.USER_ROLE, values = TestWebSecurityConfigurer.TEST_USER_ROLE)
        public String roleMatch(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/header-no-match")
        @Rate(1)
        @RateRequestIf(matchType = MatchType.HEADER, name = "invalid-header-name", values = "invalid-header-value")
        public String headerNoMatch(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/header-match")
        @Rate(1)
        @RateRequestIf(matchType = MatchType.HEADER, name = headerName, values = headerValue)
        public String headerMatch(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/header-match-name-only")
        @Rate(1)
        @RateRequestIf(matchType = MatchType.HEADER, name = headerName)
        public String headerMatchNameOnly(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/lang-no-match-or")
        @Rate(1)
        @RateRequestIf(matchType = MatchType.LOCALE, values = {noAcceptLang1, noAcceptLang2}, operator = Operator.OR)
        public String langNoMatch_or(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/lang-match-or")
        @Rate(1)
        @RateRequestIf(matchType = MatchType.LOCALE, values = {acceptLang1, noAcceptLang1}, operator = Operator.OR)
        public String langMatch_or(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/lang-no-match-and")
        @Rate(1)
        @RateRequestIf(matchType = MatchType.LOCALE, values = {acceptLang1, noAcceptLang1})
        public String langNoMatch_and(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/lang-match-and")
        @Rate(1)
        @RateRequestIf(matchType = MatchType.LOCALE, values = {acceptLang1, acceptLang2})
        public String langMatch_and(HttpServletRequest request) {
            return request.getRequestURI();
        }
    }

    @Test
    @WithMockUser(roles = TestWebSecurityConfigurer.TEST_USER_ROLE)
    void shouldNotBeRateLimitedWhenRoleNoMatch() throws Exception{
        final String endpoint = Resource.Endpoints.ROLE_NO_MATCH;
        shouldReturnDefaultResult(endpoint);
        shouldReturnDefaultResult(endpoint);
    }

    @Test
    @WithMockUser(roles = TestWebSecurityConfigurer.TEST_USER_ROLE)
    void shouldBeRateLimitedWhenRoleMatch() throws Exception{
        final String endpoint = Resource.Endpoints.ROLE_MATCH;
        shouldReturnDefaultResult(endpoint);
        shouldReturnStatusOfTooManyRequests(endpoint);
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
            request.addUserRole(TestWebSecurityConfigurer.TEST_USER_ROLE);
            return request;
        });
        return builder;
    }
}
