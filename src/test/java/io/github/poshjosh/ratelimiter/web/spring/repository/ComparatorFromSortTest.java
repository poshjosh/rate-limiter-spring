package io.github.poshjosh.ratelimiter.web.spring.repository;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ComparatorFromSortTest {

    @Test
    void testSortMany() {

        List<DummyObject> toSort = new ArrayList();
        for(int i=0; i<100; i++) {
            int n = (i + 1);
            DummyObject dummyObject = new DummyObject("name", false, n, Instant.now().plusSeconds(n));
            toSort.add(dummyObject);
        }

        List<DummyObject> expectedResult = new ArrayList<>(toSort);

        Collections.shuffle(toSort);

        testSort(toSort, expectedResult);
    }

    @Test
    void testSortByStringField() {

        DummyObject candidate1 = new DummyObject("name1", true, 1, Instant.now().plusSeconds(1));
        DummyObject candidate2 = new DummyObject("name2", false, 2, Instant.now().plusSeconds(2));
        DummyObject candidate3 = new DummyObject("name3", true, 3, Instant.now().plusSeconds(3));

        List<DummyObject> toSort = Arrays.asList(candidate3, candidate1, candidate2);

        List<DummyObject> expectedResult = Arrays.asList(candidate1, candidate2, candidate3);

        testSort(toSort, expectedResult);
    }

    @Test
    void testSortByBooleanField() {

        DummyObject candidate1 = new DummyObject("name", true, 1, Instant.now().plusSeconds(1));
        DummyObject candidate2 = new DummyObject("name", false, 2, Instant.now().plusSeconds(2));
        DummyObject candidate3 = new DummyObject("name", true, 3, Instant.now().plusSeconds(3));

        List<DummyObject> toSort = Arrays.asList(candidate3, candidate1, candidate2);

        List<DummyObject> expectedResult = Arrays.asList(candidate2, candidate1, candidate3);

        testSort(toSort, expectedResult);
    }

    @Test
    void testSortByIntegerField() {

        DummyObject candidate1 = new DummyObject("name", false, 3, Instant.now().plusSeconds(1));
        DummyObject candidate2 = new DummyObject("name", false, 2, Instant.now().plusSeconds(2));
        DummyObject candidate3 = new DummyObject("name", false, 1, Instant.now().plusSeconds(3));

        List<DummyObject> toSort = Arrays.asList(candidate3, candidate1, candidate2);

        List<DummyObject> expectedResult = Arrays.asList(candidate3, candidate2, candidate1);

        testSort(toSort, expectedResult);
    }

    @Test
    void testByInstantField() {

        DummyObject candidate1 = new DummyObject("name", false, 1, Instant.now().plusSeconds(3));
        DummyObject candidate2 = new DummyObject("name", false, 1, Instant.now().plusSeconds(1));
        DummyObject candidate3 = new DummyObject("name", false, 1, Instant.now().plusSeconds(2));

        List<DummyObject> toSort = Arrays.asList(candidate3, candidate1, candidate2);

        List<DummyObject> expectedResult = Arrays.asList(candidate2, candidate3, candidate1);

        testSort(toSort, expectedResult);
    }

    void testSort(List<DummyObject> toSort, List<DummyObject> expectedResult) {

        Sort sort = Sort.by("name", "disabled", "stat", "timeCreated");

        Collections.sort(toSort, new ComparatorFromSort<>(sort));

        assertThat(toSort).isEqualTo(expectedResult);
    }
}
