package com.looseboxes.ratelimiter.web.spring.repository;

import com.looseboxes.ratelimiter.cache.MapRateCache;
import com.looseboxes.ratelimiter.cache.RateCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class RateRepositoryTest {

    private static final class TestEntity {
        private final Integer id;
        private final String text;
        public TestEntity(Integer id) {
            this(id, null);
        }
        public TestEntity(Integer id, String text) {
            this.id = id;
            this.text = text;
        }
        @Override public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            TestEntity that = (TestEntity) o;
            return Objects.equals(id, that.id);
        }
        @Override public int hashCode() {
            return Objects.hash(id);
        }
        @Override public String toString() {
            return "TestEntity{" + "id=" + id + ", text='" + text + '\'' + '}';
        }
        // Tests will fail without these getters
        public Integer getId() {
            return id;
        }

        public String getText() {
            return text;
        }
    }


    private final RateCache<Integer, TestEntity> rateCache = new MapRateCache<>();
    private final RateRepository<TestEntity, Integer> rateRepository =
        new RateRepositoryForCache<>(new RateCacheWithKeysImpl<>(rateCache), testEntity -> testEntity.id);

    @Test
    void save_returnsSavedEntity() {
        TestEntity toSave = new TestEntity(1);
        assertThat(rateRepository.save(toSave)).isEqualTo(toSave);
    }

    @Test
    void savedEntityCanBeFound() {
        final Integer id = 1;
        TestEntity toSave = new TestEntity(id);
        rateRepository.save(toSave);
        assertThat(rateRepository.findById(id).orElse(null)).isEqualTo(toSave);
    }

    @Test
    void findAll_givenPageable() {
        List<TestEntity> toSave = Arrays.asList(new TestEntity(1), new TestEntity(2));
        final int pageSize = toSave.size() / 2;
        rateRepository.saveAll(toSave);
        Page<TestEntity> found = rateRepository.findAll(PageRequest.of(0, pageSize));
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
        List<TestEntity> toSave = Arrays.asList(
                new TestEntity(mediumId), new TestEntity(largerId), new TestEntity(smallerId));
        final int pageSize = toSave.size();
        rateRepository.saveAll(toSave);
        Pageable pageable = PageRequest.of(0, pageSize, Sort.by(direction, "id"));
        Page<TestEntity> found = rateRepository.findAll(pageable);
        assertThat(found.getSize()).isEqualTo(pageSize);
        assertThat(found.getTotalElements()).isEqualTo(toSave.size());
        assertThat(found.getContent().get(0).id).isEqualTo(Sort.Direction.ASC.equals(direction) ? smallerId : largerId);
    }

    @Test
    void findAll_givenUnmatchedExample() {
        rateRepository.save(new TestEntity(1, "One"));
        Iterable<TestEntity> found = rateRepository.findAll(Example.of(new TestEntity(2, "Two")));
        assertThat(found.iterator().hasNext()).isFalse();
    }

    @Test
    void findAll_givenMatchingExample() {
        List<TestEntity> toSave = Arrays.asList(
                new TestEntity(1, "One"), new TestEntity(2, "Two"));
        rateRepository.saveAll(toSave);
        final TestEntity expected = toSave.get(toSave.size() - 1);
        Iterable<TestEntity> found = rateRepository.findAll(Example.of(expected));
        assertThat(found.iterator().next()).isEqualTo(expected);
    }
}
