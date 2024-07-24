package io.github.poshjosh.ratelimiter.web.spring.uri;

import io.github.poshjosh.ratelimiter.web.core.util.ResourcePath;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MethodLevelResourcePathTest extends AbstractResourcePathTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(MethodLevelResourcePathTest.class);

    @Test
    public void shouldMatchSinglePathVariable() {
        LOG.debug("#shouldMatchSinglePathVariable()");
        ResourcePath resourcePath = givenResourcePath("/{id}");
        assertThat( resourcePath.matches("/1")).isTrue();
        assertThat( resourcePath.matches("/1/fake")).isFalse();
    }

    @Test
    public void shouldMatchMultiplePathVariables() {
        LOG.debug("#shouldMatchSinglePathVariable()");
        ResourcePath resourcePath = givenResourcePath("/before/{id}/{name}");
        assertThat( resourcePath.matches("/before/1/jane")).isTrue();
        assertThat( resourcePath.matches("/1/jane")).isFalse();
        assertThat( resourcePath.matches("/before/1")).isFalse();
        assertThat( resourcePath.matches("/before/1/jane/fake")).isFalse();
    }

    @Test
    public void shouldMatchSingleAsterix() {
        LOG.debug("#shouldMatchSingleAsterix()");
        ResourcePath resourcePath = givenResourcePath("/*");
        assertThat( resourcePath.matches("/")).isTrue();
        assertThat( resourcePath.matches("/numbers")).isTrue();
        assertThat( resourcePath.matches("/numbers/1")).isFalse();
    }

    @Test
    public void shouldMatchSingleQuestionMark() {
        LOG.debug("#shouldMatchSingleQuestionMark()");
        ResourcePath resourcePath = givenResourcePath("/?");
        assertThat( resourcePath.matches("/a")).isTrue();
        assertThat( resourcePath.matches("/")).isFalse();
        assertThat( resourcePath.matches("/numbers")).isFalse();
    }

    ResourcePath givenResourcePath(String... uris) {
        return new MethodLevelResourcePath(uris);
    }
}
