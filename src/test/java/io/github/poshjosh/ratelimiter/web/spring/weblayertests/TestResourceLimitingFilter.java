package io.github.poshjosh.ratelimiter.web.spring.weblayertests;

import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterConfigurer;
import io.github.poshjosh.ratelimiter.web.spring.RateLimitPropertiesSpring;
import io.github.poshjosh.ratelimiter.web.spring.ResourceLimitingFilter;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@TestComponent
public class TestResourceLimitingFilter extends ResourceLimitingFilter {

    public TestResourceLimitingFilter(
            RateLimitPropertiesSpring properties, ResourceLimiterConfigurer configurer) {
        super(properties, configurer);
        // Some test classes initialize resource class/packages as required
        // In which case we do not override
        if (properties.getResourceClasses().isEmpty() && properties.getResourcePackages().isEmpty()) {
            properties.setResourcePackages(
                    Collections.singletonList(AbstractResourceTest.class.getPackage().getName()));
        }
    }

    @Override
    protected void onLimitExceeded(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException {
        response.sendError(
                HttpStatus.TOO_MANY_REQUESTS.value(), HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase());
    }
}
