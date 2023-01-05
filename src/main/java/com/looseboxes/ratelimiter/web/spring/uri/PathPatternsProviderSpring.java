package com.looseboxes.ratelimiter.web.spring.uri;

import com.looseboxes.ratelimiter.annotation.Element;
import com.looseboxes.ratelimiter.web.core.util.PathPatterns;
import com.looseboxes.ratelimiter.web.core.util.PathPatternsProvider;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

public class PathPatternsProviderSpring implements PathPatternsProvider {

    @Override
    public PathPatterns<String> get(Element source) {
        if (source.isOwnDeclarer()) {
            return getClassPatterns(source).orElse(PathPatterns.none());
        }
        return getMethodPatterns(source);
    }

    private Optional<PathPatterns<String>> getClassPatterns(Element source) {

        final RequestMapping requestAnnotation = source.getAnnotation(RequestMapping.class).orElse(null);

        if(requestAnnotation == null) {
            return Optional.empty();
        }

        String [] paths = requestAnnotation.value();

        if(paths.length == 0) {

            paths = requestAnnotation.path();
        }

        if(paths.length == 0) {

            return Optional.of(PathPatterns.none());
        }

        return Optional.of(new ClassLevelPathPatterns(paths));
    }

    private PathPatterns<String> getMethodPatterns(Element source) {

        final PathPatterns<String> classLevelPathPatterns =
                getClassPatterns(source.getDeclarer()).orElse(PathPatterns.none());

        GetMapping getMapping = source.getAnnotation(GetMapping.class).orElse(null);
        if (getMapping != null) {
            String [] methodLevelPathPatterns = selectPatterns(getMapping.value(), getMapping.path());
            return buildPathPatterns(classLevelPathPatterns, methodLevelPathPatterns);
        }

        PostMapping postMapping = source.getAnnotation(PostMapping.class).orElse(null);
        if (postMapping != null) {
            String [] methodLevelPathPatterns = selectPatterns(postMapping.value(), postMapping.path());
            return buildPathPatterns(classLevelPathPatterns, methodLevelPathPatterns);
        }

        PutMapping putMapping = source.getAnnotation(PutMapping.class).orElse(null);
        if (putMapping != null) {
            String [] methodLevelPathPatterns = selectPatterns(putMapping.value(), putMapping.path());
            return buildPathPatterns(classLevelPathPatterns, methodLevelPathPatterns);
        }

        DeleteMapping deleteMapping = source.getAnnotation(DeleteMapping.class).orElse(null);
        if (deleteMapping != null) {
            String [] methodLevelPathPatterns = selectPatterns(deleteMapping.value(), deleteMapping.path());
            return buildPathPatterns(classLevelPathPatterns, methodLevelPathPatterns);
        }

        PatchMapping patchMapping = source.getAnnotation(PatchMapping.class).orElse(null);
        if (patchMapping != null) {
            String [] methodLevelPathPatterns = selectPatterns(patchMapping.value(), patchMapping.path());
            return buildPathPatterns(classLevelPathPatterns, methodLevelPathPatterns);
        }

        RequestMapping requestMapping = source.getAnnotation(RequestMapping.class).orElse(null);
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