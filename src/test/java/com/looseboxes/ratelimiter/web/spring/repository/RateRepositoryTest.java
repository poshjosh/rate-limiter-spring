package com.looseboxes.ratelimiter.web.spring.repository;

import com.looseboxes.ratelimiter.bandwidths.Bandwidth;
import com.looseboxes.ratelimiter.bandwidths.Bandwidths;
import com.looseboxes.ratelimiter.cache.RateCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class RateRepositoryTest {

    private final RateCache<Integer> rateCache = RateCache.ofMap();
    private final RateRepository<RateEntity<Integer>, Integer> rateRepository =
            new RateRepositoryForCache<>(new RateCacheWithKeysImpl<>(rateCache));

    @Test
    void save_returnsSavedEntity() {
        RateEntity<Integer> toSave = entity(1, 1);
        assertThat(rateRepository.save(toSave)).isEqualTo(toSave);
    }

    @Test
    void savedEntityCanBeFound() {
        final Integer id = 1;
        RateEntity<Integer> toSave = entity(id, 1);
        rateRepository.save(toSave);
        assertThat(rateRepository.findById(id).orElse(null)).isEqualTo(toSave);
    }

    @Test
    void findAll_givenPageable() {
        List<RateEntity<Integer>> toSave = Arrays.asList(entity(1, 1), entity(2, 2));
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
                entity(mediumId, 2), entity(largerId, 3), entity(smallerId,1));
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
        rateRepository.save(entity(1, 1));
        Iterable<RateEntity<Integer>> found = rateRepository.findAll(Example.of(entity(2, 2)));
        assertThat(found.iterator().hasNext()).isFalse();
    }

    @Test
    void findAll_givenExampleWithUnmatchedValueButSameId() {
        final Integer id = 1;
        rateRepository.save(entity(id, 1));
        Iterable<RateEntity<Integer>> found = rateRepository.findAll(Example.of(entity(id, 2)));
        assertThat(found.iterator().hasNext()).isFalse();
    }

    @Test
    void findAll_givenMatchingExample() {
        List<RateEntity<Integer>> toSave = Arrays.asList(entity(1, 1), entity(2, 2));
        rateRepository.saveAll(toSave);
        final RateEntity<Integer> expected = toSave.get(toSave.size() - 1);
        Iterable<RateEntity<Integer>> found = rateRepository.findAll(Example.of(expected));
        assertThat(found.iterator().next()).isEqualTo(expected);
    }
    
    private RateEntity<Integer> entity(int id, double permitsPerSecond) {
        return new RateEntity<>(id, bandwidths(permitsPerSecond));
    }
    
    private Bandwidths bandwidths(double permitsPerSecond) {
        return Bandwidths.of(Bandwidth.bursty(permitsPerSecond));
    }
}
