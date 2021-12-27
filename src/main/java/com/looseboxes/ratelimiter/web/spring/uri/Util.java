package com.looseboxes.ratelimiter.web.spring.uri;

import org.springframework.util.StringUtils;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.ArrayList;
import java.util.List;

class Util {
    static PathPattern[] composePathPatterns(PathPatternParser pathPatternParser, PathPattern [] basePathPatterns, List<String> subPathPatterns) {
        if(subPathPatterns == null || subPathPatterns.isEmpty()) {
            return new PathPattern[0];
        }
        final List<PathPattern> result = new ArrayList<>(basePathPatterns.length * subPathPatterns.size());
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
}
