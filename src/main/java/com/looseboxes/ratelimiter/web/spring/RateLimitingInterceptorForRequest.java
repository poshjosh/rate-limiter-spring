package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.RateLimitExceededException;
import com.looseboxes.ratelimiter.RateLimiter;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

public class RateLimitingInterceptorForRequest implements HandlerInterceptor {

    private final RateLimiter<String>[] rateLimiters;

    @SafeVarargs
    public RateLimitingInterceptorForRequest(RateLimiter<String>... rateLimiters) {
        this.rateLimiters = Objects.requireNonNull(rateLimiters);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws RateLimitExceededException {
        final String requestUri = request.getRequestURI();
        for(RateLimiter<String> rateLimiter : rateLimiters) {
            rateLimiter.record(requestUri);
        }
        return true;
    }
}
