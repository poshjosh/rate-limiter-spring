package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.annotation.IdProvider;
import com.looseboxes.ratelimiter.web.core.util.PathPatterns;
import com.looseboxes.ratelimiter.web.spring.uri.ClassLevelPathPatterns;
import org.springframework.web.bind.annotation.RequestMapping;

public class ClassIdProvider implements IdProvider<Class<?>, PathPatterns<String>> {

    public ClassIdProvider() { }

    @Override
    public PathPatterns<String> getId(Class<?> source) {

        final RequestMapping requestAnnotation = source.getAnnotation(RequestMapping.class);

        if(requestAnnotation == null) {
            return PathPatterns.none();
        }

        String [] paths = requestAnnotation.value();

        if(paths.length == 0) {

            paths = requestAnnotation.path();
        }

        if(paths.length == 0) {

            return PathPatterns.none();
        }

        return new ClassLevelPathPatterns(paths);
    }
}
