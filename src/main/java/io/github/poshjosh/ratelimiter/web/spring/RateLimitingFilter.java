package io.github.poshjosh.ratelimiter.web.spring;

import io.github.poshjosh.ratelimiter.RateLimiterFactory;
import io.github.poshjosh.ratelimiter.web.core.RateLimiterConfigurer;
import io.github.poshjosh.ratelimiter.web.core.RateLimiterContext;
import io.github.poshjosh.ratelimiter.web.core.RateLimiterRegistry;
import io.github.poshjosh.ratelimiter.web.core.util.RateLimitProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

public abstract class RateLimitingFilter extends GenericFilterBean {

    private static final Logger LOG = LoggerFactory.getLogger(RateLimitingFilter.class);

    private final RateLimitProperties properties;
    private final RateLimiterConfigurer configurer;

    private RateLimiterRegistry rateLimiterRegistry;
    private RateLimiterFactory<HttpServletRequest> rateLimiterFactory;

    protected RateLimitingFilter() {
        this(registries -> {});
    }

    protected RateLimitingFilter(RateLimitProperties properties) {
        this(properties, registries -> {});
    }

    protected RateLimitingFilter(RateLimiterConfigurer configurer) {
        this(new RateLimitPropertiesSpring(), configurer);
    }

    protected RateLimitingFilter(
            RateLimitProperties properties, RateLimiterConfigurer configurer) {
        this.properties = Objects.requireNonNull(properties);
        this.configurer = Objects.requireNonNull(configurer);
    }

    /**
     * Called when a limit is exceeded.
     */
    protected abstract void onLimitExceeded(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException;

    @Override
    protected void initFilterBean() throws ServletException {

        super.initFilterBean();

        RateLimiterContext config = rateLimiterContextBuilder().build();

        if (rateLimiterRegistry == null) {
            rateLimiterRegistry = rateLimiterRegistry(config);
        }

        if (rateLimiterFactory != null) {
            return;
        }

        if (config.getResourceClassesSupplier().get().isEmpty()) {
            rateLimiterFactory = RateLimiterFactory.noop();
        } else {
            rateLimiterFactory = rateLimiterRegistry.createRateLimiterFactory();
        }
        LOG.info(rateLimiterRegistry.isRateLimitingEnabled()
                ? "Completed setup of automatic rate limiting" : "Rate limiting is disabled");
    }

    protected RateLimiterRegistry rateLimiterRegistry(RateLimiterContext config) {
        return RateLimiterRegistrySpring.of(config);
    }

    protected RateLimiterContext.Builder rateLimiterContextBuilder() {
        return RateLimiterContextSpring.builder()
                .properties(properties).configurer(configurer);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest) {

            final HttpServletRequest httpRequest = (HttpServletRequest)request;

            if (!tryConsume(httpRequest)) {
                onLimitExceeded(httpRequest, (HttpServletResponse)response, chain);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    protected boolean tryConsume(HttpServletRequest httpRequest) {
        return getRateLimiterFactory().getRateLimiter(httpRequest).tryAcquire();
    }

    public RateLimiterRegistry getRateLimiterRegistry() {
        return rateLimiterRegistry;
    }

    public RateLimiterFactory<HttpServletRequest> getRateLimiterFactory() { return rateLimiterFactory; }
}
