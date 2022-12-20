package com.looseboxes.ratelimiter.web.spring.weblayertests;

import com.looseboxes.ratelimiter.annotation.RateLimit;
import com.looseboxes.ratelimiter.annotation.RateLimitGroup;
import com.looseboxes.ratelimiter.util.Operator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(ApiEndpoints.API)
public class ResourceWithMethodLimits {

    interface Endpoints{
        String HOME = "/method-limits";
        String LIMIT_1 = "/limit_1";
        String LIMIT_1_OR_5 = "/limit_1_or_5";
        String LIMIT_1_AND_5 = "/limit_1_and_5";
    }

    @RequestMapping(Endpoints.HOME)
    public String home(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @RequestMapping(Endpoints.LIMIT_1)
    @RateLimit(limit = Constants.LIMIT_1, duration = Constants.DURATION_SECONDS, timeUnit = TimeUnit.SECONDS)
    public String limit_1(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @RequestMapping(Endpoints.LIMIT_1_OR_5)
    @RateLimit(limit = Constants.LIMIT_1, duration = Constants.DURATION_SECONDS, timeUnit = TimeUnit.SECONDS)
    @RateLimit(limit = Constants.LIMIT_5, duration = Constants.DURATION_SECONDS, timeUnit = TimeUnit.SECONDS)
    public String limit_1_or_5(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @RequestMapping(Endpoints.LIMIT_1_AND_5)
    @RateLimitGroup(logic = Operator.AND)
    @RateLimit(limit = Constants.LIMIT_1, duration = Constants.DURATION_SECONDS, timeUnit = TimeUnit.SECONDS)
    @RateLimit(limit = Constants.LIMIT_5, duration = Constants.DURATION_SECONDS, timeUnit = TimeUnit.SECONDS)
    public String limit_1_and_5(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
