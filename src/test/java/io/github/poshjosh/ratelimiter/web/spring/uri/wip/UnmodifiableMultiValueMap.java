package io.github.poshjosh.ratelimiter.web.spring.uri.wip;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

final class UnmodifiableMultiValueMap<K, V> implements MultiValueMap<K, V> {
    private final MultiValueMap<K, V> delegate;

    UnmodifiableMultiValueMap(MultiValueMap<K, V> delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override public V getFirst(K key) {
        return delegate.getFirst(key);
    }

    @Override public void add(K key, V value) {
        throw err();
    }

    @Override public void addAll(K key, List<? extends V> values) {
        throw err();
    }

    @Override public void addAll(MultiValueMap<K, V> values) {
        throw err();
    }

    @Override public void set(K key, V value) {
        throw err();
    }

    @Override public void setAll(Map<K, V> values) {
        throw err();
    }

    @Override public Map<K, V> toSingleValueMap() {
        return Collections.unmodifiableMap(delegate.toSingleValueMap());
    }

    @Override public int size() {
        return delegate.size();
    }

    @Override public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    @Override public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override public List<V> get(Object key) {
        return delegate.get(key);
    }

    @Override public List<V> put(K key, List<V> value) {
        throw err();
    }

    @Override public List<V> remove(Object key) {
        throw err();
    }

    @Override public void putAll(Map<? extends K, ? extends List<V>> m) {
        throw err();
    }

    @Override public void clear() {
        throw err();
    }

    @Override public Set<K> keySet() {
        return Collections.unmodifiableSet(delegate.keySet());
    }

    @Override public Collection<List<V>> values() {
        return Collections.unmodifiableCollection(delegate.values());
    }

    @Override public Set<Entry<K, List<V>>> entrySet() {
        UnaryOperator<Entry<K, List<V>>> makeImmutable = entry -> new AbstractMap.SimpleImmutableEntry<>(
                entry.getKey(), entry.getValue());
        return Collections.unmodifiableSet(
                delegate.entrySet().stream().map(makeImmutable).collect(Collectors.toSet()));
    }

    private UnsupportedOperationException err() {
        return new UnsupportedOperationException("Not modifiable");
    }
}
