package io.github.poshjosh.ratelimiter.web.spring.weblayertests;

import io.github.poshjosh.ratelimiter.annotations.Rate;
import io.github.poshjosh.ratelimiter.annotations.RateGroup;
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

@WebMvcControllersTest(classes = {
        RateGroupTest.Resource1.class, RateGroupTest.Resource2.class, RateGroupTest.TestConfig.class })
class RateGroupTest extends AbstractResourceTest{

    @Configuration
    static class TestConfig {
        public TestConfig(RateLimitPropertiesSpring properties) {
            properties.setResourcePackages(Collections.emptyList());
            properties.setResourceClasses(Arrays.asList(
                    RateGroupTest.Resource1.class, RateGroupTest.Resource2.class
            ));
        }
    }

    private static final String GROUP_NAME = "test-group";

    @Rate("1/s")
    @RateGroup(GROUP_NAME)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.METHOD})
    private @interface MyRateGroup{ }


    @MyRateGroup
    @RestController
    @RequestMapping(ApiEndpoints.API + Resource1._BASE)
    static class Resource1 {

        private static final String _BASE = "/rate-group-test/resource1";
        private static final String _HOME = "/home";

        @RequestMapping(_HOME)
        public String home(HttpServletRequest request) {
            return request.getRequestURI();
        }
    }

    @RestController
    @RequestMapping(ApiEndpoints.API + Resource2._BASE)
    static class Resource2 {

        private static final String _BASE = "/rate-group-test/resource2";
        private static final String _HOME = "/home";

        interface Endpoints{
            String HOME = ApiEndpoints.API + _BASE + _HOME;
        }

        @MyRateGroup
        @RequestMapping(_HOME)
        public String home(HttpServletRequest request) {
            return request.getRequestURI();
        }
    }

    @Test
    void groupMember_whenNoRateDefined_shouldBeRateLimitedByGroupRate() throws Exception{
        final String endpoint = Resource2.Endpoints.HOME;
        System.out.println();
        shouldReturnDefaultResult(endpoint);
        System.out.println();
        shouldReturnStatusOfTooManyRequests(endpoint);
    }
}
