package com.looseboxes.ratelimiter.spring.web;

import com.looseboxes.ratelimiter.*;
import com.looseboxes.ratelimiter.annotation.RateComposition;
import com.looseboxes.ratelimiter.rates.Rate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RateLimiterForMethodLevelAnnotation implements RateLimiter<HttpServletRequest> {

    private static final Logger LOG = LoggerFactory.getLogger(RateLimiterForMethodLevelAnnotation.class);

    private final AnnotatedRequestMapping [] requestMappings;
    private final RateLimiter<AnnotatedRequestMapping> [] rateLimiters;

    public RateLimiterForMethodLevelAnnotation(
            RateSupplier rateSupplier,
            RateExceededHandler rateExceededHandler,
            List<RateComposition<AnnotatedRequestMapping>> limits) {
        this(Util.createRateLimiters(rateSupplier, rateExceededHandler, limits));
    }

    public RateLimiterForMethodLevelAnnotation(Map<AnnotatedRequestMapping, RateLimiter<AnnotatedRequestMapping>> rateLimiters) {
        LOG.debug("Rate limiters: {}", rateLimiters);
        this.requestMappings = new AnnotatedRequestMapping[rateLimiters.size()];
        this.rateLimiters = new RateLimiter[rateLimiters.size()];
        int i = 0;
        Set<Map.Entry<AnnotatedRequestMapping, RateLimiter<AnnotatedRequestMapping>>> entrySet = rateLimiters.entrySet();
        for(Map.Entry<AnnotatedRequestMapping, RateLimiter<AnnotatedRequestMapping>> entry : entrySet) {
            this.requestMappings[i] = entry.getKey();
            this.rateLimiters[i] = entry.getValue();
            ++i;
        }
    }

    @Override
    public Rate record(HttpServletRequest request) throws RateLimitExceededException {
        final String requestURI = request.getRequestURI();
        LOG.trace("Invoking {} rate limiters for {}", rateLimiters.length, requestURI);
        for(int i=0; i<rateLimiters.length; i++) {
            AnnotatedRequestMapping annotatedRequestMapping = requestMappings[i];
            if(annotatedRequestMapping.matches(request)) {
                final RateLimiter<AnnotatedRequestMapping> rateLimiter = rateLimiters[i];
                final Rate result = rateLimiter.record(annotatedRequestMapping);
                LOG.trace("Result: {}, for rate limiting: {}", result, requestURI);
                return result;
            }
        }
        return Rate.NONE;
    }
}
