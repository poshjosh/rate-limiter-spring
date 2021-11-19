package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.web.core.PathPatterns;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PathPatternsForMethodTest extends AbstractPathPatternsTestBase {

    PathPatterns pathPatterns(String... uris) {
        return new PathPatternsForMethod(uris);
    }
}
