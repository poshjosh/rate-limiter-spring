package com.looseboxes.ratelimiter.web.spring.weblayertests;

import com.looseboxes.ratelimiter.RateLimiter;
import com.looseboxes.ratelimiter.web.spring.RateLimiterWebMvcConfigurer;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class TestWebMvcConfigurer extends RateLimiterWebMvcConfigurer {

    public TestWebMvcConfigurer(RateLimiter<HttpServletRequest> rateLimiter) {
        super(rateLimiter);
    }
}
