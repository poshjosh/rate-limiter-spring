package com.looseboxes.ratelimiter.web.spring.weblayertests;

import com.looseboxes.ratelimiter.RateLimiter;
import org.springframework.boot.test.context.TestComponent;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@TestComponent
public class RequestRateLimitingFilter implements Filter {

    private final RateLimiter<HttpServletRequest> rateLimiter;

    public RequestRateLimitingFilter(RateLimiter<HttpServletRequest> rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        rateLimiter.increment((HttpServletRequest)request);

        chain.doFilter(request, response);
    }
}
