package io.github.poshjosh.ratelimiter.web.spring.weblayertests;

import io.github.poshjosh.ratelimiter.annotations.Rate;
import io.github.poshjosh.ratelimiter.annotations.RateCondition;
import io.github.poshjosh.ratelimiter.web.core.WebExpressionKey;
import io.github.poshjosh.ratelimiter.web.spring.RateLimitPropertiesSpring;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcControllersTest(classes = {
        RateConditionRoleTest.Resource.class, RateConditionRoleTest.TestConfig.class })
class RateConditionRoleTest extends AbstractResourceTest{

    @Configuration
    static class TestConfig {
        public TestConfig(RateLimitPropertiesSpring properties) {
            properties.setResourcePackages(Collections.emptyList());
            properties.setResourceClasses(Arrays.asList(RateConditionRoleTest.Resource.class));
        }
    }

    private static final String ROOT = "/rate-condition-role-test";

    private static final String validUserRole = TestWebSecurityConfigurer.TEST_USER_ROLE;
    private static final String invalidUserRole = "invalid-" + validUserRole;
    private static final String invalidUserRole2 = invalidUserRole + "2";

    @RestController
    @RequestMapping(ApiEndpoints.API + ROOT)
    public static class Resource { // Has to be public for tests to succeed

        interface Endpoints{
            String ROLE_NO_MATCH = ApiEndpoints.API + ROOT + "/role-no-match";
            String ROLE_MATCH = ApiEndpoints.API + ROOT + "/role-match";
        }

        @RequestMapping("/role-no-match")
        @Rate(1)
        @RateCondition(WebExpressionKey.USER_ROLE + " = " + invalidUserRole)
        public String roleNoMatch(HttpServletRequest request) {
            System.out.println("RateConditionTest#roleNoMatch, sessionId: " + request.getSession().getId());
            return request.getRequestURI();
        }

        @RequestMapping("/role-no-match-or")
        @Rate(1)
        @RateCondition(WebExpressionKey.USER_ROLE + " = [" + invalidUserRole + " | " + invalidUserRole2+"]")
        public String roleNoMatch_or(HttpServletRequest request) {
            System.out.println("RateConditionTest#roleNoMatch_or, sessionId: " + request.getSession().getId());
            return request.getRequestURI();
        }

        @RequestMapping("/role-match")
        @Rate(1)
        @RateCondition(WebExpressionKey.USER_ROLE + " = " + validUserRole)
        public String roleMatch(HttpServletRequest request) {
            System.out.println("RateConditionTest#roleMatch, sessionId: " + request.getSession().getId());
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

    @Override
    protected MockHttpServletRequestBuilder requestBuilder(HttpMethod method, String endpoint) {
        MockHttpServletRequestBuilder builder = super.requestBuilder(method, endpoint);
        builder.with(request -> {
            request.setUserPrincipal(() -> TestWebSecurityConfigurer.TEST_USER_NAME);
            request.addUserRole(validUserRole);
            return request;
        });
        return builder;
    }
}
