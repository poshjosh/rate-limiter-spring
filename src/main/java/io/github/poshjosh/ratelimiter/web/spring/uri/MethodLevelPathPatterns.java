package io.github.poshjosh.ratelimiter.web.spring.uri;

import io.github.poshjosh.ratelimiter.web.core.util.PathPatterns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.*;
import java.util.stream.Collectors;

final class MethodLevelPathPatterns implements PathPatterns<String> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodLevelPathPatterns.class);

    private final PathPattern [] pathPatterns;
    private final List<String> stringPatterns;

    private final PathPatternParser pathPatternParser;

    MethodLevelPathPatterns(String... pathPatterns) {
        this.pathPatternParser = new PathPatternParser();
        this.pathPatterns = new PathPattern[pathPatterns.length];
        for(int i = 0; i<pathPatterns.length; i++) {
            this.pathPatterns[i] = pathPatternParser.parse(pathPatterns[i]);
        }
        this.stringPatterns = Arrays.asList(pathPatterns);
        LOG.trace("Path patterns: {}", stringPatterns);
    }

    MethodLevelPathPatterns(PathPattern... pathPatterns) {
        this.pathPatternParser = new PathPatternParser();
        this.pathPatterns = Objects.requireNonNull(pathPatterns);
        this.stringPatterns = Arrays.stream(pathPatterns)
                .map(PathPattern::getPatternString)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getPatterns() {
        return stringPatterns;
    }

    public PathPatterns<String> combine(PathPatterns<String> other) {
        return new MethodLevelPathPatterns(Util.composePathPatterns(pathPatternParser, pathPatterns, other.getPatterns()));
    }

    @Override
    public boolean matches(String uri) {
        if(LOG.isTraceEnabled()) {
            LOG.trace("Checking if: {} matches any: {}", uri, Arrays.toString(pathPatterns));
        }
        final PathContainer pathContainer = pathContainer(uri);
        for(PathPattern pathPattern : pathPatterns) {
            if(pathPattern.matches(pathContainer)) {
                LOG.trace("Matches: true, uri: {}, pathPattern: {}", uri, pathPattern);
                return true;
            }
        }
        if(LOG.isTraceEnabled()) {
            LOG.trace("Matches: false, uri: {}, pathPatterns: {}", uri, Arrays.toString(pathPatterns));
        }
        return false;
    }

    private PathContainer pathContainer(String uri) {
        return PathContainer.parsePath(uri, PathContainer.Options.HTTP_PATH);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodLevelPathPatterns that = (MethodLevelPathPatterns) o;
        return Arrays.equals(pathPatterns, that.pathPatterns);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(pathPatterns);
    }

    @Override
    public String toString() {
        return "MethodLevelPathPatterns{" + Arrays.toString(pathPatterns) + '}';
    }
}
