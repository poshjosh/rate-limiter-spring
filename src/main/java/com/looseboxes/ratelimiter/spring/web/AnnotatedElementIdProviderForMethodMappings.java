package com.looseboxes.ratelimiter.spring.web;

import com.looseboxes.ratelimiter.annotation.AnnotatedElementIdProvider;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.*;

public class AnnotatedElementIdProviderForMethodMappings implements AnnotatedElementIdProvider<Method, AnnotatedRequestMapping> {

    private final AnnotatedElementIdProvider<Class<?>, AnnotatedRequestMapping> annotatedElementIdProvider;

    public AnnotatedElementIdProviderForMethodMappings(AnnotatedElementIdProvider<Class<?>, AnnotatedRequestMapping> annotatedElementIdProvider) {
        this.annotatedElementIdProvider = Objects.requireNonNull(annotatedElementIdProvider);
    }

    @Override
    public AnnotatedRequestMapping getId(Method method) {

        final AnnotatedRequestMapping annotatedRequestMapping = annotatedElementIdProvider.getId(method.getDeclaringClass());

        if (method.getAnnotation(GetMapping.class) != null) {
            return buildPathPatterns(annotatedRequestMapping, method.getAnnotation(GetMapping.class).path());
        }

        if (method.getAnnotation(PostMapping.class) != null) {
            return buildPathPatterns(annotatedRequestMapping, method.getAnnotation(PostMapping.class).path());
        }

        if (method.getAnnotation(PutMapping.class) != null) {
            return buildPathPatterns(annotatedRequestMapping, method.getAnnotation(PutMapping.class).path());
        }

        if (method.getAnnotation(DeleteMapping.class) != null) {
            return buildPathPatterns(annotatedRequestMapping, method.getAnnotation(DeleteMapping.class).path());
        }

        if (method.getAnnotation(PatchMapping.class) != null) {
            return buildPathPatterns(annotatedRequestMapping, method.getAnnotation(PatchMapping.class).path());
        }

        if (method.getAnnotation(RequestMapping.class) != null) {
            return buildPathPatterns(annotatedRequestMapping, method.getAnnotation(RequestMapping.class).path());
        }

        return annotatedRequestMapping;
    }

    private AnnotatedRequestMapping buildPathPatterns(AnnotatedRequestMapping annotatedRequestMapping, String [] subPathPatterns) {
        if(subPathPatterns == null || subPathPatterns.length == 0) {
            return AnnotatedRequestMapping.NONE;
        }
        return annotatedRequestMapping.combine(subPathPatterns);
    }
}
