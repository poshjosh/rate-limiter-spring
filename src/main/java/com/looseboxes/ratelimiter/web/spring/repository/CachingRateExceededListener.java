package com.looseboxes.ratelimiter.web.spring.repository;

import com.looseboxes.ratelimiter.RateExceededEvent;
import com.looseboxes.ratelimiter.RateExceededListener;
import com.looseboxes.ratelimiter.cache.RateCache;
import com.looseboxes.ratelimiter.util.Experimental;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@Experimental
public class CachingRateExceededListener implements RateExceededListener {

    private static final Logger LOG = LoggerFactory.getLogger(CachingRateExceededListener.class);

    private final RateCache<Object> rateCache;

    public CachingRateExceededListener(RateCache<Object> rateCache) {
        this.rateCache = Objects.requireNonNull(rateCache);
    }

    @Override
    public void onRateExceeded(RateExceededEvent rateExceededEvent) {
        LOG.debug("Exceeded limit: true, {} = {} exceeds: {}",
                rateExceededEvent.getKey(), rateExceededEvent.getRate(), rateExceededEvent.getExceededLimit());
        this.rateCache.put(rateExceededEvent.getKey(), rateExceededEvent.getRate());
    }
}
