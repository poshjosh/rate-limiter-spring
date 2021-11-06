package com.looseboxes.ratelimiter.spring.util;

import com.looseboxes.ratelimiter.util.ClassFilter;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ClassFilterForAnnotations implements ClassFilter {

    private final List<Class<? extends Annotation>> annotationClassList;

    public ClassFilterForAnnotations(Class<? extends Annotation>... annotationClasses) {
        this.annotationClassList = Arrays.asList(annotationClasses);
    }

    @Override
    public boolean test(Class<?> aClass) {
        return annotationClassList.stream()
                .anyMatch(annotationClass -> aClass.getAnnotation(annotationClass) != null);
    }
}
