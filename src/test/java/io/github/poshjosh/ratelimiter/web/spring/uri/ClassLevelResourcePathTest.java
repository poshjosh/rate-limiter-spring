package io.github.poshjosh.ratelimiter.web.spring.uri;

import io.github.poshjosh.ratelimiter.web.core.util.ResourcePath;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ClassLevelResourcePathTest extends AbstractResourcePathTestBase {

    @Test
    void shouldMatchStartOf() {
        ResourcePath resourcePath = givenResourcePath("/numbers");
        assertThat( resourcePath.matches("/numbers")).isTrue();
        assertThat( resourcePath.matches("/numbers/1")).isTrue();
        assertThat( resourcePath.matches("/letters/a")).isFalse();
    }

    ResourcePath givenResourcePath(String... uris) {
        return new ClassLevelResourcePath(uris);
    }
}
