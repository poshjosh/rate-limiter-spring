package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.web.core.PathPatterns;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PathPatternsForClassTest extends AbstractPathPatternsTestBase {

    @Test
    void shouldMatchStartOf() {
        com.looseboxes.ratelimiter.web.core.PathPatterns pathPatterns = pathPatterns("/numbers");
        assertThat( pathPatterns.matches("/numbers")).isTrue();
        assertThat( pathPatterns.matches("/numbers/1")).isTrue();
        assertThat( pathPatterns.matches("/letters/a")).isFalse();
    }

    PathPatterns pathPatterns(String... uris) {
        return new PathPatternsForClass(uris);
    }
}
