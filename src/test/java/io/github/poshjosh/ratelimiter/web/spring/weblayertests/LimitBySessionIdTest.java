package io.github.poshjosh.ratelimiter.web.spring.weblayertests;

import io.github.poshjosh.ratelimiter.annotations.Rate;
import io.github.poshjosh.ratelimiter.web.core.WebExpressionKey;
import io.github.poshjosh.ratelimiter.web.spring.RateLimitPropertiesSpring;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;

@WebMvcControllersTest(classes = {
        LimitBySessionIdTest.Resource.class, LimitBySessionIdTest.TestConfig.class })
class LimitBySessionIdTest extends AbstractResourceTest{

    @Configuration
    static class TestConfig {
        public TestConfig(RateLimitPropertiesSpring properties) {
            properties.setResourcePackages(Collections.emptyList());
            properties.setResourceClasses(Arrays.asList(LimitBySessionIdTest.Resource.class));
        }
    }

    private static final String ROOT = "/limit-by-session-id-test";

    @RestController
    @RequestMapping(ApiEndpoints.API + ROOT)
    static class Resource {

        interface Endpoints{
            String BOOKS = ApiEndpoints.API + ROOT + "/books";
        }

        @GetMapping("/books")
        @Rate(permits=1, condition=WebExpressionKey.SESSION_ID+" !=")
        public String getAll(HttpServletRequest request) {
            System.out.println("LimitBySessionIdTest.Resource#getAll, sessionId: " + request.getSession().getId());
            return request.getRequestURI();
        }

        @GetMapping("/books/{id}")
        @Rate(permits=1, condition=WebExpressionKey.SESSION_ID+" !=")
        public String getOne(HttpServletRequest request, @PathVariable("id") String id) {
            System.out.println("LimitBySessionIdTest.Resource#getOne, sessionId: " + request.getSession().getId());
            return request.getRequestURI();
        }

        @DeleteMapping("/books/{id}")
        @Rate(permits=1, condition=WebExpressionKey.SESSION_ID+" !=")
        public String deleteOne(HttpServletRequest request, @PathVariable("id") String id) {
            System.out.println("LimitBySessionIdTest.Resource#deleteOne, sessionId: " + request.getSession().getId());
            return request.getRequestURI();
        }
    }

    private MockHttpSession session;

    @BeforeEach
    void beforeEach() {
        session = new MockHttpSession();
    }

    @Test
    void givenSameSessionIdAndEndpoint_differentHttpMethodsShouldBeRateLimitedSeparately() throws Exception{
        final String endpoint = Resource.Endpoints.BOOKS + "/1";
        shouldReturnDefaultResult(HttpMethod.GET, endpoint);
        shouldReturnDefaultResult(HttpMethod.DELETE, endpoint);
        shouldReturnStatusOfTooManyRequests(HttpMethod.GET, endpoint);
        shouldReturnStatusOfTooManyRequests(HttpMethod.DELETE, endpoint);
    }

    @Test
    void givenDifferentSessionIdsAndSameEndpoint_shouldNotBeRateLimited() throws Exception{
        final String endpoint = Resource.Endpoints.BOOKS + "/1";
        shouldReturnDefaultResult(HttpMethod.GET, endpoint);
        session = new MockHttpSession();
        shouldReturnDefaultResult(HttpMethod.DELETE, endpoint);
        session = new MockHttpSession();
        shouldReturnDefaultResult(HttpMethod.GET, endpoint);
        session = new MockHttpSession();
        shouldReturnDefaultResult(HttpMethod.DELETE, endpoint);
    }

    @Test
    void givenSameSessionIdAndHttpMethod_differentEndpointShouldBeRateLimitedSeparately() throws Exception{
        shouldReturnDefaultResult(HttpMethod.GET, Resource.Endpoints.BOOKS + "/1");
        shouldReturnDefaultResult(HttpMethod.GET, Resource.Endpoints.BOOKS);
        shouldReturnStatusOfTooManyRequests(HttpMethod.GET, Resource.Endpoints.BOOKS + "/1");
        shouldReturnStatusOfTooManyRequests(HttpMethod.GET, Resource.Endpoints.BOOKS);
    }

    @Override
    protected MockHttpServletRequestBuilder requestBuilder(HttpMethod method, String endpoint) {
        MockHttpServletRequestBuilder builder = super.requestBuilder(method, endpoint);
        builder.session(session);
        return builder;
    }
}
