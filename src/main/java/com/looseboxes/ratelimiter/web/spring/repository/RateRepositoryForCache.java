package com.looseboxes.ratelimiter.web.spring.repository;

import com.looseboxes.ratelimiter.rates.Rate;
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
 */
@Experimental
public class RateRepositoryForCache<ID> implements RateRepository<RateEntity<ID>, ID> {

    private static final Logger log = LoggerFactory.getLogger(RateRepositoryForCache.class);

    private final RateCacheWithKeys<ID, Rate> rateCache;

    public RateRepositoryForCache(RateCacheWithKeys<ID, Rate> rateCache) {
        this.rateCache = Objects.requireNonNull(rateCache);
    }

    @Override
    public Page<RateEntity<ID>> findAll(Pageable pageable) {
        return findAll(null, pageable);
    }

    @Override
    public Page<RateEntity<ID>> findAll(
            @Nullable Example<RateEntity<ID>> example, Pageable pageable) {
        log.debug("Request to findAll by, example: {}, pageable: {}", example, pageable);

        final Page<RateEntity<ID>> result;

        final long offset = pageable.getOffset();
        final long pageSize = pageable.getPageSize();

        if(pageSize < 1 || offset < 0) {
            result = Page.empty(pageable);
        }else{

            // Though very sub-optimal, we first find all then sort everything,
            // before applying offset and pageSize
            Iterable<RateEntity<ID>> found = example == null ? findAll() : findAll(example);

            Stream<RateEntity<ID>> stream = StreamSupport.stream(found.spliterator(), false);

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
    public Iterable<RateEntity<ID>> findAll(Example<RateEntity<ID>> example) {
        return findAll(new FilterFromExample<>(example));
    }

    @Override
    public Iterable<RateEntity<ID>> findAll(Sort sort) {
        return new Iterable<RateEntity<ID>>() {
            @Override
            public Iterator<RateEntity<ID>> iterator() {
                return StreamSupport.stream(findAll().spliterator(), false)
                        .sorted(new ComparatorFromSort<>(sort)).iterator();
            }
            @Override
            public Spliterator<RateEntity<ID>> spliterator() {
                return Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED);
            }
        };
    }

    @Override
    public <S extends RateEntity<ID>> S save(S s) {
        rateCache.put(Objects.requireNonNull(s.getId()), s.getRate());
        return s;
    }

    @Override
    public <S extends RateEntity<ID>> Iterable<S> saveAll(Iterable<S> iterable) {
        List<S> saved = new ArrayList<>();
        iterable.forEach(toSave -> {
            save(toSave);
            saved.add(toSave);
        });
        return saved;
    }

    @Override
    public Optional<RateEntity<ID>> findById(ID id) {
        Rate rate = this.rateCache.get(id);
        return Optional.ofNullable(rate == null ? null : new RateEntity<>(id, rate));
    }

    @Override
    public boolean existsById(ID id) {
        return this.rateCache.containsKey(id);
    }

    @Override
    public Iterable<RateEntity<ID>> findAll() {
        return findAll((candidate) -> true);
    }

    @Override
    public Iterable<RateEntity<ID>> findAllById(Iterable<ID> ids) {
        return new Iterable<RateEntity<ID>>() {
            @Override
            public Iterator<RateEntity<ID>> iterator() {
                return streamAllById(ids).iterator();
            }
            @Override
            public Spliterator<RateEntity<ID>> spliterator() {
                return Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED);
            }
        };
    }

    private Stream<RateEntity<ID>> streamAllById(Iterable<ID> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false)
                .map(id -> id == null ? null : new RateEntity<>(id, rateCache.get(id)));
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
    public void delete(RateEntity<ID> toDelete) {
        ID id = toDelete.getId();
        if (id != null) {
            deleteById(id);
            return;
        }
        findAll(toDelete::equals).forEach(this::delete);
    }

    @Override
    public void deleteAll(Iterable<? extends RateEntity<ID>> iterable) {
        iterable.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        this.rateCache.clear();
    }

    private Iterable<RateEntity<ID>> findAll(Predicate<RateEntity<ID>> filter) {
        return findAll(0, Long.MAX_VALUE, filter);
    }

    private Iterable<RateEntity<ID>> findAll(long offset, long limit, Predicate<RateEntity<ID>> filter) {
        final Iterable<ID> ids = this.rateCache.keys(offset, limit);
        if (log.isTraceEnabled()) {
            log.trace("Offset: {}, limit: {}, IDs: {}", offset, limit, ids);
        }

        return new Iterable<RateEntity<ID>>() {
            @Override
            public Iterator<RateEntity<ID>> iterator() {
                return streamAllById(ids).filter(filter).iterator();
            }
            @Override
            public Spliterator<RateEntity<ID>> spliterator() {
                return Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED);
            }
        };
    }
}
