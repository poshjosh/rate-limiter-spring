package com.looseboxes.ratelimiter.spring.web;

import com.looseboxes.ratelimiter.RateLimitExceededException;
import com.looseboxes.ratelimiter.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RateLimitingInterceptorForRequest implements HandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(RateLimitingInterceptorForRequest.class);

    private final RateLimiter<HttpServletRequest>[] rateLimiters;

    public RateLimitingInterceptorForRequest(RateLimiter<HttpServletRequest>... rateLimiters) {
        this.rateLimiters = rateLimiters;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws RateLimitExceededException {

        final String requestURI = request.getRequestURI();

        LOG.trace("Invoking {} rate limiters for {}", rateLimiters.length, requestURI);

        for(RateLimiter<HttpServletRequest> rateLimiter : rateLimiters) {
            rateLimiter.record(request);
        }

        return true;
    }
}
