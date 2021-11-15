package com.looseboxes.ratelimiter.spring.web;

import com.looseboxes.ratelimiter.annotation.AnnotatedElementIdProvider;
import org.springframework.web.bind.annotation.RequestMapping;

public class AnnotatedElementIdProviderForRequestMapping implements AnnotatedElementIdProvider<Class<?>, AnnotatedRequestMapping> {

    public AnnotatedElementIdProviderForRequestMapping() { }

    @Override
    public AnnotatedRequestMapping getId(Class<?> source) {

        final RequestMapping requestAnnotation = source.getAnnotation(RequestMapping.class);

        if(requestAnnotation == null) {
            return AnnotatedRequestMapping.NONE;
        }

        String [] paths = requestAnnotation.value();

        if(paths == null || paths.length == 0) {

            paths = requestAnnotation.path();
        }

        if(paths == null || paths.length == 0) {

            return AnnotatedRequestMapping.NONE;
        }

        return new AnnotatedRequestMappingImpl(paths);
    }
}
