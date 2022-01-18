package com.looseboxes.ratelimiter.web.spring.weblayertests;

public interface ApiEndpoints {
    String API = "/api";

    String METHOD_LIMITS_HOME = API + ResourceWithMethodLimits.Endpoints.HOME;
    String METHOD_LIMIT_1 = API + ResourceWithMethodLimits.Endpoints.LIMIT_1;
    String METHOD_LIMIT_1_OR_5 = API + ResourceWithMethodLimits.Endpoints.LIMIT_1_OR_5;
    String METHOD_LIMIT_1_AND_5 = API + ResourceWithMethodLimits.Endpoints.LIMIT_1_AND_5;

    String CLASS_LIMITS_HOME = API + ResourceWithClassLimits.Endpoints.HOME;

    // This does not have the /api prefix
    String NO_CLASS_PATTERNS_LIMIT_1 = ResourceWithoutClassPatterns.Endpoints.LIMIT_1;

    String NO_METHOD_PATTERNS_LIMIT_1 = API + ResourceWithoutMethodPatterns.Endpoints.LIMIT_1;
}
