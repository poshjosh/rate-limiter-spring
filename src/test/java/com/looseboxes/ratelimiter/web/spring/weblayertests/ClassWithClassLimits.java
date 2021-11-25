package com.looseboxes.ratelimiter.web.spring.weblayertests;

import com.looseboxes.ratelimiter.annotation.RateLimit;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@RequestMapping(ApiEndpoints.API)
@RateLimit(limit = Constants.LIMIT_1, duration = Constants.DURATION_SECONDS, timeUnit = TimeUnit.SECONDS)
@RateLimit(limit = Constants.LIMIT_5, duration = Constants.DURATION_SECONDS, timeUnit = TimeUnit.SECONDS)
public class ClassWithClassLimits {

    interface Endpoints{
        String HOME = "/class-limits";
    }

    @RequestMapping(Endpoints.HOME)
    @RateLimit(limit = Constants.LIMIT_5 * 2, duration = Constants.DURATION_SECONDS, timeUnit = TimeUnit.SECONDS)
    public String home(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
