package com.looseboxes.ratelimiter.spring.web;

import org.springframework.http.server.PathContainer;
import org.springframework.util.StringUtils;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.*;

public class AnnotatedRequestMappingImpl implements AnnotatedRequestMapping {

    private final PathPattern [] pathPatterns;

    private final PathPatternParser pathPatternParser;

    public AnnotatedRequestMappingImpl(String... pathPatterns) {
        this.pathPatternParser = new PathPatternParser();
        this.pathPatterns = new PathPattern[pathPatterns.length];
        for(int i = 0; i<pathPatterns.length; i++) {
            this.pathPatterns[i] = pathPatternParser.parse(pathPatterns[i]);
        }
    }

    public AnnotatedRequestMappingImpl(PathPattern... pathPatterns) {
        this.pathPatterns = Objects.requireNonNull(pathPatterns);
        this.pathPatternParser = new PathPatternParser();
    }

    public AnnotatedRequestMapping combine(String... uris) {
        return new AnnotatedRequestMappingImpl(buildPathPatterns(pathPatterns, uris));
    }

    private PathPattern [] buildPathPatterns(PathPattern [] basePathPatterns, String... subPathPatterns) {
        if(subPathPatterns == null || subPathPatterns.length == 0) {
            return new PathPattern[0];
        }
        final List<PathPattern> result = new ArrayList<>(basePathPatterns.length * subPathPatterns.length);
        for(String subPath : subPathPatterns) {
            if(StringUtils.hasText(subPath)) {
                PathPattern pathPattern = pathPatternParser.parse(subPath);
                for(PathPattern basePath : basePathPatterns) {
                    result.add(basePath.combine(pathPattern));
                }
            }
        }
        return result.isEmpty() ? new PathPattern[0] : result.toArray(new PathPattern[0]);
    }

    @Override
    public boolean matches(String uri) {
        final PathContainer pathContainer = pathContainer(uri);
        for(PathPattern pathPattern : pathPatterns) {
            if(pathPattern.matches(pathContainer)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean matchesStartOf(String uri) {
        final PathContainer pathContainer = pathContainer(uri);
        for(PathPattern pathPattern : pathPatterns) {
            if(pathPattern.matchStartOfPath(pathContainer) != null) {
                return true;
            }
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
        AnnotatedRequestMappingImpl that = (AnnotatedRequestMappingImpl) o;
        for(int i=0; i<pathPatterns.length; i++) {
            if(!pathPatterns[i].equals(that.pathPatterns[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pathPatterns);
    }

    @Override
    public String toString() {
        return "AnnotatedRequestMappingImpl{" +
                "pathPatterns=" + Arrays.toString(pathPatterns) +
                '}';
    }
}
