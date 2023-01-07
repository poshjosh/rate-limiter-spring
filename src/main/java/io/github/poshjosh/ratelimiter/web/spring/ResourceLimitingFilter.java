package io.github.poshjosh.ratelimiter.web.spring;

import io.github.poshjosh.ratelimiter.ResourceLimiter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ConditionalOnProperty(prefix = "rate-limiter", name = "disabled", havingValue = "false")
public abstract class ResourceLimitingFilter extends GenericFilterBean {

    private ResourceLimiter<HttpServletRequest> resourceLimiter;

    protected ResourceLimitingFilter() { }

    /**
     * Called when a limit is exceeded.
     */
    protected abstract void onLimitExceeded(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException;

    @Override
    protected void initFilterBean() throws ServletException {

        super.initFilterBean();

        final WebApplicationContext webApplicationContext =
                WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());

        resourceLimiter = getResourceLimiter(webApplicationContext);
    }

    private ResourceLimiter<HttpServletRequest> getResourceLimiter(WebApplicationContext webApplicationContext) {
        if (webApplicationContext.getBeanNamesForType(ResourceLimiter.class).length > 0) {
            return webApplicationContext.getBean(ResourceLimiter.class);
        } else {
            return ResourceLimiter.noop();
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest) {

            final HttpServletRequest httpRequest = (HttpServletRequest)request;

            if (!resourceLimiter.tryConsume(httpRequest)) {
                onLimitExceeded(httpRequest, (HttpServletResponse)response, chain);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    public ResourceLimiter<HttpServletRequest> getResourceLimiter() {
        return resourceLimiter;
    }
}
