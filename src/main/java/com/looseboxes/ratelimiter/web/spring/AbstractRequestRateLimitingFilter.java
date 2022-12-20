package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.RateLimiter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ConditionalOnProperty(prefix = "rate-limiter", name = "disabled", havingValue = "false")
public abstract class AbstractRequestRateLimitingFilter extends GenericFilterBean {

    private RateLimiter<HttpServletRequest> rateLimiter;

    protected AbstractRequestRateLimitingFilter() { }

    @Override
    protected void initFilterBean() throws ServletException {

        super.initFilterBean();

        final WebApplicationContext webApplicationContext =
                WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());

        rateLimiter = getRateLimiter(webApplicationContext);
    }

    private RateLimiter<HttpServletRequest> getRateLimiter(WebApplicationContext webApplicationContext) {
        if (webApplicationContext.getBeanNamesForType(RateLimiter.class).length > 0) {
            return webApplicationContext.getBean(RateLimiter.class);
        } else {
            return RateLimiter.noop();
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest) {

            final HttpServletRequest httpRequest = (HttpServletRequest)request;

            if (!rateLimiter.tryConsume(httpRequest)) {
                onLimitExceeded(httpRequest, (HttpServletResponse)response, chain);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    protected void onLimitExceeded(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException { }

    public RateLimiter<HttpServletRequest> getRateLimiter() {
        return rateLimiter;
    }
}
