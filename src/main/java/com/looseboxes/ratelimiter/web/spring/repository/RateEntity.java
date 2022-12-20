package com.looseboxes.ratelimiter.web.spring.repository;

import com.looseboxes.ratelimiter.Rate;
import com.looseboxes.ratelimiter.util.Experimental;

import java.util.Objects;

@Experimental
public class RateEntity<ID> {

    private final ID id;
    private final Rate rate;

    public RateEntity() {
        this(null);
    }
    public RateEntity(ID id) {
        this(id, Rate.NONE);
    }
    public RateEntity(ID id, Rate rate) {
        this.id = id;
        this.rate = rate;
    }

    public ID getId() { return id; }

    public Rate getRate() {
        return rate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RateEntity that = (RateEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "RateEntity{id=" + id + ", rate=" + rate + '}';
    }
}
