package com.looseboxes.ratelimiter.web.spring.uri;

import com.looseboxes.ratelimiter.web.core.util.PathPatterns;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ClassLevelPathPatternsTest extends AbstractPathPatternsTestBase {

    @Test
    public void shouldMatchStartOf() {
        PathPatterns<String> pathPatterns = pathPatterns("/numbers");
        assertThat( pathPatterns.matches("/numbers")).isTrue();
        assertThat( pathPatterns.matches("/numbers/1")).isTrue();
        assertThat( pathPatterns.matches("/letters/a")).isFalse();
    }

    PathPatterns<String> pathPatterns(String... uris) {
        return new ClassLevelPathPatterns(uris);
    }
}
