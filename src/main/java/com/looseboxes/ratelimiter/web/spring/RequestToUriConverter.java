package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.web.core.RequestToIdConverter;

import javax.servlet.http.HttpServletRequest;

public class RequestToUriConverter implements RequestToIdConverter<HttpServletRequest, String> {
    @Override
    public String convert(HttpServletRequest request) {
        return request.getRequestURI();
    }
}