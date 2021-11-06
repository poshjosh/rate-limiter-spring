package com.looseboxes.ratelimiter.spring.web;

import com.looseboxes.ratelimiter.*;
import com.looseboxes.ratelimiter.cache.RateCache;
import com.looseboxes.ratelimiter.rates.Rate;
import com.looseboxes.ratelimiter.rates.Rates;
import com.looseboxes.ratelimiter.spring.util.RateLimitProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class RateLimiterHttpServletRequest implements RateLimiter<HttpServletRequest> {

    private static final Logger LOG = LoggerFactory.getLogger(RateLimiterHttpServletRequest.class);

    private final Map<String, RateLimiter> rateLimiterMap;

    private final RequestToIdConverterRegistry requestToIdConverterRegistry;

    public RateLimiterHttpServletRequest(
            RateLimitProperties properties,
            RequestToIdConverterRegistry requestToIdConverterRegistry,
            RateCache rateCache,
            RateSupplier rateSupplier,
            RateExceededHandler rateExceededHandler) {
        this.requestToIdConverterRegistry = requestToIdConverterRegistry;
        if(isDisabled(properties)) {
            rateLimiterMap = Collections.emptyMap();
        }else {

            final Map<String, List<Rate>> limitMap = properties.toRateLists();

            final int size = limitMap.size();

            this.rateLimiterMap = new LinkedHashMap<>(size, 1.0f);

            limitMap.forEach((name, limits) -> {
                RequestToIdConverter requestToIdConverter = requestToIdConverterRegistry.getConverter(name);
                Rates.Logic logic = properties.getLogic(name);
                RateLimiter rateLimiter = new RateLimiterImpl(rateCache, rateSupplier, logic, limits, rateExceededHandler);
                LOG.debug("Request to id converter: {}, RateLimiter: {}", requestToIdConverter, rateLimiter);
                this.rateLimiterMap.put(name, rateLimiter);
            });
        }
    }

    private boolean isDisabled(RateLimitProperties properties) {
        return Boolean.TRUE.equals(properties.getDisabled());
    }

    public Rate record(HttpServletRequest request) throws RateLimitExceededException {

        Rate result = null;
        RateLimitExceededException exception = null;

        final Set<Map.Entry<String, RateLimiter>>  entrySet = rateLimiterMap.entrySet();

        for(Map.Entry<String, RateLimiter> entry : entrySet) {
            RequestToIdConverter requestToIdConverter = requestToIdConverterRegistry.getConverter(entry.getKey());
            if(requestToIdConverter == null) {
                throw new IllegalStateException(
                        String.format("For key: %s, could not find any %s instance in registry %s",
                                entry.getKey(), RequestToIdConverter.class.getName(), RequestToIdConverterRegistry.class)
                );
            }
            Object id = requestToIdConverter.convert(request);
            if(id == null) {
                continue;
            }
            RateLimiter rateLimiter = entry.getValue();
            try {
                Rate rate = rateLimiter.record(id);
                if(result == null) {
                    result = rate;
                }
            }catch(RateLimitExceededException e) {
                if(exception == null) {
                    exception = e;
                }else{
                    exception.addSuppressed(e);
                }
            }
        }

        if(exception != null) {
            throw exception;
        }

        return result;
    }
}
