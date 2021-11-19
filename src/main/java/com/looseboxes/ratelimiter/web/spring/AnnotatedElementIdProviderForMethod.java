package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.annotation.AnnotatedElementIdProvider;
import com.looseboxes.ratelimiter.web.core.PathPatterns;
import org.springframework.web.bind.annotation.*;
import java.lang.reflect.Method;
import java.util.*;

public class AnnotatedElementIdProviderForMethod implements AnnotatedElementIdProvider<Method, PathPatterns<String>> {

    private final AnnotatedElementIdProvider<Class<?>, PathPatterns<String>> annotatedElementIdProvider;

    public AnnotatedElementIdProviderForMethod(AnnotatedElementIdProvider<Class<?>, PathPatterns<String>> annotatedElementIdProvider) {
        this.annotatedElementIdProvider = Objects.requireNonNull(annotatedElementIdProvider);
    }

    @Override
    public PathPatterns<String> getId(Method method) {

        final PathPatterns<String> annotatedRequestMapping = annotatedElementIdProvider.getId(method.getDeclaringClass());

        if (method.getAnnotation(GetMapping.class) != null) {
            PathPatterns<String> mapping = buildPathPatterns(annotatedRequestMapping, method.getAnnotation(GetMapping.class).value());
            if(isNone(mapping)) {
                mapping =  buildPathPatterns(annotatedRequestMapping, method.getAnnotation(GetMapping.class).path());
            }
            return mapping;
        }

        if (method.getAnnotation(PostMapping.class) != null) {
            PathPatterns<String> mapping =  buildPathPatterns(annotatedRequestMapping, method.getAnnotation(PostMapping.class).value());
            if(isNone(mapping)) {
                mapping =  buildPathPatterns(annotatedRequestMapping, method.getAnnotation(PostMapping.class).path());
            }
            return mapping;
        }

        if (method.getAnnotation(PutMapping.class) != null) {
            PathPatterns<String> mapping =  buildPathPatterns(annotatedRequestMapping, method.getAnnotation(PutMapping.class).value());
            if(isNone(mapping)) {
                mapping =  buildPathPatterns(annotatedRequestMapping, method.getAnnotation(PutMapping.class).path());
            }
            return mapping;
        }

        if (method.getAnnotation(DeleteMapping.class) != null) {
            PathPatterns<String> mapping =  buildPathPatterns(annotatedRequestMapping, method.getAnnotation(DeleteMapping.class).value());
            if(isNone(mapping)) {
                mapping =  buildPathPatterns(annotatedRequestMapping, method.getAnnotation(DeleteMapping.class).path());
            }
            return mapping;
        }

        if (method.getAnnotation(PatchMapping.class) != null) {
            PathPatterns<String> mapping =  buildPathPatterns(annotatedRequestMapping, method.getAnnotation(PatchMapping.class).value());
            if(isNone(mapping)) {
                mapping =  buildPathPatterns(annotatedRequestMapping, method.getAnnotation(PatchMapping.class).path());
            }
            return mapping;
        }

        if (method.getAnnotation(RequestMapping.class) != null) {
            PathPatterns<String> mapping =  buildPathPatterns(annotatedRequestMapping, method.getAnnotation(RequestMapping.class).value());
            if(isNone(mapping)) {
                mapping =  buildPathPatterns(annotatedRequestMapping, method.getAnnotation(RequestMapping.class).path());
            }
            return mapping;
        }

        return annotatedRequestMapping;
    }

    private <K> boolean isNone(PathPatterns<K> pathPatterns) {
        return PathPatterns.none().equals(pathPatterns);
    }

    private PathPatterns<String> buildPathPatterns(PathPatterns<String> annotatedRequestMapping, String [] subPathPatterns) {
        if(subPathPatterns == null || subPathPatterns.length == 0) {
            return PathPatterns.none();
        }
        return annotatedRequestMapping.combine(new PathPatternsForMethod(subPathPatterns));
    }
}
