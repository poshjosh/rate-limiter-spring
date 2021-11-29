package com.looseboxes.ratelimiter.web.spring.weblayertests;

import com.looseboxes.ratelimiter.web.spring.repository.LimitWithinDurationDTO;
import com.looseboxes.ratelimiter.web.spring.repository.RateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@WebMvcTest(ResourceWithMethodLimits.class)
public class RateRepositoryTest extends AbstractResourceTest {

    @Autowired
    RateRepository<?, LimitWithinDurationDTO> rateRepository;

    @Test
    public void shouldPersistRateToRepository() throws Exception {

        assertThat(rateRepository.findAll()).isEmpty();

        shouldReturnDefaultResult(ApiEndpoints.METHOD_LIMIT_1);

        assertThat(rateRepository.findAll()).isNotEmpty();
    }

// @TODO Fix this
//    @Test
    public void shouldPersistCorrectNumberOfRates() throws Exception {

        assertThat(rateRepository.findAll()).isEmpty();

        final int expectedAmount = Constants.LIMIT_5; // Just within limit

        for(int i = 0; i < expectedAmount; i++) {
            shouldReturnDefaultResult(ApiEndpoints.METHOD_LIMIT_1_AND_5);
        }

        List<LimitWithinDurationDTO> rates = rateRepository.findAll();
        for(LimitWithinDurationDTO rate : rates) {
            assertThat(rate.getLimit()).isEqualTo(expectedAmount);
        }
    }
}
