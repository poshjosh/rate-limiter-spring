package io.github.poshjosh.ratelimiter.web.spring;

import io.github.poshjosh.ratelimiter.ResourceLimiter;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterConfig;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterConfigurer;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterRegistry;
import io.github.poshjosh.ratelimiter.web.core.util.RateLimitProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

public abstract class ResourceLimitingFilter extends GenericFilterBean {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceLimitingFilter.class);

    private final RateLimitProperties properties;
    private final ResourceLimiterConfigurer configurer;

    private ResourceLimiterRegistry resourceLimiterRegistry;
    private ResourceLimiter<HttpServletRequest> resourceLimiter;

    protected ResourceLimitingFilter() {
        this(registries -> {});
    }

    protected ResourceLimitingFilter(RateLimitProperties properties) {
        this(properties, registries -> {});
    }

    protected ResourceLimitingFilter(ResourceLimiterConfigurer configurer) {
        this(new RateLimitPropertiesSpring(), configurer);
    }

    protected ResourceLimitingFilter(
            RateLimitProperties properties, ResourceLimiterConfigurer configurer) {
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

        ResourceLimiterConfig config = resourceLimiterConfigBuilder().build();

        if (resourceLimiterRegistry == null) {
            resourceLimiterRegistry = resourceLimiterRegistry(config);
        }

        if (resourceLimiter != null) {
            return;
        }

        if (config.getResourceClassesSupplier().get().isEmpty()) {
            resourceLimiter = ResourceLimiter.noop();
        } else {
            resourceLimiter = resourceLimiterRegistry.createResourceLimiter();
        }
        LOG.info(resourceLimiterRegistry.isRateLimitingEnabled()
                ? "Completed setup of automatic rate limiting" : "Rate limiting is disabled");
    }

    protected ResourceLimiterRegistry resourceLimiterRegistry(ResourceLimiterConfig config) {
        return ResourceLimiterRegistrySpring.of(config);
    }

    protected ResourceLimiterConfig.Builder resourceLimiterConfigBuilder() {
        return ResourceLimiterConfigSpring.builder()
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
        return getResourceLimiter().tryConsume(httpRequest);
    }

    public ResourceLimiterRegistry getResourceLimiterRegistry() {
        return resourceLimiterRegistry;
    }

    public ResourceLimiter<HttpServletRequest> getResourceLimiter() { return resourceLimiter; }
}
