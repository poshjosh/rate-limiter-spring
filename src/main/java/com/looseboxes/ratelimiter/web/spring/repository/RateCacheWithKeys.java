package com.looseboxes.ratelimiter.web.spring.repository;

import com.looseboxes.ratelimiter.cache.RateCache;
import com.looseboxes.ratelimiter.annotations.Experimental;

@Experimental
public interface RateCacheWithKeys<K> extends RateCache<K> {

    default Iterable<K> keys() {
        return keys(0, Long.MAX_VALUE);
    }

    Iterable<K> keys(long offset, long limit);
}
