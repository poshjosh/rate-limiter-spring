package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.web.core.WebResourceLimiterConfig;
import com.looseboxes.ratelimiter.web.spring.uri.SpringPathPatternsProvider;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

public abstract class WebResourceLimiterConfigSpring
        extends WebResourceLimiterConfig<HttpServletRequest> {

    public static Builder<HttpServletRequest> builder() {
        return WebResourceLimiterConfig.<HttpServletRequest>builder()
            .requestToIdConverter(new RequestToUriConverter())
            .classesInPackageFinder(new ClassesInPackageFinderSpring())
            .pathPatternsProvider(new SpringPathPatternsProvider())
            .resourceAnnotationTypes(new Class[]{ Controller.class, RestController.class });
    }
}
