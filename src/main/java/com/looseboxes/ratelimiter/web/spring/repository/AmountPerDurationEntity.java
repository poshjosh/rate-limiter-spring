package com.looseboxes.ratelimiter.web.spring.repository;

import com.looseboxes.ratelimiter.rates.AmountPerDuration;
import com.looseboxes.ratelimiter.util.Experimental;

import java.io.Serializable;
import java.util.Objects;

@Experimental
public class AmountPerDurationEntity<ID> implements Serializable {

    private ID id;

    private long amount;
    private long duration;
    private long timeCreated;

    public AmountPerDurationEntity(ID id, AmountPerDuration value) {
        this.id = id;
        this.amount = value.getAmount();
        this.duration = value.getDuration();
        this.timeCreated = value.getTimeCreated();
    }

    public AmountPerDuration value() {
        return new AmountPerDuration(this.amount, this.duration, this.timeCreated);
    }

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
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
        AmountPerDurationEntity that = (AmountPerDurationEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AmountPerDurationEntity{" +
                "id='" + id + '\'' +
                ", limit=" + getAmount() +
                ", duration=" + getDuration() +
                ", timeCreated=" + getTimeCreated() +
                '}';
    }
}