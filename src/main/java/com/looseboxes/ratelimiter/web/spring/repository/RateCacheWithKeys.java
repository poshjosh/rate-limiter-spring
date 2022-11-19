package com.looseboxes.ratelimiter.web.spring.repository;

import com.looseboxes.ratelimiter.cache.RateCache;
import com.looseboxes.ratelimiter.util.Experimental;

@Experimental
public interface RateCacheWithKeys<K, V> extends RateCache<K, V> {

    long size();

    Iterable<K> keys(long offset, long limit);
}
