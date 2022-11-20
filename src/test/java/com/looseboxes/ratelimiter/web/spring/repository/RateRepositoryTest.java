package com.looseboxes.ratelimiter.web.spring.repository;

import com.looseboxes.ratelimiter.cache.MapRateCache;
import com.looseboxes.ratelimiter.cache.RateCache;
import com.looseboxes.ratelimiter.rates.AmountPerDuration;
import com.looseboxes.ratelimiter.rates.Rate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class RateRepositoryTest {

    private final RateCache<Integer, Rate> rateCache = new MapRateCache<>();
    private final RateRepository<RateEntity<Integer>, Integer> rateRepository =
            new RateRepositoryForCache<>(new RateCacheWithKeysImpl<>(rateCache));

    @Test
    void save_returnsSavedEntity() {
        RateEntity<Integer> toSave = new RateEntity<>(1);
        assertThat(rateRepository.save(toSave)).isEqualTo(toSave);
    }

    @Test
    void savedEntityCanBeFound() {
        final Integer id = 1;
        RateEntity<Integer> toSave = new RateEntity<>(id);
        rateRepository.save(toSave);
        assertThat(rateRepository.findById(id).orElse(null)).isEqualTo(toSave);
    }

    @Test
    void findAll_givenPageable() {
        List<RateEntity<Integer>> toSave = Arrays.asList(new RateEntity<>(1), new RateEntity<>(2));
        final int pageSize = toSave.size() / 2;
        rateRepository.saveAll(toSave);
        Page<RateEntity<Integer>> found = rateRepository.findAll(PageRequest.of(0, pageSize));
        assertThat(found.getSize()).isEqualTo(pageSize);
        assertThat(found.getTotalElements()).isEqualTo(toSave.size());
        assertThat(found.getContent().get(0)).isEqualTo(toSave.get(0));
    }

    @ParameterizedTest
    @EnumSource(Sort.Direction.class)
    void findAll_givenSort(Sort.Direction direction) {
        final int smallerId = 1;
        final int mediumId = 2;
        final int largerId = 3;
        List<RateEntity<Integer>> toSave = Arrays.asList(
                new RateEntity<>(mediumId), new RateEntity<>(largerId), new RateEntity<>(smallerId));
        final int pageSize = toSave.size();
        rateRepository.saveAll(toSave);
        Pageable pageable = PageRequest.of(0, pageSize, Sort.by(direction, "id"));
        Page<RateEntity<Integer>> found = rateRepository.findAll(pageable);
        assertThat(found.getSize()).isEqualTo(pageSize);
        assertThat(found.getTotalElements()).isEqualTo(toSave.size());
        assertThat(found.getContent().get(0).getId()).isEqualTo(Sort.Direction.ASC.equals(direction) ? smallerId : largerId);
    }

    @Test
    void findAll_givenExampleWithUnmatchedId() {
        rateRepository.save(new RateEntity<>(1));
        Iterable<RateEntity<Integer>> found = rateRepository.findAll(Example.of(new RateEntity<>(2)));
        assertThat(found.iterator().hasNext()).isFalse();
    }

    @Test
    void findAll_givenExampleWithUnmatchedValueButSameId() {
        final Integer id = 1;
        final AmountPerDuration lhs = new AmountPerDuration(1, 1);
        final AmountPerDuration rhs = new AmountPerDuration(2, 2);
        rateRepository.save(new RateEntity<>(id, lhs));
        Iterable<RateEntity<Integer>> found = rateRepository.findAll(Example.of(new RateEntity<>(id, rhs)));
        assertThat(found.iterator().hasNext()).isFalse();
    }

    @Test
    void findAll_givenMatchingExample() {
        List<RateEntity<Integer>> toSave = Arrays.asList(new RateEntity<>(1), new RateEntity<>(2));
        rateRepository.saveAll(toSave);
        final RateEntity<Integer> expected = toSave.get(toSave.size() - 1);
        Iterable<RateEntity<Integer>> found = rateRepository.findAll(Example.of(expected));
        assertThat(found.iterator().next()).isEqualTo(expected);
    }
}
