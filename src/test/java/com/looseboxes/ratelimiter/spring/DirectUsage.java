package com.looseboxes.ratelimiter.spring;

import com.looseboxes.ratelimiter.RateLimitExceededException;
import com.looseboxes.ratelimiter.RateLimiter;
import com.looseboxes.ratelimiter.RateLimiterImpl;
import com.looseboxes.ratelimiter.rates.LimitWithinDuration;
import com.looseboxes.ratelimiter.spring.util.RateLimitPropertiesSpring;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class DirectUsage {

    private final RateLimiter rateLimiter;

    public DirectUsage(RateLimitPropertiesSpring properties) {
        rateLimiter = new RateLimiterImpl(() -> new LimitWithinDuration(), properties.toRates().values());
    }

    public void rateLimit(HttpServletRequest request) throws RateLimitExceededException {
        rateLimiter.record(request.getRequestURI());
    }
}
