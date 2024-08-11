package io.github.poshjosh.ratelimiter.web.spring.repository;

import io.github.poshjosh.ratelimiter.annotations.Experimental;
import io.github.poshjosh.ratelimiter.bandwidths.Bandwidth;
import io.github.poshjosh.ratelimiter.store.BandwidthsStore;
import org.springframework.cache.Cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Experimental
public class BandwidthStoreSpring<K> implements BandwidthsStore<K> {

    private final Cache delegate;

    public BandwidthStoreSpring(Cache delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    public Bandwidth get(K key) {
        Cache.ValueWrapper valueWrapper = delegate.get(key);
        return valueWrapper == null ? null : (Bandwidth) valueWrapper.get();
    }

    @Override
    public void put(K key, Bandwidth value) {
        delegate.put(key, value);
    }
}
