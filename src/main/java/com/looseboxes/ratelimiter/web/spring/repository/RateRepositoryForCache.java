package com.looseboxes.ratelimiter.web.spring.repository;

import com.looseboxes.ratelimiter.util.Experimental;
import com.looseboxes.ratelimiter.util.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * An adapter to make a {@link com.looseboxes.ratelimiter.cache.RateCache} implement
 * a {@link RateRepository}, so we can access our rate cache with the fluidity provided
 * by spring data repositories.
 * <p>
 * Each save method depends on the value returned by {@link KeyAccessor}. If null is returned
 * for the entity to save, the save method throws {@link NullPointerException}
 */
@Experimental
public class RateRepositoryForCache<V, ID> implements RateRepository<V, ID> {

    public interface KeyAccessor<V, K> {
        K getKey(V value);
    }

    private final Logger log = LoggerFactory.getLogger(RateRepositoryForCache.class);

    private final RateCacheWithKeys<ID, V> rateCache;
    private final KeyAccessor<V, ID> keyAccessor;

    public RateRepositoryForCache(RateCacheWithKeys<ID, V> rateCache) {
        this(rateCache, entity -> null);
    }

    public RateRepositoryForCache(RateCacheWithKeys<ID, V> rateCache, KeyAccessor<V, ID> keyAccessor) {
        this.rateCache = Objects.requireNonNull(rateCache);
        this.keyAccessor = Objects.requireNonNull(keyAccessor);
    }

    @Override
    public Page<V> findAll(Pageable pageable) {
        return findAll(null, pageable);
    }

    @Override
    public Page<V> findAll(
            @Nullable Example<V> example, Pageable pageable) {
        log.debug("Request to findAll by, example: {}, pageable: {}", example, pageable);

        final Page<V> result;

        final long offset = pageable.getOffset();
        final long pageSize = pageable.getPageSize();

        if(pageSize < 1 || offset < 0) {
            result = Page.empty(pageable);
        }else{

            // Though very sub-optimal, we first find all then sort everything,
            // before applying offset and pageSize
            Iterable<V> found = example == null ? findAll() : findAll(example);

            Stream<V> stream = StreamSupport.stream(found.spliterator(), false);

            final Sort sort = pageable.getSort();
            if(sort.isSorted() && !sort.isEmpty()) {
                stream = stream.sorted(new ComparatorFromSort<>(sort));
            }

            stream = stream.skip(offset);

            // Another round trip to get our count
            long count = example == null ? this.count() : StreamSupport.stream(found.spliterator(), true).count();
            result = new PageImpl<>(stream.limit(pageSize).collect(Collectors.toList()), pageable, count);
        }

        return result;
    }

    @Override
    public Iterable<V> findAll(Example<V> example) {
        return findAll(new FilterFromExample<>(example));
    }

    @Override
    public Iterable<V> findAll(Sort sort) {
        return new Iterable<V>() {
            @Override
            public Iterator<V> iterator() {
                return StreamSupport.stream(findAll().spliterator(), false)
                        .sorted(new ComparatorFromSort<>(sort)).iterator();
            }
            @Override
            public Spliterator<V> spliterator() {
                return Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED);
            }
        };
    }

    @Override
    public <S extends V> S save(S s) {
        ID key = keyAccessor.getKey(s);
        rateCache.put(Objects.requireNonNull(key), s);
        return s;
    }

    @Override
    public <S extends V> Iterable<S> saveAll(Iterable<S> iterable) {
        List<S> saved = new ArrayList<>();
        iterable.forEach(toSave -> {
            save(toSave);
            saved.add(toSave);
        });
        return saved;
    }

    @Override
    public Optional<V> findById(ID id) {
        return Optional.ofNullable(this.rateCache.get(id));
    }

    @Override
    public boolean existsById(ID id) {
        return this.rateCache.containsKey(id);
    }

    @Override
    public Iterable<V> findAll() {
        return findAll((candidate) -> true);
    }

    @Override
    public Iterable<V> findAllById(Iterable<ID> ids) {
        return new Iterable<V>() {
            @Override
            public Iterator<V> iterator() {
                return streamAllById(ids).iterator();
            }
            @Override
            public Spliterator<V> spliterator() {
                return Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED);
            }
        };
    }

    private Stream<V> streamAllById(Iterable<ID> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false)
                .map(id -> id == null ? null : rateCache.get(id));
    }

    @Override
    public long count() {
        return this.rateCache.size();
    }

    @Override
    public void deleteById(ID id) {
        this.rateCache.remove(id);
    }

    @Override
    public void delete(V toDelete) {
        ID key = keyAccessor.getKey(toDelete);
        if (key != null) {
            deleteById(key);
            return;
        }
        findAll(toDelete::equals).forEach(this::delete);
    }

    @Override
    public void deleteAll(Iterable<? extends V> iterable) {
        iterable.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        this.rateCache.clear();
    }

    private Iterable<V> findAll(Predicate<V> filter) {
        return findAll(0, Long.MAX_VALUE, filter);
    }

    private Iterable<V> findAll(long offset, long limit, Predicate<V> filter) {
        final Iterable<ID> ids = this.rateCache.keys(offset, limit);
        if (log.isTraceEnabled()) {
            log.trace("Offset: {}, limit: {}, IDs: {}", offset, limit, ids);
        }

        return new Iterable<V>() {
            @Override
            public Iterator<V> iterator() {
                return streamAllById(ids).filter(filter).iterator();
            }
            @Override
            public Spliterator<V> spliterator() {
                return Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED);
            }
        };
    }
}
