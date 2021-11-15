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

    private final RequestToIdConverter[] requestToIdConverters;

    private final RateLimiter[] rateLimiters;

    public RateLimiterHttpServletRequest(
            RateLimitProperties properties,
            RequestToIdConverterRegistry requestToIdConverterRegistry,
            RateCache rateCache,
            RateSupplier rateSupplier,
            RateExceededHandler rateExceededHandler) {
        if(isDisabled(properties)) {
            this.requestToIdConverters = new RequestToIdConverter[0];
            this.rateLimiters = new RateLimiter[0];
        }else {

            final Map<String, List<Rate>> limitMap = properties.toRateLists();

            final int size = limitMap.size();

            this.requestToIdConverters = new RequestToIdConverter[size];
            this.rateLimiters = new RateLimiter[0];

            int i = 0;
            Set<Map.Entry<String, List<Rate>>> entrySet = limitMap.entrySet();
            for(Map.Entry<String, List<Rate>> entry : entrySet) {
                String name = entry.getKey();
                RequestToIdConverter requestToIdConverter = requestToIdConverterRegistry.getConverter(name);
                if(requestToIdConverter == null) {
                    throw new IllegalStateException(
                            String.format("For key: %s, could not find any %s instance in registry %s",
                                    name, RequestToIdConverter.class.getSimpleName(), RequestToIdConverterRegistry.class)
                    );
                }
                List<Rate> limits = entry.getValue();
                Rates.Logic logic = properties.getLogic(name);
                RateLimiter rateLimiter = new RateLimiterImpl(rateCache, rateSupplier, logic, rateExceededHandler, limits.toArray(new Rate[0]));
                LOG.debug("Request to id converter: {}, RateLimiter: {}", requestToIdConverter, rateLimiter);

                ++i;
            }
        }
    }

    private boolean isDisabled(RateLimitProperties properties) {
        return Boolean.TRUE.equals(properties.getDisabled());
    }

    public Rate record(HttpServletRequest request) throws RateLimitExceededException {

        Rate result = null;
        RateLimitExceededException exception = null;

        for(int i=0; i<rateLimiters.length; i++) {
            RequestToIdConverter requestToIdConverter = requestToIdConverters[i];
            Object id = requestToIdConverter.convert(request);
            if(id == null) {
                continue;
            }
            RateLimiter rateLimiter = rateLimiters[i];
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
