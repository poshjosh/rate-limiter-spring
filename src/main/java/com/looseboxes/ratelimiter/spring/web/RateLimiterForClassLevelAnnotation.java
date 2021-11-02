package com.looseboxes.ratelimiter.spring.web;

import com.looseboxes.ratelimiter.RateExceededExceptionThrower;
import com.looseboxes.ratelimiter.RateLimitExceededException;
import com.looseboxes.ratelimiter.RateLimiter;
import com.looseboxes.ratelimiter.RateSupplier;
import com.looseboxes.ratelimiter.rates.Rate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RateLimiterForClassLevelAnnotation implements RateLimiter<HttpServletRequest> {

    private static final Logger LOG = LoggerFactory.getLogger(RateLimiterForClassLevelAnnotation.class);

    private final ConcurrentMap<AnnotatedRequestMapping, RateLimiter<AnnotatedRequestMapping>> rateLimiters;

    public RateLimiterForClassLevelAnnotation(Rate first, Map<AnnotatedRequestMapping, Rate> limits) {
        this(() -> first, limits);
    }

    public RateLimiterForClassLevelAnnotation(RateSupplier rateSupplier, Map<AnnotatedRequestMapping, Rate> limits) {
        this(Util.createRateLimiters(limits, rateSupplier, new RateExceededExceptionThrower<>()));
    }

    public RateLimiterForClassLevelAnnotation(Map<AnnotatedRequestMapping, RateLimiter<AnnotatedRequestMapping>> rateLimiters) {
        this.rateLimiters = new ConcurrentHashMap<>(rateLimiters);
    }

    @Override
    public Rate record(HttpServletRequest request) throws RateLimitExceededException {
        final String requestURI = request.getRequestURI();
        LOG.trace("Rate limiting: {}", requestURI);
        Set<Map.Entry<AnnotatedRequestMapping, RateLimiter<AnnotatedRequestMapping>>> entrySet = rateLimiters.entrySet();
        for(Map.Entry<AnnotatedRequestMapping, RateLimiter<AnnotatedRequestMapping>> entry : entrySet) {
            AnnotatedRequestMapping annotatedRequestMapping = entry.getKey();
            if(annotatedRequestMapping.matchesStartOf(request)) {
                final RateLimiter<AnnotatedRequestMapping> rateLimiter = entry.getValue();
                final Rate result = rateLimiter.record(annotatedRequestMapping);
                LOG.trace("Result: {}, for rate limiting: {}", result, requestURI);
                return result;
            }
        }
        return Rate.NONE;
    }
}
