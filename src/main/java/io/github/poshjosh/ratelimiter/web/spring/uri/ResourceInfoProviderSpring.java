package io.github.poshjosh.ratelimiter.web.spring.uri;

import io.github.poshjosh.ratelimiter.model.RateSource;
import io.github.poshjosh.ratelimiter.web.core.util.PathPatterns;
import io.github.poshjosh.ratelimiter.web.core.util.ResourceInfoProvider;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class ResourceInfoProviderSpring implements ResourceInfoProvider {

    public ResourceInfoProviderSpring() { }

    @Override
    public ResourceInfoProvider.ResourceInfo get(RateSource source) {
        if (source.isOwnDeclarer()) {
            return getClassResourceInfo(source).orElse(ResourceInfo.NONE);
        }
        return getMethodResourceInfo(source);
    }

    private Optional<ResourceInfo> getClassResourceInfo(RateSource source) {

        final RequestMapping requestMapping = source.getAnnotation(RequestMapping.class).orElse(null);

        if(requestMapping == null) {
            return Optional.empty();
        }

        String [] paths = requestMapping.value();

        if(paths.length == 0) {
            paths = requestMapping.path();
        }

        final String [] methods = getMethods(requestMapping);

        if(paths.length == 0) {
            return Optional.of(ResourceInfo.of(methods));
        }

        return Optional.of(ResourceInfo.of(new ClassLevelPathPatterns(paths), methods));
    }

    private ResourceInfo getMethodResourceInfo(RateSource source) {

        final ResourceInfo classResourceInfo = source.getDeclarer()
                .flatMap(this::getClassResourceInfo).orElse(ResourceInfo.NONE);

        final PathPatterns<String> classLevelPathPatterns = classResourceInfo.getPathPatterns();

        GetMapping getMapping = source.getAnnotation(GetMapping.class).orElse(null);
        if (getMapping != null) {
            String [] methodLevelPathPatterns = selectPatterns(getMapping.value(), getMapping.path());
            return ResourceInfo.of(buildPathPatterns(classLevelPathPatterns, methodLevelPathPatterns), "GET");
        }

        PostMapping postMapping = source.getAnnotation(PostMapping.class).orElse(null);
        if (postMapping != null) {
            String [] methodLevelPathPatterns = selectPatterns(postMapping.value(), postMapping.path());
            return ResourceInfo.of(buildPathPatterns(classLevelPathPatterns, methodLevelPathPatterns), "POST");
        }

        PutMapping putMapping = source.getAnnotation(PutMapping.class).orElse(null);
        if (putMapping != null) {
            String [] methodLevelPathPatterns = selectPatterns(putMapping.value(), putMapping.path());
            return ResourceInfo.of(buildPathPatterns(classLevelPathPatterns, methodLevelPathPatterns), "PUT");
        }

        DeleteMapping deleteMapping = source.getAnnotation(DeleteMapping.class).orElse(null);
        if (deleteMapping != null) {
            String [] methodLevelPathPatterns = selectPatterns(deleteMapping.value(), deleteMapping.path());
            return ResourceInfo.of(buildPathPatterns(classLevelPathPatterns, methodLevelPathPatterns), "DELETE");
        }

        PatchMapping patchMapping = source.getAnnotation(PatchMapping.class).orElse(null);
        if (patchMapping != null) {
            String [] methodLevelPathPatterns = selectPatterns(patchMapping.value(), patchMapping.path());
            return ResourceInfo.of(buildPathPatterns(classLevelPathPatterns, methodLevelPathPatterns), "PATCH");
        }

        RequestMapping requestMapping = source.getAnnotation(RequestMapping.class).orElse(null);
        if (requestMapping != null) {
            String [] methodLevelPathPatterns = selectPatterns(requestMapping.value(), requestMapping.path());
            return ResourceInfo.of(
                    buildPathPatterns(classLevelPathPatterns, methodLevelPathPatterns),
                    getMethods(requestMapping));
        }

        return classResourceInfo;
    }

    private String [] getMethods(RequestMapping requestMapping) {
        return Arrays.stream(requestMapping.method())
                .map(Enum::name)
                .collect(Collectors.toList()).toArray(new String[0]);
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
