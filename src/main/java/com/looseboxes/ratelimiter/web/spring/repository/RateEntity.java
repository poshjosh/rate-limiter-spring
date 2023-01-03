package com.looseboxes.ratelimiter.web.spring.repository;

import com.looseboxes.ratelimiter.annotations.Experimental;
import com.looseboxes.ratelimiter.bandwidths.Bandwidths;

import java.util.Objects;

/** Experimental */
@Experimental
public class RateEntity<ID> {

    private final ID id;
    private final Bandwidths data;

    public RateEntity(ID id, Bandwidths data) {
        this.id = id;
        this.data = data;
    }

    public ID getId() { return id; }

    public Object getData() {
        return data;
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
        return "RateEntity{id=" + id + ", data=" + data + '}';
    }
}
