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

@WebMvcControllersTest(classes = {
        RateConditionLocaleTest.Resource.class, RateConditionLocaleTest.TestConfig.class })
class RateConditionLocaleTest extends AbstractResourceTest{

    @Configuration
    static class TestConfig {
        public TestConfig(RateLimitPropertiesSpring properties) {
            properties.setResourcePackages(Collections.emptyList());
            properties.setResourceClasses(Arrays.asList(RateConditionLocaleTest.Resource.class));
        }
    }

    private static final String ROOT = "/rate-condition-locale-test";

    private static final String acceptLang1 = "en-US";
    private static final String acceptLang2 = "en-UK";
    private static final String noAcceptLang1 = "fr-FR";
    private static final String noAcceptLang2 = "fr-CA";

    @RestController
    @RequestMapping(ApiEndpoints.API + ROOT)
    public static class Resource { // Has to be public for tests to succeed

        interface Endpoints{
            String LANG_NO_MATCH_OR = ApiEndpoints.API + ROOT + "/lang-no-match-or";
            String LANG_MATCH_OR = ApiEndpoints.API + ROOT + "/lang-match-or";
            String LANG_NO_MATCH_AND = ApiEndpoints.API + ROOT + "/lang-no-match-and";
            String LANG_MATCH_AND = ApiEndpoints.API + ROOT + "/lang-match-and";
        }

        @RequestMapping("/lang-no-match-or")
        @Rate("1/s")
        @RateCondition(WebExpressionKey.LOCALE + " = [" + noAcceptLang1 + " | " + noAcceptLang2 + "]")
        public String langNoMatch_or(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/lang-match-or")
        @Rate("1/s")
        @RateCondition(WebExpressionKey.LOCALE + " = [" + acceptLang1 + " | " + noAcceptLang1 + "]")
        public String langMatch_or(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/lang-no-match-and")
        @Rate("1/s")
        @RateCondition(WebExpressionKey.LOCALE + " = [" + acceptLang1 + " & " + noAcceptLang1 + "]")
        public String langNoMatch_and(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/lang-match-and")
        @Rate("1/s")
        @RateCondition(WebExpressionKey.LOCALE + " = [" + acceptLang1 + " & " + acceptLang1 + "]")
        public String langMatch_and(HttpServletRequest request) {
            return request.getRequestURI();
        }
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
    protected MockHttpServletRequestBuilder requestBuilder(HttpMethod method, String endpoint) {
        MockHttpServletRequestBuilder builder = super.requestBuilder(method, endpoint);
        builder.header("Accept-Language", acceptLang1 + ',' + acceptLang2);
        return builder;
    }
}
