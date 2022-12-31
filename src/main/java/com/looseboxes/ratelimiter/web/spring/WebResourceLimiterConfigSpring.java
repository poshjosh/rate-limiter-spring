package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.web.core.WebResourceLimiterConfig;
import com.looseboxes.ratelimiter.web.core.impl.WebResourceLimiterConfigBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

public interface WebResourceLimiterConfigSpring
        extends WebResourceLimiterConfig<HttpServletRequest> {

    final class WebResourceLimiterConfigBuilderSpring extends
            WebResourceLimiterConfigBuilder<HttpServletRequest> {
        public WebResourceLimiterConfigBuilderSpring() {
            requestToIdConverter(new RequestToUriConverter());
            classesInPackageFinder(new ClassesInPackageFinderSpring());
            classPathPatternsProvider(new ClassPathPatternsProvider());
            methodPathPatternsProvider(new MethodPathPatternsProvider());
            resourceAnnotationTypes(new Class[]{ Controller.class, RestController.class });
        }
    }

    static Builder<HttpServletRequest> builder() {
        return new WebResourceLimiterConfigBuilderSpring();
    }
}
