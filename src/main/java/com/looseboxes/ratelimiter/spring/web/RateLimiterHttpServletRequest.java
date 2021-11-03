package com.looseboxes.ratelimiter.spring.web;

import com.looseboxes.ratelimiter.*;
import com.looseboxes.ratelimiter.cache.RateCache;
import com.looseboxes.ratelimiter.rates.Rate;
import com.looseboxes.ratelimiter.spring.util.RateLimitProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.function.Function;

public class RateLimiterHttpServletRequest implements RateLimiter<HttpServletRequest> {

    private final Logger log = LoggerFactory.getLogger(RateLimiterHttpServletRequest.class);

    private final List<Function<HttpServletRequest, Object>> requestToIdConverterList;

    private final List<RateLimiter> rateLimiterList;

    public RateLimiterHttpServletRequest(
            RateLimitProperties properties,
            RateCache rateCache,
            RateSupplier rateSupplier,
            RateExceededHandler rateExceededHandler) {

        if(isDisabled(properties)) {
            requestToIdConverterList = Collections.emptyList();
            rateLimiterList = Collections.emptyList();
        }else{

            final Map<String, Rate> rateMap = properties.toRates();

            // We use the class to ensure equality
            final Map<Class, List<Rate>> converterClassToRateList = new HashMap<>();
            final Map<Function<HttpServletRequest, Object>, List<Rate>> converterToRateList = new HashMap<>();

            rateMap.forEach((name, rate) -> {

                Function<HttpServletRequest, Object> converterFunc = properties
                        .getRequestToIdConverterFunctionInstanceOptional(name).orElse(null);

                Class converterFuncClass = converterFunc == null ? null : converterFunc.getClass();

                List<Rate> limits = converterClassToRateList.get(converterFuncClass);
                if(limits == null) {
                    limits = new ArrayList<>();
                    converterClassToRateList.put(converterFuncClass, limits);
                    converterToRateList.put(converterFunc, limits);
                }

                limits.add(rate);
            });

            requestToIdConverterList = new ArrayList<>();
            rateLimiterList = new ArrayList<>();

            converterToRateList.forEach((requestToIdConverter, limits) -> {
                requestToIdConverterList.add(requestToIdConverter);
                rateLimiterList.add(createRateLimiter(rateCache, rateSupplier, limits, rateExceededHandler));
            });
        }

        if(requestToIdConverterList.size() != rateLimiterList.size()) {
            throw new AssertionError();
        }

        if(log.isDebugEnabled()) {
            for (int i = 0; i < rateLimiterList.size(); i++) {
                log.debug("Request to id converter: {}, RateLimiter: {}", requestToIdConverterList.get(i), rateLimiterList.get(i));
            }
        }
    }

    private RateLimiter createRateLimiter(
            RateCache rateCache, RateSupplier rateSupplier,
            Collection<Rate> limits, RateExceededHandler rateExceededHandler) {
        return new RateLimiterImpl(rateCache, rateSupplier, limits, rateExceededHandler);
    }

    private boolean isDisabled(RateLimitProperties properties) {
        return Boolean.TRUE.equals(properties.getDisabled());
    }

    public Rate record(HttpServletRequest request) throws RateLimitExceededException {

        final int size = requestToIdConverterList.size();

        Rate result = Rate.NONE;
        RateLimitExceededException exception = null;

        for(int i=0; i<size; i++) {

            final Function<HttpServletRequest, Object> requestToIdConverter = requestToIdConverterList.get(i);

            final Object id = requestToIdConverter == null ? request : requestToIdConverter.apply(request);

            try {
                Rate rate = rateLimiterList.get(i).record(id);

                if (i == 0) {
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
