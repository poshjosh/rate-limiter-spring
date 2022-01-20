package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.annotation.IdProvider;
import com.looseboxes.ratelimiter.web.core.util.PathPatterns;
import com.looseboxes.ratelimiter.web.spring.uri.MethodLevelPathPatterns;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.*;

public class MethodPathPatternsProvider implements IdProvider<Method, PathPatterns<String>> {

    private final IdProvider<Class<?>, PathPatterns<String>> classIdProvider;

    public MethodPathPatternsProvider() {
        this(new ClassPathPatternsProvider());
    }

    public MethodPathPatternsProvider(IdProvider<Class<?>, PathPatterns<String>> classIdProvider) {
        this.classIdProvider = Objects.requireNonNull(classIdProvider);
    }

    @Override
    public PathPatterns<String> getId(Method method) {

        final PathPatterns<String> classLevelPathPatterns = classIdProvider.getId(method.getDeclaringClass());

        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        if (getMapping != null) {
            String [] methodLevelPathPatterns = selectPatterns(getMapping.value(), getMapping.path());
            return buildPathPatterns(classLevelPathPatterns, methodLevelPathPatterns);
        }

        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        if (postMapping != null) {
            String [] methodLevelPathPatterns = selectPatterns(postMapping.value(), postMapping.path());
            return buildPathPatterns(classLevelPathPatterns, methodLevelPathPatterns);
        }

        PutMapping putMapping = method.getAnnotation(PutMapping.class);
        if (putMapping != null) {
            String [] methodLevelPathPatterns = selectPatterns(putMapping.value(), putMapping.path());
            return buildPathPatterns(classLevelPathPatterns, methodLevelPathPatterns);
        }

        DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
        if (deleteMapping != null) {
            String [] methodLevelPathPatterns = selectPatterns(deleteMapping.value(), deleteMapping.path());
            return buildPathPatterns(classLevelPathPatterns, methodLevelPathPatterns);
        }

        PatchMapping patchMapping = method.getAnnotation(PatchMapping.class);
        if (patchMapping != null) {
            String [] methodLevelPathPatterns = selectPatterns(patchMapping.value(), patchMapping.path());
            return buildPathPatterns(classLevelPathPatterns, methodLevelPathPatterns);
        }

        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            String [] methodLevelPathPatterns = selectPatterns(requestMapping.value(), requestMapping.path());
            return buildPathPatterns(classLevelPathPatterns, methodLevelPathPatterns);
        }

        return classLevelPathPatterns;
    }

    private String [] selectPatterns(String [] subPathPatterns1, String [] subPathPatterns2) {
        if(subPathPatterns1.length == 0) {
            return subPathPatterns2;
        }
        return subPathPatterns1;
    }

    private PathPatterns<String> buildPathPatterns(PathPatterns<String> classLevelPathPatterns, String [] subPathPatterns) {
        if(subPathPatterns == null || subPathPatterns.length == 0) {
            return classLevelPathPatterns;
        }
        return classLevelPathPatterns.combine(new MethodLevelPathPatterns(subPathPatterns));
    }
}
