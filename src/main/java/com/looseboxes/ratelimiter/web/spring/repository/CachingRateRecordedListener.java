package com.looseboxes.ratelimiter.web.spring.repository;

import com.looseboxes.ratelimiter.RateRecordedListener;
import com.looseboxes.ratelimiter.cache.RateCache;
import com.looseboxes.ratelimiter.rates.Rate;
import com.looseboxes.ratelimiter.util.Experimental;

import java.util.Objects;

@Experimental
public class CachingRateRecordedListener implements RateRecordedListener {

    private final RateCache<Object> rateCache;

    public CachingRateRecordedListener(RateCache<Object> rateCache) {
        this.rateCache = Objects.requireNonNull(rateCache);
    }

    @Override
    public void onRateRecorded(Object key, Rate rate) {
        this.rateCache.put(key, rate);
    }

    @Override
    public void onRateExceeded(Object key, Rate rate, Rate exceededRate) { }
}
