package com.looseboxes.ratelimiter.spring.web;

import com.looseboxes.ratelimiter.*;
import com.looseboxes.ratelimiter.rates.Rate;
import com.looseboxes.ratelimiter.util.RateFactory;
import com.looseboxes.ratelimiter.annotation.AnnotatedElementIdProvider;
import com.looseboxes.ratelimiter.annotation.RateFactoryForClassLevelAnnotation;
import com.looseboxes.ratelimiter.annotation.RateFactoryForMethodLevelAnnotation;

import java.lang.reflect.Method;
import java.util.*;

class Util {

    static <K> Map<K, RateLimiter<K>> createRateLimiters(
            Map<K, Rate> limits,
            RateSupplier rateSupplier,
            RateExceededHandler<K> rateExceededHandler) {
        final Map<K, RateLimiter<K>> rateLimiters;
        if(limits.isEmpty()) {
            rateLimiters = Collections.emptyMap();
        }else{
            rateLimiters = new HashMap<>(limits.size(), 1.0f);
            limits.forEach((key, rate) -> {
                rateLimiters.put(key, new RateLimiterSingleton<>(
                        key, rateSupplier, Collections.singletonList(rate), rateExceededHandler
                ));
            });
        }
        return rateLimiters.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(rateLimiters);
    }
}
