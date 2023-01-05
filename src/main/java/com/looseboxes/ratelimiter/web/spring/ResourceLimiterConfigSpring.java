package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.web.core.RequestToIdConverter;
import com.looseboxes.ratelimiter.web.core.ResourceLimiterConfig;
import com.looseboxes.ratelimiter.web.spring.uri.PathPatternsProviderSpring;

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
