package com.looseboxes.ratelimiter.spring.web;

import com.looseboxes.ratelimiter.*;
import com.looseboxes.ratelimiter.annotation.RateComposition;

import java.util.*;

class Util {

    static <K> Map<K, RateLimiter<K>> createRateLimiters(
            RateSupplier rateSupplier,
            RateExceededHandler rateExceededHandler,
            List<RateComposition<K>> limits) {
        final Map<K, RateLimiter<K>> rateLimiters;
        if(limits.isEmpty()) {
            rateLimiters = Collections.emptyMap();
        }else{
            rateLimiters = new HashMap<>(limits.size(), 1.0f);
            for(RateComposition<K> limit : limits) {
                rateLimiters.put(limit.getId(), new RateLimiterSingleton<>(
                        limit.getId(), rateSupplier, limit.getLogic(), rateExceededHandler, limit.getRates()
                ));
            }
        }
        return rateLimiters.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(rateLimiters);
    }
}
