package com.looseboxes.ratelimiter.web.spring.weblayertests;

import com.looseboxes.ratelimiter.ResourceLimiter;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@TestComponent
public class RequestRateLimitingFilter implements Filter {

    private final ResourceLimiter<HttpServletRequest> resourceLimiter;

    public RequestRateLimitingFilter(ResourceLimiter<HttpServletRequest> resourceLimiter) {
        this.resourceLimiter = resourceLimiter;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        if (resourceLimiter.tryConsume((HttpServletRequest)request)) {

            filterChain.doFilter(request, response);

            return;
        }

        ((HttpServletResponse) response).sendError(
                HttpStatus.TOO_MANY_REQUESTS.value(), HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase());
    }
}
