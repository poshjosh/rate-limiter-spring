package com.looseboxes.ratelimiter.web.spring.repository;

import com.looseboxes.ratelimiter.util.Experimental;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Experimental
public interface RateRepository<ID, E> {

    Optional<E> findById(ID id);

    List<E> findAll(Example<E> example);

    List<E> findAll();

    Page<E> findAll(Pageable pageable);

    Page<E> findAll(Example<E> example, Pageable pageable);
}
