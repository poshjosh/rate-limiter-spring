package com.looseboxes.ratelimiter.web.spring.weblayertests;

import com.looseboxes.ratelimiter.Rate;
import com.looseboxes.ratelimiter.web.spring.repository.RateEntity;
import com.looseboxes.ratelimiter.web.spring.repository.RateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@WebMvcControllersTest(classes = { ResourceWithMethodLimits.class })
public class RateRepositoryTest extends AbstractResourceTest {

    @Autowired
    RateRepository<RateEntity<Object>, Object> rateRepository;

    @Test
    public void shouldPersistRateToRepository() throws Exception {

        assertThat(rateRepository.findAll()).isEmpty();

        shouldReturnDefaultResult(ApiEndpoints.METHOD_LIMIT_1);

        assertThat(rateRepository.findAll()).isNotEmpty();
    }

    @Test
    public void shouldPersistCorrectNumberOfRates() throws Exception {

        assertThat(rateRepository.findAll()).isEmpty();

        final int expectedAmount = Constants.LIMIT_5; // Just within limit

        for(int i = 0; i < expectedAmount; i++) {
            shouldReturnDefaultResult(ApiEndpoints.METHOD_LIMIT_1_AND_5);
        }

        Iterable<RateEntity<Object>> entities = rateRepository.findAll();

        for(RateEntity<Object> entity : entities) {
            Rate rate = entity.getRate();
            assertThat(rate.getAmount()).isEqualTo(expectedAmount);
        }
    }
}
