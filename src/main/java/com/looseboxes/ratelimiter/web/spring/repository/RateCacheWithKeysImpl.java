package com.looseboxes.ratelimiter.web.spring.repository;

import com.looseboxes.ratelimiter.cache.RateCache;
import com.looseboxes.ratelimiter.annotations.Experimental;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/** @Experimental */
@Experimental
public class RateCacheWithKeysImpl<K, V> implements RateCacheWithKeys<K, V>{

    private static final Logger LOG = LoggerFactory.getLogger(RateCacheWithKeysImpl.class);

    private final RateCache<K, V> delegate;

    private final List<K> keys = new ArrayList<>();
    private final ReadWriteLock keysLock = new ReentrantReadWriteLock();

    public RateCacheWithKeysImpl(RateCache<K, V> delegate) {
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
        return delegate.containsKey(key);
    }

    @Override
    public V get(K key) {
        return delegate.get(key);
    }

    @Override
    public boolean putIfAbsent(K key, V value) {
        boolean result = delegate.putIfAbsent(key, value);
        if(result) {
            addKey(key);
        }
        return result;
    }

    @Override
    public void put(K key, V value) {
        delegate.put(key, value);
        addKey(key);
    }

    @Override
    public boolean remove(K key) {
        boolean result = delegate.remove(key);
        if(result) {
            removeKey(key);
        }
        return result;
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        return delegate.unwrap(clazz);
    }

    private void addKey(K key) {
        LOG.trace("Adding key: {}", key);
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
        LOG.trace("Removing key: {}", key);
        try{
            keysLock.writeLock().lock();
            keys.remove(key);
        }finally {
            keysLock.writeLock().unlock();
        }
    }
}
