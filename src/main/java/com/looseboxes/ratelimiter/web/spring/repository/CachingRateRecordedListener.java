package com.looseboxes.ratelimiter.web.spring.repository;

import com.looseboxes.ratelimiter.RateRecordedEvent;
import com.looseboxes.ratelimiter.RateRecordedListener;
import com.looseboxes.ratelimiter.cache.RateCache;
import com.looseboxes.ratelimiter.util.Experimental;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@Experimental
public class CachingRateRecordedListener implements RateRecordedListener {

    private static final Logger LOG = LoggerFactory.getLogger(CachingRateRecordedListener.class);

    private final RateCache<Object> rateCache;

    public CachingRateRecordedListener(RateCache<Object> rateCache) {
        this.rateCache = Objects.requireNonNull(rateCache);
    }

    @Override
    public void onRateRecorded(RateRecordedEvent rateRecordedEvent) {
        LOG.debug("Exceeded limit: {}, {} = {} exceeds: {}",
                rateRecordedEvent.isLimitExceeded(), rateRecordedEvent.getKey(), rateRecordedEvent.getRate(),
                rateRecordedEvent.getExceededLimitOptional().orElse(null));
        this.rateCache.put(rateRecordedEvent.getKey(), rateRecordedEvent.getRate());
    }
}
