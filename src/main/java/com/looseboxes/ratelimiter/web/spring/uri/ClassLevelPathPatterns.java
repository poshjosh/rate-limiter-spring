package com.looseboxes.ratelimiter.web.spring.uri;

import com.looseboxes.ratelimiter.web.core.util.PathPatterns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.*;
import java.util.stream.Collectors;

public class ClassLevelPathPatterns implements PathPatterns<String> {

    private static final Logger LOG = LoggerFactory.getLogger(ClassLevelPathPatterns.class);

    private final PathPattern [] pathPatterns;
    private final List<String> stringPatterns;

    private final PathPatternParser pathPatternParser;

    public ClassLevelPathPatterns(String... pathPatterns) {
        this.pathPatternParser = new PathPatternParser();
        this.pathPatterns = new PathPattern[pathPatterns.length];
        for(int i = 0; i<pathPatterns.length; i++) {
            this.pathPatterns[i] = pathPatternParser.parse(pathPatterns[i]);
        }
        this.stringPatterns = Arrays.asList(pathPatterns);
        LOG.trace("Path patterns: {}", stringPatterns);
    }

    ClassLevelPathPatterns(PathPattern... pathPatterns) {
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
        // issue #001 For now Parent patterns must always return a child type from the combine method
        return new MethodLevelPathPatterns(Util.composePathPatterns(pathPatternParser, pathPatterns, other.getPatterns()));
    }

    @Override
    public boolean matches(String uri) {
        if(LOG.isTraceEnabled()) {
            LOG.trace("Checking if: {}, matches start of any: {}", uri, Arrays.toString(pathPatterns));
        }
        final PathContainer pathContainer = pathContainer(uri);
        for(PathPattern pathPattern : pathPatterns) {
            if(pathPattern.matchStartOfPath(pathContainer) != null) {
                LOG.trace("Matches start: true, uri: {}, path pattern: {}", uri, pathPattern);
                return true;
            }
        }
        if(LOG.isInfoEnabled()) {
            LOG.trace("Matches start: false, uri: {}, path patterns:{} ", uri, Arrays.toString(pathPatterns));
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
        ClassLevelPathPatterns that = (ClassLevelPathPatterns) o;
        return Arrays.equals(pathPatterns, that.pathPatterns);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(pathPatterns);
    }

    @Override
    public String toString() {
        return "ClassLevelPathPatterns{" + Arrays.toString(pathPatterns) + '}';
    }
}
