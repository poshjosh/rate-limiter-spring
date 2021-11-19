package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.web.core.PathPatterns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.*;

public class PathPatternsForClass implements PathPatterns<String> {

    private static final Logger LOG = LoggerFactory.getLogger(PathPatternsForClass.class);

    private final PathPattern [] pathPatterns;

    private final PathPatternParser pathPatternParser;

    public PathPatternsForClass(String... pathPatterns) {
        this.pathPatternParser = new PathPatternParser();
        this.pathPatterns = new PathPattern[pathPatterns.length];
        for(int i = 0; i<pathPatterns.length; i++) {
            this.pathPatterns[i] = pathPatternParser.parse(pathPatterns[i]);
        }
        LOG.trace("Path patterns: {}", Arrays.toString(pathPatterns));
    }

    public PathPatternsForClass(PathPattern... pathPatterns) {
        this.pathPatterns = Objects.requireNonNull(pathPatterns);
        this.pathPatternParser = new PathPatternParser();
    }

    @Override
    public List<String> getPathPatterns() {
        final List<String> result = new ArrayList<>(pathPatterns.length);
        for(PathPattern pathPattern : pathPatterns) {
            result.add(pathPattern.getPatternString());
        }
        return result.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(result);
    }

    public PathPatterns<String> combine(PathPatterns<String> other) {
        return new PathPatternsForClass(Util.composePathPatterns(pathPatternParser, pathPatterns, other.getPathPatterns()));
    }

    @Override
    public boolean matches(String uri) {
        if(LOG.isTraceEnabled()) {
            LOG.trace("Checking if: {}, matches start of any: {}", uri, Arrays.toString(pathPatterns));
        }
        final PathContainer pathContainer = pathContainer(uri);
        for(PathPattern pathPattern : pathPatterns) {
            if(pathPattern.matchStartOfPath(pathContainer) != null) {
                LOG.trace("Matches start: true, uri: {}, pathPattern: {}", uri, pathPattern);
                return true;
            }
        }
        LOG.trace("Matches start: false, uri: {}", uri);
        return false;
    }

    private PathContainer pathContainer(String uri) {
        return PathContainer.parsePath(uri, PathContainer.Options.HTTP_PATH);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathPatternsForClass that = (PathPatternsForClass) o;
        return Arrays.equals(pathPatterns, that.pathPatterns);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(pathPatterns);
    }

    @Override
    public String toString() {
        return "PathPatternsForClass{" +
                "pathPatterns=" + Arrays.toString(pathPatterns) +
                '}';
    }
}
