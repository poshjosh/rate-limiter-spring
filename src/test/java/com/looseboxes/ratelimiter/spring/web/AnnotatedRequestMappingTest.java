package com.looseboxes.ratelimiter.spring.web;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AnnotatedRequestMappingTest {

    @Test
    void shouldCombine() {
        AnnotatedRequestMapping annotatedRequestMapping = requestPaths("/numbers");
        AnnotatedRequestMapping result = annotatedRequestMapping.combine("/1", "/2");
        AnnotatedRequestMapping expected = requestPaths("/numbers/1", "/numbers/2");
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldMatchSingleAsterix() {
        AnnotatedRequestMapping annotatedRequestMapping = requestPaths("/*");
        assertThat(annotatedRequestMapping.matches("/numbers")).isTrue();
        assertThat(annotatedRequestMapping.matches("/numbers/1")).isFalse();
    }

    @Test
    void shouldMatchDoubleAsterix() {
        AnnotatedRequestMapping annotatedRequestMapping = requestPaths("/**");
        assertThat(annotatedRequestMapping.matches("/numbers")).isTrue();
        assertThat(annotatedRequestMapping.matches("/numbers/1")).isTrue();
    }

    @Test
    void shouldMatchStartOf() {
        AnnotatedRequestMapping annotatedRequestMapping = requestPaths("/numbers");
        assertThat(annotatedRequestMapping.matchesStartOf("/numbers/1")).isTrue();
        assertThat(annotatedRequestMapping.matchesStartOf("/letters/a")).isFalse();
    }

    AnnotatedRequestMapping requestPaths(String... uris) {
        return new AnnotatedRequestMappingImpl(uris);
    }
}