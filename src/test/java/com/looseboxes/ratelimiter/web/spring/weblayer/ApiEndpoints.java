package com.looseboxes.ratelimiter.web.spring.weblayer;

public interface ApiEndpoints {
    String API = "/api";

    String METHOD_LIMITS = API + ResourceWithMethodLimits.Endpoints.HOME;
    String LIMIT_1 = API + ResourceWithMethodLimits.Endpoints.LIMIT_1;
    String LIMIT_1_OR_5 = API + ResourceWithMethodLimits.Endpoints.LIMIT_1_OR_5;
    String LIMIT_1_AND_5 = API + ResourceWithMethodLimits.Endpoints.LIMIT_1_AND_5;

    String CLASS_LIMITS = API + ResourceWithClassLimits.Endpoints.HOME;
}
