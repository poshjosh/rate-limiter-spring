package io.github.poshjosh.ratelimiter.web.spring.repository;

import io.github.poshjosh.ratelimiter.annotations.Experimental;
import io.github.poshjosh.ratelimiter.bandwidths.Bandwidth;
import org.springframework.cache.Cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Experimental
public class RateCacheSpring<K> implements RateCache<K> {

    private final Cache delegate;

    private final List<K> keys = new ArrayList<>();
    private final ReadWriteLock keysLock = new ReentrantReadWriteLock();

    public RateCacheSpring(Cache delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    public Iterable<K> keys(long offset, long limit) {
        return getKeys(offset, limit);
    }

    private List<K> getKeys(long offset, long limit) {
        try {
            keysLock.readLock().lock();
            final int size = keys.size();
            if (offset >= size) {
                return Collections.emptyList();
            } else {
                final long n = offset + limit;
                final long end = Math.min(n, size);
                if (end - offset <= 0) {
                    return Collections.emptyList();
                } else {
                    return keys.subList((int)offset, (int)end);
                }
            }
        }finally {
            keysLock.readLock().unlock();
        }
    }

    @Override
    public void clear() {
        delegate.clear();
        keys.clear();
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public Bandwidth get(K key) {
        Cache.ValueWrapper valueWrapper = delegate.get(key);
        return valueWrapper == null ? null : (Bandwidth) valueWrapper.get();
    }

    @Override
    public boolean putIfAbsent(K key, Bandwidth value) {
        Cache.ValueWrapper wrapper = delegate.putIfAbsent(key, value);
        final Object result = wrapper == null ? null : wrapper.get();
        if(result != null) {
            addKey(key);
        }
        return result != null;
    }

    @Override
    public void put(K key, Bandwidth value) {
        delegate.put(key, value);
        addKey(key);
    }

    @Override
    public boolean remove(K key) {
        boolean result = delegate.evictIfPresent(key);
        if(result) {
            removeKey(key);
        }
        return result;
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        if (clazz.isAssignableFrom(delegate.getClass())) {
            return clazz.cast(delegate);
        }
        throw new IllegalArgumentException("Unwrapping to " + clazz + " is not supported by this implementation");
    }

    private void addKey(K key) {
        try{
            keysLock.writeLock().lock();
            if(!keys.contains(key)) {
                keys.add(key);
            }
        }finally {
            keysLock.writeLock().unlock();
        }
    }

    private void removeKey(K key) {
        try{
            keysLock.writeLock().lock();
            keys.remove(key);
        }finally {
            keysLock.writeLock().unlock();
        }
    }
}
