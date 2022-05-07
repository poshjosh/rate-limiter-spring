package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractRateLimiterWebMvcConfigurer implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(AbstractRateLimiterWebMvcConfigurer.class);

    private final HandlerInterceptor rateLimitingHandlerInterceptor;

    protected AbstractRateLimiterWebMvcConfigurer(RateLimiter<HttpServletRequest> rateLimiter) {

        rateLimitingHandlerInterceptor = new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                rateLimiter.increment(request);
                return true;
            }
        };

        log.info("Completed automatic setup of rate limiting");
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitingHandlerInterceptor);
    }

    public HandlerInterceptor getRateLimitingHandlerInterceptor() {
        return rateLimitingHandlerInterceptor;
    }
}
