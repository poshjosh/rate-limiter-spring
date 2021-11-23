package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.annotation.AnnotatedElementIdProvider;
import com.looseboxes.ratelimiter.web.core.PathPatterns;
import org.springframework.web.bind.annotation.*;
import java.lang.reflect.Method;
import java.util.*;

public class MethodIdProvider implements AnnotatedElementIdProvider<Method, PathPatterns<String>> {

    private final AnnotatedElementIdProvider<Class<?>, PathPatterns<String>> classIdProvider;

    public MethodIdProvider(AnnotatedElementIdProvider<Class<?>, PathPatterns<String>> classIdProvider) {
        this.classIdProvider = Objects.requireNonNull(classIdProvider);
    }

    @Override
    public PathPatterns<String> getId(Method method) {

        final PathPatterns<String> classLevelPathPatterns = classIdProvider.getId(method.getDeclaringClass());

        if (method.getAnnotation(GetMapping.class) != null) {
            PathPatterns<String> mapping = buildPathPatterns(classLevelPathPatterns, method.getAnnotation(GetMapping.class).value());
            if(isNone(mapping)) {
                mapping =  buildPathPatterns(classLevelPathPatterns, method.getAnnotation(GetMapping.class).path());
            }
            return mapping;
        }

        if (method.getAnnotation(PostMapping.class) != null) {
            PathPatterns<String> mapping =  buildPathPatterns(classLevelPathPatterns, method.getAnnotation(PostMapping.class).value());
            if(isNone(mapping)) {
                mapping =  buildPathPatterns(classLevelPathPatterns, method.getAnnotation(PostMapping.class).path());
            }
            return mapping;
        }

        if (method.getAnnotation(PutMapping.class) != null) {
            PathPatterns<String> mapping =  buildPathPatterns(classLevelPathPatterns, method.getAnnotation(PutMapping.class).value());
            if(isNone(mapping)) {
                mapping =  buildPathPatterns(classLevelPathPatterns, method.getAnnotation(PutMapping.class).path());
            }
            return mapping;
        }

        if (method.getAnnotation(DeleteMapping.class) != null) {
            PathPatterns<String> mapping =  buildPathPatterns(classLevelPathPatterns, method.getAnnotation(DeleteMapping.class).value());
            if(isNone(mapping)) {
                mapping =  buildPathPatterns(classLevelPathPatterns, method.getAnnotation(DeleteMapping.class).path());
            }
            return mapping;
        }

        if (method.getAnnotation(PatchMapping.class) != null) {
            PathPatterns<String> mapping =  buildPathPatterns(classLevelPathPatterns, method.getAnnotation(PatchMapping.class).value());
            if(isNone(mapping)) {
                mapping =  buildPathPatterns(classLevelPathPatterns, method.getAnnotation(PatchMapping.class).path());
            }
            return mapping;
        }

        if (method.getAnnotation(RequestMapping.class) != null) {
            PathPatterns<String> mapping =  buildPathPatterns(classLevelPathPatterns, method.getAnnotation(RequestMapping.class).value());
            if(isNone(mapping)) {
                mapping =  buildPathPatterns(classLevelPathPatterns, method.getAnnotation(RequestMapping.class).path());
            }
            return mapping;
        }

        return classLevelPathPatterns;
    }

    private <K> boolean isNone(PathPatterns<K> pathPatterns) {
        return PathPatterns.none().equals(pathPatterns);
    }

    private PathPatterns<String> buildPathPatterns(PathPatterns<String> classLevelPathPatterns, String [] subPathPatterns) {
        if(subPathPatterns == null || subPathPatterns.length == 0) {
            return PathPatterns.none();
        }
        return classLevelPathPatterns.combine(new MethodLevelPathPatterns(subPathPatterns));
    }
}
