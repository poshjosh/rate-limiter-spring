package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.web.core.PathPatterns;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MethodLevelPathPatternsTest extends AbstractPathPatternsTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(MethodLevelPathPatternsTest.class);

    @Test
    public void shouldMatchSinglePathVariable() {
        LOG.debug("#shouldMatchSinglePathVariable()");
        PathPatterns<String> pathPatterns = pathPatterns("/{id}");
        assertThat( pathPatterns.matches("/1")).isTrue();
        assertThat( pathPatterns.matches("/1/fake")).isFalse();
    }

    @Test
    public void shouldMatchMultiplePathVariables() {
        LOG.debug("#shouldMatchSinglePathVariable()");
        PathPatterns<String> pathPatterns = pathPatterns("/before/{id}/{name}");
        assertThat( pathPatterns.matches("/before/1/jane")).isTrue();
        assertThat( pathPatterns.matches("/1/jane")).isFalse();
        assertThat( pathPatterns.matches("/before/1")).isFalse();
        assertThat( pathPatterns.matches("/before/1/jane/fake")).isFalse();
    }

    @Test
    public void shouldMatchSingleAsterix() {
        LOG.debug("#shouldMatchSingleAsterix()");
        PathPatterns<String> pathPatterns = pathPatterns("/*");
        assertThat( pathPatterns.matches("/")).isTrue();
        assertThat( pathPatterns.matches("/numbers")).isTrue();
        assertThat( pathPatterns.matches("/numbers/1")).isFalse();
    }

    @Test
    public void shouldMatchSingleQuestionMark() {
        LOG.debug("#shouldMatchSingleQuestionMark()");
        PathPatterns<String> pathPatterns = pathPatterns("/?");
        assertThat( pathPatterns.matches("/a")).isTrue();
        assertThat( pathPatterns.matches("/")).isFalse();
        assertThat( pathPatterns.matches("/numbers")).isFalse();
    }

    PathPatterns<String> pathPatterns(String... uris) {
        return new MethodLevelPathPatterns(uris);
    }
}
