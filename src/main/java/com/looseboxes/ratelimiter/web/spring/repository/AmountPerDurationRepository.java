package com.looseboxes.ratelimiter.web.spring.repository;

import com.looseboxes.ratelimiter.rates.AmountPerDuration;
import com.looseboxes.ratelimiter.util.Experimental;
import com.looseboxes.ratelimiter.util.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Experimental
public class AmountPerDurationRepository<ID> implements RateRepository<AmountPerDurationEntity<ID>, ID> {

    private final Logger log = LoggerFactory.getLogger(AmountPerDurationRepository.class);

    private final RateCacheWithKeys<ID, AmountPerDuration> rateCache;

    public AmountPerDurationRepository(RateCacheWithKeys<ID, AmountPerDuration> rateCache) {
        this.rateCache = Objects.requireNonNull(rateCache);
    }

    @Override
    public Page<AmountPerDurationEntity<ID>> findAll(Pageable pageable) {
        return findAll(null, pageable);
    }

    public Page<AmountPerDurationEntity<ID>> findAll(
            @Nullable Example<AmountPerDurationEntity<ID>> example, Pageable pageable) {
        log.debug("Request to getPage rate-limit data: {}", pageable);

        final Page<AmountPerDurationEntity<ID>> result;

        final long offset = pageable.getOffset();
        final long pageSize = pageable.getPageSize();

        if(pageSize < 1 || offset < 0) {
            result = Page.empty(pageable);
        }else{

            // Though very sub-optimal, we first find all then sort everything,
            // before applying offset and pageSize
            final Iterable<AmountPerDurationEntity<ID>> found = example == null ? findAll() : findAll(example);
            final List<AmountPerDurationEntity<ID>> rateList = found instanceof List ?
                    (List<AmountPerDurationEntity<ID>>)found
                    : StreamSupport.stream(found.spliterator(), false).collect(Collectors.toList());

            final int total = rateList.size();
            if(offset >= total) {
                result = Page.empty(pageable);
            }else{

                final Sort sort = pageable.getSort();
                if(sort.isSorted() && !sort.isEmpty()) {
                    rateList.sort(new ComparatorFromSort<>(sort));
                }

                long end = offset + pageSize;
                if(end > total) {
                    end = total;
                }

                result = new PageImpl<>(rateList.subList((int)offset, (int)end), pageable, total);
            }
        }

        return result;
    }

    public Iterable<AmountPerDurationEntity<ID>> findAll(Example<AmountPerDurationEntity<ID>> example) {
        return findAll(new FilterFromExample<>(example));
    }

    @Override
    public Iterable<AmountPerDurationEntity<ID>> findAll(Sort sort) {
        return new Iterable<AmountPerDurationEntity<ID>>() {
            @Override
            public Iterator<AmountPerDurationEntity<ID>> iterator() {
                return StreamSupport.stream(findAll().spliterator(), false)
                        .sorted(new ComparatorFromSort<>(sort)).iterator();
            }
            @Override
            public Spliterator<AmountPerDurationEntity<ID>> spliterator() {
                return Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED);
            }
        };
    }

    @Override
    public <S extends AmountPerDurationEntity<ID>> S save(S s) {
        rateCache.put(s.getId(), s.value());
        return s;
    }

    @Override
    public <S extends AmountPerDurationEntity<ID>> Iterable<S> saveAll(Iterable<S> iterable) {
        List<S> saved = new ArrayList<>();
        iterable.forEach(toSave -> {
            this.rateCache.put(toSave.getId(), toSave.value());
            saved.add(toSave);
        });
        return saved;
    }

    @Override
    public Optional<AmountPerDurationEntity<ID>> findById(ID id) {
        AmountPerDuration rate = this.rateCache.get(id);
        return rate == null ? Optional.empty() : Optional.of(new AmountPerDurationEntity<>(id, rate));
    }

    @Override
    public boolean existsById(ID id) {
        return this.rateCache.containsKey(id);
    }

    @Override
    public Iterable<AmountPerDurationEntity<ID>> findAll() {
        return findAll((candidate) -> true);
    }

    @Override
    public Iterable<AmountPerDurationEntity<ID>> findAllById(Iterable<ID> iterable) {
        return new Iterable<AmountPerDurationEntity<ID>>() {
            @Override
            public Iterator<AmountPerDurationEntity<ID>> iterator() {
                return StreamSupport.stream(iterable.spliterator(), false)
                        .map(id -> {
                            AmountPerDuration value = id == null ? null : rateCache.get(id);
                            return value == null ? null : new AmountPerDurationEntity<>(id, value);
                        }).iterator();
            }
            @Override
            public Spliterator<AmountPerDurationEntity<ID>> spliterator() {
                return Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED);
            }
        };
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
    public void delete(AmountPerDurationEntity<ID> amountPerDurationEntity) {
        ID id = amountPerDurationEntity.getId();
        if (id != null) {
            deleteById(id);
            return;
        }
        findAll(amountPerDurationEntity::equals).forEach(found -> deleteById(found.getId()));
    }

    @Override
    public void deleteAll(Iterable<? extends AmountPerDurationEntity<ID>> iterable) {
        iterable.forEach(toDelete -> deleteById(toDelete.getId()));
    }

    @Override
    public void deleteAll() {
        this.rateCache.clear();
    }

    private Iterable<AmountPerDurationEntity<ID>> findAll(Predicate<AmountPerDurationEntity<ID>> filter) {
        return findAll(0, Long.MAX_VALUE, filter);
    }

    private Iterable<AmountPerDurationEntity<ID>> findAll(long offset, long limit, Predicate<AmountPerDurationEntity<ID>> filter) {
        final Iterable<ID> ids = this.rateCache.keys(offset, limit);
        if (log.isTraceEnabled()) {
            log.trace("Offset: {}, limit: {}, IDs: {}", offset, limit, ids);
        }

        return new Iterable<AmountPerDurationEntity<ID>>() {
            @Override
            public Iterator<AmountPerDurationEntity<ID>> iterator() {
                return StreamSupport.stream(findAllById(ids).spliterator(), false)
                        .filter(filter).iterator();
            }
            @Override
            public Spliterator<AmountPerDurationEntity<ID>> spliterator() {
                return Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED);
            }
        };
    }
}
