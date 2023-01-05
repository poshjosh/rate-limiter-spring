package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.annotation.Element;
import com.looseboxes.ratelimiter.util.Matcher;
import com.looseboxes.ratelimiter.web.core.MatcherFactory;
import com.looseboxes.ratelimiter.web.core.RequestToIdConverter;
import com.looseboxes.ratelimiter.web.core.ResourceLimiterConfig;
import com.looseboxes.ratelimiter.web.core.util.PathPatterns;
import com.looseboxes.ratelimiter.web.core.util.PathPatternsMatcher;
import com.looseboxes.ratelimiter.web.core.util.PathPatternsProvider;
import com.looseboxes.ratelimiter.web.spring.uri.SpringPathPatternsProvider;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public abstract class ResourceLimiterConfigSpring
        extends ResourceLimiterConfig<HttpServletRequest> {

    private static PathPatternsProvider pathPatternsProvider = new SpringPathPatternsProvider();
    private static RequestToIdConverter<HttpServletRequest, String> requestToUriConverter =
            new RequestToUriConverter();

    private static final class MatcherFactorySpring
            implements MatcherFactory<HttpServletRequest, Element> {
        @Override
        public Optional<Matcher<HttpServletRequest, ?>> createMatcher(String name, Element e) {
            PathPatterns<String> pathPatterns = pathPatternsProvider.get(e);
            return Optional.of(new PathPatternsMatcher<>(pathPatterns, requestToUriConverter));
        }
    }
    
    public static Builder<HttpServletRequest> builder() {
        return ResourceLimiterConfig.<HttpServletRequest>builder()
            .matcherFactory(new MatcherFactorySpring())
            //.pathPatternsProvider(pathPatternsProvider)
            //.requestToIdConverter(requestToUriConverter)
            .classesInPackageFinder(new ClassesInPackageFinderSpring());

    }
}
