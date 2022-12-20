package com.looseboxes.ratelimiter.web.spring.weblayertests;

import com.looseboxes.ratelimiter.annotation.RateLimit;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("")
public class ResourceWithoutClassPatterns {

    interface Endpoints{
        String LIMIT_1 = "/limit_1";
    }

    @RequestMapping(Endpoints.LIMIT_1)
    @RateLimit(limit = Constants.LIMIT_1, duration = Constants.DURATION_SECONDS, timeUnit = TimeUnit.SECONDS)
    public String limit_1(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
