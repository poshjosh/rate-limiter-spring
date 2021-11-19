package com.looseboxes.ratelimiter.web.spring.repository;

import com.looseboxes.ratelimiter.rates.LimitWithinDuration;

import java.io.Serializable;
import java.util.Objects;

public class LimitWithinDurationDTO<ID> implements Serializable {

    private ID id;

    private int limit;
    private long duration;
    private long timeCreated;

    public LimitWithinDurationDTO() {
        this(null, new LimitWithinDuration());
    }

    public LimitWithinDurationDTO(ID id, LimitWithinDuration delegate) {
        this.id = id;
        this.limit = delegate.getLimit();
        this.duration = delegate.getDuration();
        this.timeCreated = delegate.getTimeCreated();
    }

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LimitWithinDurationDTO that = (LimitWithinDurationDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "LimitWithinDurationDTO{" +
                "id='" + id + '\'' +
                ", limit=" + getLimit() +
                ", duration=" + getDuration() +
                ", timeCreated=" + getTimeCreated() +
                '}';
    }
}