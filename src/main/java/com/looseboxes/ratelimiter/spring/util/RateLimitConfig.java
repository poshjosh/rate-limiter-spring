package com.looseboxes.ratelimiter.spring.util;

import com.looseboxes.ratelimiter.rates.LimitWithinDuration;
import com.looseboxes.ratelimiter.rates.Rate;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class RateLimitConfig {

    private int limit;
    private long duration;
    private TimeUnit timeUnit;
    private String requestToIdConverterFunction;

    public Rate toRate() {
        return new LimitWithinDuration(limit, timeUnit.toMillis(duration));
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public Optional<Function<HttpServletRequest, Object>> getRequestToIdConverterFunctionInstanceOptional() {
        try {
            final Function<HttpServletRequest, Object> function = requestToIdConverterFunction == null ? null :
                    (Function<HttpServletRequest, Object>) Class.forName(requestToIdConverterFunction)
                    .getConstructors()[0].newInstance();
            return Optional.ofNullable(function);
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getRequestToIdConverterFunction() {
        return requestToIdConverterFunction;
    }

    public void setRequestToIdConverterFunction(String requestToIdConverterFunction) {
        this.requestToIdConverterFunction = requestToIdConverterFunction;
    }

    @Override
    public String toString() {
        return "RateLimitConfig{" +
                "duration=" + duration +
                ", limit=" + limit +
                ", timeUnit=" + timeUnit +
                ", requestToIdConverterFunction='" + requestToIdConverterFunction + "'" +
                '}';
    }
}
