package com.looseboxes.ratelimiter.spring.util;

import com.looseboxes.ratelimiter.util.ClassFilter;

import java.lang.annotation.Annotation;
import java.util.Objects;

public class ClassFilterForAnnotation implements ClassFilter {

    private final Class<? extends Annotation> annotationClass;

    public ClassFilterForAnnotation(Class<? extends Annotation> annotationClass) {
        this.annotationClass = Objects.requireNonNull(annotationClass);
    }

    @Override
    public boolean test(Class<?> aClass) {
        return aClass.getAnnotation(annotationClass) != null;
    }
}
