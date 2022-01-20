package com.looseboxes.ratelimiter.web.spring.uri;

import org.springframework.util.StringUtils;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.ArrayList;
import java.util.List;

class Util {
    static PathPattern[] composePathPatterns(PathPatternParser pathPatternParser, PathPattern [] basePathPatterns, List<String> subPathPatterns) {
        if(subPathPatterns == null || subPathPatterns.isEmpty()) {
            PathPattern [] result = new PathPattern[basePathPatterns.length];
            System.arraycopy(basePathPatterns, 0, result, 0, basePathPatterns.length);
            return result;
        }
        final List<PathPattern> result = new ArrayList<>(basePathPatterns.length * subPathPatterns.size());
        for(String subPath : subPathPatterns) {
            PathPattern sub = StringUtils.hasText(subPath) ? pathPatternParser.parse(subPath) : null;
            for(PathPattern basePath : basePathPatterns) {
                result.add(sub == null ? basePath : basePath.combine(sub));
            }
        }
        return result.isEmpty() ? new PathPattern[0] : result.toArray(new PathPattern[0]);
    }
}
