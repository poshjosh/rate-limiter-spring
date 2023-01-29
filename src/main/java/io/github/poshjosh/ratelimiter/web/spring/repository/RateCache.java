package io.github.poshjosh.ratelimiter.web.spring.repository;

import io.github.poshjosh.ratelimiter.store.BandwidthsStore;
import io.github.poshjosh.ratelimiter.annotations.Experimental;
import io.github.poshjosh.ratelimiter.bandwidths.Bandwidths;

@Experimental
public interface RateCache<K> extends BandwidthsStore<K> {

    default Iterable<K> keys() {
        return keys(0, Long.MAX_VALUE);
    }

    Iterable<K> keys(long offset, long limit);

    void clear();

    boolean containsKey(K key);

    @Override
    Bandwidths get(K key);

    boolean putIfAbsent(K key, Bandwidths value);

    @Override
    void put(K key, Bandwidths value);

    boolean remove(K key);

    <T> T unwrap(Class<T> clazz);
}
