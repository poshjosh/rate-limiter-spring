package com.looseboxes.ratelimiter.web.spring.repository;

import com.looseboxes.ratelimiter.BandwidthFactory;
import com.looseboxes.ratelimiter.bandwidths.Bandwidth;
import com.looseboxes.ratelimiter.cache.RateCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.data.domain.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class RateRepositoryTest {

    private final RateCache<Integer, Object> rateCache = RateCache.ofMap();
    private final RateRepository<RateEntity<Integer>, Integer> rateRepository =
            new RateRepositoryForCache<>(new RateCacheWithKeysImpl<>(rateCache));

    @Test
    void save_returnsSavedEntity() {
        RateEntity<Integer> toSave = new RateEntity<>(1, "one");
        assertThat(rateRepository.save(toSave)).isEqualTo(toSave);
    }

    @Test
    void savedEntityCanBeFound() {
        final Integer id = 1;
        RateEntity<Integer> toSave = new RateEntity<>(id, "one");
        rateRepository.save(toSave);
        assertThat(rateRepository.findById(id).orElse(null)).isEqualTo(toSave);
    }

    @Test
    void findAll_givenPageable() {
        List<RateEntity<Integer>> toSave = Arrays.asList(new RateEntity<>(1, "one"), new RateEntity<>(2, "two"));
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
                new RateEntity<>(mediumId, "two"), new RateEntity<>(largerId, "three"), new RateEntity<>(smallerId,"1"));
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
        rateRepository.save(new RateEntity<>(1, "one"));
        Iterable<RateEntity<Integer>> found = rateRepository.findAll(Example.of(new RateEntity<>(2, "two")));
        assertThat(found.iterator().hasNext()).isFalse();
    }

    @Test
    void findAll_givenExampleWithUnmatchedValueButSameId() {
        final Integer id = 1;
        final Bandwidth lhs = createBandwidth(1);
        final Bandwidth rhs = createBandwidth(2);
        rateRepository.save(new RateEntity<>(id, lhs));
        Iterable<RateEntity<Integer>> found = rateRepository.findAll(Example.of(new RateEntity<>(id, rhs)));
        assertThat(found.iterator().hasNext()).isFalse();
    }

    private final BandwidthFactory bandwidthFactory = BandwidthFactory.bursty();
    private Bandwidth createBandwidth(long permitsPerSeconds) {
        return bandwidthFactory.createNew(permitsPerSeconds, Duration.ofSeconds(1));
    }

    @Test
    void findAll_givenMatchingExample() {
        List<RateEntity<Integer>> toSave = Arrays.asList(new RateEntity<>(1, "one"), new RateEntity<>(2, "two"));
        rateRepository.saveAll(toSave);
        final RateEntity<Integer> expected = toSave.get(toSave.size() - 1);
        Iterable<RateEntity<Integer>> found = rateRepository.findAll(Example.of(expected));
        assertThat(found.iterator().next()).isEqualTo(expected);
    }
}
