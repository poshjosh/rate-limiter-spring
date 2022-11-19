package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.cache.RateCache;
import org.springframework.cache.Cache;

import java.util.Objects;

public class SpringRateCache<K, V> implements RateCache<K, V> {

    private final Cache delegate;

    public SpringRateCache(Cache delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public V get(K key) {
        Cache.ValueWrapper valueWrapper = delegate.get(key);
        return valueWrapper == null ? null : (V)valueWrapper.get();
    }

    @Override
    public boolean putIfAbsent(K key, V value) {
        Cache.ValueWrapper valueWrapper = delegate.putIfAbsent(key, value);
        return valueWrapper == null || valueWrapper.get() == null;
    }

    @Override
    public void put(K key, V value) {
        delegate.put(key, value);
    }

    @Override
    public boolean remove(K key) {
        return delegate.evictIfPresent(key);
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        if (clazz.isAssignableFrom(delegate.getClass())) {
            return clazz.cast(delegate);
        }
        throw new IllegalArgumentException("Unwrapping to " + clazz + " is not supported by this implementation");
    }
}
