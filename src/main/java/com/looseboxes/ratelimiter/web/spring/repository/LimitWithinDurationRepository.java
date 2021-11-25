package com.looseboxes.ratelimiter.web.spring.repository;

import com.looseboxes.ratelimiter.cache.RateCache;
import com.looseboxes.ratelimiter.rates.LimitWithinDuration;
import com.looseboxes.ratelimiter.rates.Rate;
import com.looseboxes.ratelimiter.util.Experimental;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;

import java.util.*;
import java.util.function.Predicate;

@Experimental
public class LimitWithinDurationRepository<ID> implements RateRepository<ID, LimitWithinDurationDTO<ID>> {

    private final Logger log = LoggerFactory.getLogger(LimitWithinDurationRepository.class);

    private final RateCache<ID> rateCache;

    public LimitWithinDurationRepository(RateCache<ID> rateCache) {
        this.rateCache = Objects.requireNonNull(rateCache);
    }

    @Override
    public Optional<LimitWithinDurationDTO<ID>> findById(ID id) {
        Rate rate = this.rateCache.get(id);
        return rate == null ? Optional.empty() : Optional.of(toDto(id, rate));
    }

    @Override
    public Page<LimitWithinDurationDTO<ID>> findAll(Pageable pageable) {
        return findAll(null, pageable);
    }

    @Override
    public Page<LimitWithinDurationDTO<ID>> findAll(Example<LimitWithinDurationDTO<ID>> example, Pageable pageable) {
        log.debug("Request to get rate-limit data: {}", pageable);

        final Page<LimitWithinDurationDTO<ID>> result;

        final long offset = pageable.getOffset();
        final long pageSize = pageable.getPageSize();

        if(pageSize < 1 || offset < 0) {
            result = Page.empty(pageable);
        }else{
            final List<LimitWithinDurationDTO<ID>> rateList = example == null ? findAll() : findAll(example);

            log.debug("Found {} rates for {}", rateList.size(), example);

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

    private List<LimitWithinDurationDTO<ID>> findAll(Example<LimitWithinDurationDTO<ID>> example) {
        return findAll(new FilterFromExample<>(example));
    }

    private List<LimitWithinDurationDTO<ID>> findAll() {
        return findAll(limitWithinDurationDTO -> true);
    }

    private List<LimitWithinDurationDTO<ID>> findAll(Predicate<LimitWithinDurationDTO<ID>> filter) {
        final List<LimitWithinDurationDTO<ID>> rateList = new ArrayList<>();
        rateCache.forEach((id, rate) -> {
            LimitWithinDurationDTO<ID> dto = toDto(id, rate);
            if (filter.test(dto)) {
                rateList.add(dto);
            }
        });
        return rateList;
    }

    private LimitWithinDurationDTO<ID> toDto(ID id, Rate rate) {
        return new LimitWithinDurationDTO<>(id, (LimitWithinDuration) rate);
    }
}
