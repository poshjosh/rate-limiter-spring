package io.github.poshjosh.ratelimiter.web.spring.uri;

import io.github.poshjosh.ratelimiter.web.core.util.ResourcePath;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

abstract class AbstractResourcePathTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractResourcePathTestBase.class);

    abstract ResourcePath<String> givenResourcePath(String... uris);

    @Test
    public void shouldCombine() {
        LOG.debug("shouldCombine()");
        ResourcePath<String> resourcePath = givenResourcePath("/numbers");
        ResourcePath<String> result =  resourcePath.combine(givenResourcePath("/1/**", "/2/*"));
        ResourcePath<String> expected = givenResourcePath("/numbers/1/**", "/numbers/2/*");
        assertThat(result.getPatterns()).isEqualTo(expected.getPatterns());
    }

    @Test
    public void shouldMatchDoubleAsterix() {
        LOG.debug("shouldMatchDoubleAsterix()");
        ResourcePath<String> resourcePath = givenResourcePath("/**");
        assertThat( resourcePath.matches("/numbers")).isTrue();
        assertThat( resourcePath.matches("/numbers/1")).isTrue();
    }
}