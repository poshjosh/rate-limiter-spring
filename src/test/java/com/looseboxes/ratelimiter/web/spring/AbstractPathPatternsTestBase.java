package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.web.core.PathPatterns;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

abstract class AbstractPathPatternsTestBase {

    abstract PathPatterns pathPatterns(String... uris);

    @Test
    void shouldCombine() {
         PathPatterns  pathPatterns = pathPatterns("/numbers");
         PathPatterns result =  pathPatterns.combine(pathPatterns("/1/**", "/2/*"));
         PathPatterns expected = pathPatterns("/numbers/1/**", "/numbers/2/*");
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldMatchSingleAsterix() {
         PathPatterns  pathPatterns = pathPatterns("/*");
        assertThat( pathPatterns.matches("/numbers")).isTrue();
        assertThat( pathPatterns.matches("/numbers/1")).isFalse();
    }

    @Test
    void shouldMatchDoubleAsterix() {
         PathPatterns  pathPatterns = pathPatterns("/**");
        assertThat( pathPatterns.matches("/numbers")).isTrue();
        assertThat( pathPatterns.matches("/numbers/1")).isTrue();
    }
}