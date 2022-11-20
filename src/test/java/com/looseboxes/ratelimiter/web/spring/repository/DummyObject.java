package com.looseboxes.ratelimiter.web.spring.repository;

import java.time.Instant;
import java.util.Objects;

final class DummyObject {
    private String name;
    private Boolean fieldNotNamedAfterAccessorMethods;
    private Integer stat;
    private Instant timeCreated;

    public DummyObject(String name, Boolean disabled, Integer stat, Instant timeCreated) {
        this.name = name;
        this.fieldNotNamedAfterAccessorMethods = disabled;
        this.stat = stat;
        this.timeCreated = timeCreated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getDisabled() {
        return fieldNotNamedAfterAccessorMethods;
    }

    public void setDisabled(Boolean disabled) {
        this.fieldNotNamedAfterAccessorMethods = disabled;
    }

    public Integer getStat() {
        return stat;
    }

    public void setStat(Integer stat) {
        this.stat = stat;
    }

    public Instant getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Instant timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DummyObject that = (DummyObject) o;
        return Objects.equals(name, that.name) && Objects.equals(fieldNotNamedAfterAccessorMethods, that.fieldNotNamedAfterAccessorMethods) && Objects.equals(stat, that.stat) && Objects.equals(timeCreated, that.timeCreated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, fieldNotNamedAfterAccessorMethods, stat, timeCreated);
    }

    @Override
    public String toString() {
        return "DummyObject{" +
            "name='" + name + '\'' +
            ", disabled=" + fieldNotNamedAfterAccessorMethods +
            ", stat=" + stat +
            ", timeCreated=" + timeCreated +
            '}';
    }
}
