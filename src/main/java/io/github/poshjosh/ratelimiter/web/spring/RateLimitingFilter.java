package io.github.poshjosh.ratelimiter.web.spring;

import io.github.poshjosh.ratelimiter.RateLimiter;
import io.github.poshjosh.ratelimiter.web.core.RateLimiterConfigurer;
import io.github.poshjosh.ratelimiter.web.core.WebRateLimiterContext;
import io.github.poshjosh.ratelimiter.web.core.WebRateLimiterRegistries;
import io.github.poshjosh.ratelimiter.web.core.WebRateLimiterRegistry;
import io.github.poshjosh.ratelimiter.util.RateLimitProperties;
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

    private WebRateLimiterRegistry webRateLimiterRegistry;

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

        if (webRateLimiterRegistry == null) {
            WebRateLimiterContext context = rateLimiterContextBuilder().build();
            webRateLimiterRegistry = rateLimiterRegistry(context);
            LOG.info(context.isRateLimitingEnabled()
                    ? "Completed setup of automatic rate limiting" : "Rate limiting is disabled");
        }
    }

    protected WebRateLimiterRegistry rateLimiterRegistry(WebRateLimiterContext context) {
        return WebRateLimiterRegistries.of(context);
    }

    protected WebRateLimiterContext.Builder rateLimiterContextBuilder() {
        return WebRateLimiterContextSpring.builder()
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
        return webRateLimiterRegistry.tryAcquire(httpRequest, 1);
    }

    public RateLimiter getRateLimiter(HttpServletRequest request) {
        return webRateLimiterRegistry.getRateLimiterOrUnlimited(request);
    }

    public WebRateLimiterRegistry getRateLimiterRegistry() {
        return webRateLimiterRegistry;
    }
}
