package com.looseboxes.ratelimiter.web.spring.repository;

import java.util.List;

@FunctionalInterface
public interface PageSupplier<K> {

    default List<K> getAll() {
        return getPage(0, Integer.MAX_VALUE);
    }

    List<K> getPage(int offset, int limit);
}
