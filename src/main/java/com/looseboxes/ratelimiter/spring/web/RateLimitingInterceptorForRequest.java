package com.looseboxes.ratelimiter.spring.web;

import com.looseboxes.ratelimiter.RateLimitExceededException;
import com.looseboxes.ratelimiter.RateLimiter;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

public class RateLimitingInterceptorForRequest implements HandlerInterceptor {

    private final RateLimiter<HttpServletRequest>[] rateLimiters;

    public RateLimitingInterceptorForRequest(RateLimiter<HttpServletRequest>... rateLimiters) {
        this.rateLimiters = Objects.requireNonNull(rateLimiters);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws RateLimitExceededException {
        for(RateLimiter<HttpServletRequest> rateLimiter : rateLimiters) {
            rateLimiter.record(request);
        }
        return true;
    }
}
