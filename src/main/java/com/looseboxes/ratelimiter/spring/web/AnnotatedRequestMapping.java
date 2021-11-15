package com.looseboxes.ratelimiter.spring.web;

import javax.servlet.http.HttpServletRequest;

public interface AnnotatedRequestMapping {

    AnnotatedRequestMapping NONE = new AnnotatedRequestMapping() {
        @Override
        public AnnotatedRequestMapping combine(String... uris) {
            return new AnnotatedRequestMappingImpl(uris);
        }

        @Override
        public boolean matches(String uri) {
            return false;
        }

        @Override
        public boolean matchesStartOf(String uri) {
            return false;
        }
    };

    AnnotatedRequestMapping combine(String... uris);

    default boolean matches(HttpServletRequest request) {
        return matches(request.getRequestURI());
    }

    boolean matches(String uri);

    default boolean matchesStartOf(HttpServletRequest request) {
        return matchesStartOf(request.getRequestURI());
    }

    boolean matchesStartOf(String uri);
}
