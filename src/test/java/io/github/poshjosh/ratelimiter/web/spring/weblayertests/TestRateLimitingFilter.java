package io.github.poshjosh.ratelimiter.web.spring.weblayertests;

import io.github.poshjosh.ratelimiter.store.BandwidthsStore;
import io.github.poshjosh.ratelimiter.web.core.WebRateLimiterContext;
import io.github.poshjosh.ratelimiter.web.spring.RateLimitPropertiesSpring;
import io.github.poshjosh.ratelimiter.web.spring.RateLimitingFilter;
import io.github.poshjosh.ratelimiter.web.spring.weblayertests.performance.Usage;
import io.github.poshjosh.ratelimiter.web.spring.weblayertests.performance.RateLimiterUsageRecorder;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

@TestComponent
public class TestRateLimitingFilter extends RateLimitingFilter {

    private final BandwidthsStore<String> bandwidthsStore;

    public TestRateLimitingFilter(
            RateLimitPropertiesSpring properties, BandwidthsStore<String> bandwidthsStore) {
        super(properties);
        // Some test classes initialize resource class/packages as required
        // In which case we do not override
        if (properties.getResourceClasses().isEmpty() && properties.getResourcePackages().isEmpty()) {
            properties.setResourcePackages(
                    Collections.singletonList(AbstractResourceTest.class.getPackage().getName()));
        }
        this.bandwidthsStore = Objects.requireNonNull(bandwidthsStore);
    }

    protected boolean tryConsume(HttpServletRequest httpRequest) {
        Usage bookmark = Usage.bookmark();
        final boolean result = super.tryConsume(httpRequest);
        RateLimiterUsageRecorder.record(bookmark.current());
        return result;
    }

    @Override
    protected void onLimitExceeded(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException {
        response.sendError(
                HttpStatus.TOO_MANY_REQUESTS.value(),
                HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase());
    }

    @Override
    protected WebRateLimiterContext.Builder rateLimiterContextBuilder() {
        return super.rateLimiterContextBuilder() .store(bandwidthsStore);
    }
}
