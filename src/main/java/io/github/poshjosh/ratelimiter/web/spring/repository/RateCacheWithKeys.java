package io.github.poshjosh.ratelimiter.web.spring.repository;

import io.github.poshjosh.ratelimiter.cache.RateCache;
import io.github.poshjosh.ratelimiter.annotations.Experimental;

@Experimental
public interface RateCacheWithKeys<K> extends RateCache<K> {

    default Iterable<K> keys() {
        return keys(0, Long.MAX_VALUE);
    }

    Iterable<K> keys(long offset, long limit);
}
