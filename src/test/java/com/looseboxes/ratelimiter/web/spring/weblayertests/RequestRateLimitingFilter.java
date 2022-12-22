package com.looseboxes.ratelimiter.web.spring.weblayertests;

import com.looseboxes.ratelimiter.RateLimiter;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@TestComponent
public class RequestRateLimitingFilter implements Filter {

    private final RateLimiter<HttpServletRequest> rateLimiter;

    public RequestRateLimitingFilter(RateLimiter<HttpServletRequest> rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        if (rateLimiter.tryConsume(filterChain, (HttpServletRequest)request)) {

            filterChain.doFilter(request, response);

            return;
        }

        ((HttpServletResponse) response).sendError(
                HttpStatus.TOO_MANY_REQUESTS.value(), HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase());
    }
}
