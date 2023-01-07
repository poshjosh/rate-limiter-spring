package io.github.poshjosh.ratelimiter.web.spring;

import io.github.poshjosh.ratelimiter.web.core.RequestToIdConverter;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterConfig;
import io.github.poshjosh.ratelimiter.web.spring.uri.PathPatternsProviderSpring;

import javax.servlet.http.HttpServletRequest;

public abstract class ResourceLimiterConfigSpring
        extends ResourceLimiterConfig<HttpServletRequest> {

    private static class RequestToIdConverterSpring
            implements RequestToIdConverter<HttpServletRequest, String> {
        private RequestToIdConverterSpring() {}
        @Override
        public String convert(HttpServletRequest request) {
            return request.getRequestURI();
        }
    }

    public static Builder<HttpServletRequest> builder() {
        return ResourceLimiterConfig.<HttpServletRequest>builder()
            .pathPatternsProvider(new PathPatternsProviderSpring())
            .requestToIdConverter(new RequestToIdConverterSpring())
            .classesInPackageFinder(new ClassesInPackageFinderSpring());

    }
}
