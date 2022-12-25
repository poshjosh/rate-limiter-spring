package com.looseboxes.ratelimiter.web.spring.repository;

import com.looseboxes.ratelimiter.annotations.Experimental;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@Experimental
@NoRepositoryBean
public interface RateRepository<E, ID> extends PagingAndSortingRepository<E, ID> {

    Iterable<E> findAll(Example<E> example);

    Page<E> findAll(Example<E> example, Pageable pageable);
}
