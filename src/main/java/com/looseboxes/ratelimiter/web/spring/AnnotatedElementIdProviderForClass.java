package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.annotation.AnnotatedElementIdProvider;
import com.looseboxes.ratelimiter.web.core.PathPatterns;
import org.springframework.web.bind.annotation.RequestMapping;

public class AnnotatedElementIdProviderForClass implements AnnotatedElementIdProvider<Class<?>, PathPatterns<String>> {

    public AnnotatedElementIdProviderForClass() { }

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

        return new PathPatternsForClass(paths);
    }
}
