package io.github.poshjosh.ratelimiter.web.spring;

import io.github.poshjosh.ratelimiter.bandwidths.Bandwidths;
import io.github.poshjosh.ratelimiter.cache.RateCache;
import org.springframework.cache.Cache;

import java.util.Objects;

public class SpringRateCache<K> implements RateCache<K> {

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
    public Bandwidths get(K key) {
        Cache.ValueWrapper valueWrapper = delegate.get(key);
        return valueWrapper == null ? null : (Bandwidths) valueWrapper.get();
    }

    @Override
    public boolean putIfAbsent(K key, Bandwidths value) {
        Cache.ValueWrapper valueWrapper = delegate.putIfAbsent(key, value);
        return valueWrapper == null || valueWrapper.get() == null;
    }

    @Override
    public void put(K key, Bandwidths value) {
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