package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.web.core.PathPatterns;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PathPatternsForMethodTest extends AbstractPathPatternsTestBase {

    @Test
    void shouldMatchSingleAsterix() {
        PathPatterns  pathPatterns = pathPatterns("/*");
        assertThat( pathPatterns.matches("/numbers")).isTrue();
        assertThat( pathPatterns.matches("/numbers/1")).isFalse();
    }

    PathPatterns pathPatterns(String... uris) {
        return new PathPatternsForMethod(uris);
    }
}
