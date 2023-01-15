package io.github.poshjosh.ratelimiter.web.spring.weblayertests;

import io.github.poshjosh.ratelimiter.annotation.Rate;
import io.github.poshjosh.ratelimiter.web.spring.RateLimitPropertiesSpring;
import io.github.poshjosh.ratelimiter.web.spring.repository.RateRepository;
import io.github.poshjosh.ratelimiter.web.spring.repository.RateEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@WebMvcControllersTest(classes = {
        RateRepositoryTest.Resource.class, RateRepositoryTest.TestConfig.class })
class RateRepositoryTest extends AbstractResourceTest {

    @Configuration
    static class TestConfig {
        public TestConfig(RateLimitPropertiesSpring properties) {
            properties.setResourcePackages(Collections.emptyList());
            properties.setResourceClasses(Arrays.asList(RateRepositoryTest.Resource.class));
        }
    }

    private static final int LIMIT = 3;

    @RestController
    @RequestMapping(ApiEndpoints.API + Resource._BASE)
    static class Resource {

        private static final String _BASE = "/rate-repository-test";
        private static final String _HOME = "/home";

        interface Endpoints {
            String HOME = ApiEndpoints.API + _BASE + _HOME;
        }

        @RequestMapping(_HOME)
        @Rate(LIMIT)
        public String home(HttpServletRequest request) {
            return request.getRequestURI();
        }
    }

    @Autowired RateRepository<RateEntity<Object>, Object> rateRepository;

    @Test
    void shouldPersistCorrectNumberOfRates() throws Exception {

        assertThat(rateRepository.findAll()).isEmpty();

        final int expectedAmount = LIMIT; // Just within limit

        final String endpoint = Resource.Endpoints.HOME;

        for(int i = 0; i < expectedAmount; i++) {
            shouldReturnDefaultResult(endpoint);
        }

        assertThat(rateRepository.count()).isEqualTo(1);
    }
}
