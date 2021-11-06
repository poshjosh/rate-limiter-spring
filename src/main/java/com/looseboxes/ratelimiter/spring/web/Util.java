package com.looseboxes.ratelimiter.spring.web;

import com.looseboxes.ratelimiter.*;
import com.looseboxes.ratelimiter.rates.Rate;
import com.looseboxes.ratelimiter.rates.Rates;
import com.looseboxes.ratelimiter.util.RateFactory;
import com.looseboxes.ratelimiter.annotation.AnnotatedElementIdProvider;
import com.looseboxes.ratelimiter.annotation.RateFactoryForClassLevelAnnotation;
import com.looseboxes.ratelimiter.annotation.RateFactoryForMethodLevelAnnotation;

import java.lang.reflect.Method;
import java.util.*;

class Util {

    static <K> Map<K, RateLimiter<K>> createRateLimiters(
            RateSupplier rateSupplier,
            Map<K, Rate[]> limits,
            RateExceededHandler<K> rateExceededHandler) {
        final Map<K, RateLimiter<K>> rateLimiters;
        if(limits.isEmpty()) {
            rateLimiters = Collections.emptyMap();
        }else{
            rateLimiters = new HashMap<>(limits.size(), 1.0f);
            limits.forEach((key, rates) -> {
                rateLimiters.put(key, new RateLimiterSingleton<>(
                        key, rateSupplier, Rates.Logic.OR, Arrays.asList(rates), rateExceededHandler
                ));
            });
        }
        return rateLimiters.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(rateLimiters);
    }
}
