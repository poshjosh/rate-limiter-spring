package io.github.poshjosh.ratelimiter.web.spring.repository;

import io.github.poshjosh.ratelimiter.annotations.Experimental;
import io.github.poshjosh.ratelimiter.annotations.VisibleForTesting;
import io.github.poshjosh.ratelimiter.bandwidths.Bandwidths;
import io.github.poshjosh.ratelimiter.cache.RateCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * An adapter to make a {@link RateCache} implement
 * a {@link RateRepository}, so we can access our rate cache with the fluidity provided
 * by spring data repositories.
 * Experimental
 */
@Experimental
public class RateRepositoryForCache<ID> implements RateRepository<RateEntity<ID>, ID> {

    private static final Logger log = LoggerFactory.getLogger(RateRepositoryForCache.class);

    private final RateCacheWithKeys<ID> rateCache;

    public RateRepositoryForCache(RateCacheWithKeys<ID> rateCache) {
        this.rateCache = Objects.requireNonNull(rateCache);
    }

    @Override
    public Page<RateEntity<ID>> findAll(Pageable pageable) {
        return findAll(null, pageable);
    }

    @Override
    public Page<RateEntity<ID>> findAll(Example<RateEntity<ID>> example, Pageable pageable) {
        log.debug("Request to findAll by, example: {}, pageable: {}", example, pageable);

        final Page<RateEntity<ID>> result;

        final long offset = pageable.getOffset();
        final long pageSize = pageable.getPageSize();

        if(pageSize < 1 || offset < 0) {
            result = Page.empty(pageable);
        }else{

            // Though quite sub-optimal, we first find all then sort everything, before applying offset and pageSize
            final Iterable<RateEntity<ID>> total = select(example);

            final Stream<RateEntity<ID>> selected = stream(total, pageable.getSort()).skip(offset).limit(pageSize);

            result = new PageImpl<>(selected.collect(Collectors.toList()), pageable, count(select(example)));
        }

        return result;
    }

    private Iterable<RateEntity<ID>> select(Example<RateEntity<ID>> example) {
        return example == null ? findAll() : findAll(example);
    }

    @Override
    public Iterable<RateEntity<ID>> findAll(Example<RateEntity<ID>> example) {
        return findAll(new FilterFromExample<>(example));
    }

    @Override
    public Iterable<RateEntity<ID>> findAll(Sort sort) {
        return iterable(stream(findAll(), sort));
    }

    @Override
    public <S extends RateEntity<ID>> S save(S s) {
        rateCache.put(Objects.requireNonNull(s.getId()), (Bandwidths) s.getData());
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
        Bandwidths data = this.rateCache.get(id);
        return Optional.ofNullable(data == null ? null : new RateEntity<>(id, data));
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
        return iterable(streamAllById(ids));
    }

    private Stream<RateEntity<ID>> streamAllById(Iterable<ID> iterable) {
        return stream(iterable).map(id -> id == null ? null : new RateEntity<>(id, rateCache.get(id)));
    }

    @Override
    public long count() {
        return count(this.rateCache.keys());
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
        return iterable(streamAllById(this.rateCache.keys()).filter(filter));
    }

    private <T> Iterable<T> iterable(Stream<T> stream) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return stream.iterator();
            }
            @Override
            public Spliterator<T> spliterator() {
                return Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED);
            }
        };
    }

    private <T> Stream<T> stream(Iterable<T> iterable, Sort sort) {
        return sort.isUnsorted() ? stream(iterable) : stream(iterable).sorted(new ComparatorFromSort<>(sort));
    }

    private <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    private long count(Iterable<?> iterable) {
        return StreamSupport.stream(iterable.spliterator(), true).count();
    }

    @VisibleForTesting
    public RateCacheWithKeys<ID> getCache() {
        return rateCache;
    }
}
