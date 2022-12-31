package com.looseboxes.ratelimiter.web.spring.weblayertests;

import com.looseboxes.ratelimiter.annotations.RateLimit;
import com.looseboxes.ratelimiter.web.spring.repository.RateEntity;
import com.looseboxes.ratelimiter.web.spring.repository.RateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@WebMvcControllersTest(classes = { RateRepositoryTest.Resource.class })
public class RateRepositoryTest extends AbstractResourceTest {

    private static final int LIMIT = 3;

    @RestController
    @RequestMapping(ApiEndpoints.API)
    static class Resource {

        private static final String _HOME = "/rate-repository-test/home";

        interface Endpoints {
            String HOME = ApiEndpoints.API + Resource._HOME;
        }

        @RequestMapping(Resource._HOME)
        @RateLimit(permits = LIMIT, duration = 1, timeUnit = TimeUnit.SECONDS)
        public String home(HttpServletRequest request) {
            return request.getRequestURI();
        }
    }

    @Autowired
    RateRepository<RateEntity<Object>, Object> rateRepository;

    @Test
    public void shouldPersistCorrectNumberOfRates() throws Exception {

        assertThat(rateRepository.findAll()).isEmpty();

        final int expectedAmount = LIMIT; // Just within limit

        final String endpoint = Resource.Endpoints.HOME;

        for(int i = 0; i < expectedAmount; i++) {
            shouldReturnDefaultResult(endpoint);
        }

        assertThat(rateRepository.count()).isEqualTo(1);
    }
}
