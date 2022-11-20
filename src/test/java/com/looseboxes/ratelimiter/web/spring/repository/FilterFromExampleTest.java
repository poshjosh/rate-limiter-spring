package com.looseboxes.ratelimiter.web.spring.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class FilterFromExampleTest{

    DummyObject candidate1;
    DummyObject candidate2;
    DummyObject candidate3;

    List<DummyObject> candidates;

    @BeforeEach
    void setup() {
        candidate1 = new DummyObject("name1", true, 1, Instant.now().plusSeconds(1));
        candidate2 = new DummyObject("name2", false, 2, Instant.now().plusSeconds(2));
        candidate3 = new DummyObject("name3", true, 3, Instant.now().plusSeconds(3));
        candidates = Arrays.asList(candidate1, candidate2, candidate3);
    }

    @Test
    void testFilterByMultipleFields() {
        this.testFilter(candidates, candidate1, Arrays.asList(candidate1));
    }

    @Test
    void testFiltersByNoField() {

        DummyObject params = new DummyObject(null, null, null, null);

        this.testFilter(candidates, params, candidates);
    }

    @Test
    void testFilterByStringField() {

        DummyObject expectedResult = candidate3;

        DummyObject params = new DummyObject(expectedResult.getName(), null, null, null);

        this.testFilter(candidates, params, Arrays.asList(expectedResult));
    }

    @Test
    void testFilterByBooleanField() {

        DummyObject expectedResult = candidate2;

        DummyObject params = new DummyObject(null, expectedResult.getDisabled(), null, null);

        this.testFilter(candidates, params, Arrays.asList(expectedResult));
    }

    @Test
    void testFilterByIntegerField() {

        DummyObject expectedResult = candidate1;

        DummyObject params = new DummyObject(null, null, expectedResult.getStat(), null);

        this.testFilter(candidates, params, Arrays.asList(expectedResult));
    }

    @Test
    void testFilterByInstantField() {

        DummyObject expectedResult = candidate1;

        DummyObject params = new DummyObject(null, null, null, expectedResult.getTimeCreated());

        this.testFilter(candidates, params, Arrays.asList(expectedResult));
    }

    void testFilter(List<DummyObject> toFilter, DummyObject params, List<DummyObject> expectedResult) {

        Predicate<DummyObject> filter = new FilterFromExample<>(Example.of(params));

        List<DummyObject> result = toFilter.stream().filter(filter).collect(Collectors.toList());

        assertThat(result).isEqualTo(expectedResult);
    }
}
