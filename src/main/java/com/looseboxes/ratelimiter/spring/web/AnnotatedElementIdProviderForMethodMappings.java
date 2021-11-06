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
            AnnotatedRequestMapping mapping = buildPathPatterns(annotatedRequestMapping, method.getAnnotation(GetMapping.class).value());
            if(mapping == AnnotatedRequestMapping.NONE) {
                mapping = buildPathPatterns(annotatedRequestMapping, method.getAnnotation(GetMapping.class).path());
            }
            return mapping;
        }

        if (method.getAnnotation(PostMapping.class) != null) {
            AnnotatedRequestMapping mapping = buildPathPatterns(annotatedRequestMapping, method.getAnnotation(PostMapping.class).value());
            if(mapping == AnnotatedRequestMapping.NONE) {
                mapping = buildPathPatterns(annotatedRequestMapping, method.getAnnotation(PostMapping.class).path());
            }
            return mapping;
        }

        if (method.getAnnotation(PutMapping.class) != null) {
            AnnotatedRequestMapping mapping = buildPathPatterns(annotatedRequestMapping, method.getAnnotation(PutMapping.class).value());
            if(mapping == AnnotatedRequestMapping.NONE) {
                mapping = buildPathPatterns(annotatedRequestMapping, method.getAnnotation(PutMapping.class).path());
            }
            return mapping;
        }

        if (method.getAnnotation(DeleteMapping.class) != null) {
            AnnotatedRequestMapping mapping = buildPathPatterns(annotatedRequestMapping, method.getAnnotation(DeleteMapping.class).value());
            if(mapping == AnnotatedRequestMapping.NONE) {
                mapping = buildPathPatterns(annotatedRequestMapping, method.getAnnotation(DeleteMapping.class).path());
            }
            return mapping;
        }

        if (method.getAnnotation(PatchMapping.class) != null) {
            AnnotatedRequestMapping mapping = buildPathPatterns(annotatedRequestMapping, method.getAnnotation(PatchMapping.class).value());
            if(mapping == AnnotatedRequestMapping.NONE) {
                mapping = buildPathPatterns(annotatedRequestMapping, method.getAnnotation(PatchMapping.class).path());
            }
            return mapping;
        }

        if (method.getAnnotation(RequestMapping.class) != null) {
            AnnotatedRequestMapping mapping = buildPathPatterns(annotatedRequestMapping, method.getAnnotation(RequestMapping.class).value());
            if(mapping == AnnotatedRequestMapping.NONE) {
                mapping = buildPathPatterns(annotatedRequestMapping, method.getAnnotation(RequestMapping.class).path());
            }
            return mapping;
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
