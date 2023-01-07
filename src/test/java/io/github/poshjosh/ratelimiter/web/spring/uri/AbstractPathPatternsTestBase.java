package io.github.poshjosh.ratelimiter.web.spring.uri;

import io.github.poshjosh.ratelimiter.web.core.util.PathPatterns;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

abstract class AbstractPathPatternsTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPathPatternsTestBase.class);

    abstract PathPatterns<String> pathPatterns(String... uris);

    @Test
    public void shouldCombine() {
        LOG.debug("shouldCombine()");
        PathPatterns<String> pathPatterns = pathPatterns("/numbers");
        PathPatterns<String> result =  pathPatterns.combine(pathPatterns("/1/**", "/2/*"));
        PathPatterns<String> expected = pathPatterns("/numbers/1/**", "/numbers/2/*");
        assertThat(result.getPatterns()).isEqualTo(expected.getPatterns());
    }

    @Test
    public void shouldMatchDoubleAsterix() {
        LOG.debug("shouldMatchDoubleAsterix()");
        PathPatterns<String> pathPatterns = pathPatterns("/**");
        assertThat( pathPatterns.matches("/numbers")).isTrue();
        assertThat( pathPatterns.matches("/numbers/1")).isTrue();
    }
}