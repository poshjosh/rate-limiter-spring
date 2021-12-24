package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.*;
import com.looseboxes.ratelimiter.annotation.AnnotationProcessor;
import com.looseboxes.ratelimiter.annotation.ClassAnnotationProcessor;
import com.looseboxes.ratelimiter.cache.RateCache;
import com.looseboxes.ratelimiter.cache.MapRateCache;
import com.looseboxes.ratelimiter.util.Experimental;
import com.looseboxes.ratelimiter.web.core.*;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import com.looseboxes.ratelimiter.web.spring.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

@Configuration
public class RateLimiterConfiguration {

    public static final class RequestToUriConverter implements RequestToIdConverter<HttpServletRequest, String>{
        @Override
        public String convert(HttpServletRequest request) {
            return request.getRequestURI();
        }
    }

    public static final String DEFAULT_CACHE_NAME = "com.looseboxes.ratelimiter.web.spring.cache";

    @Bean
    public RateLimiter<HttpServletRequest> rateLimiter(
            RateLimitProperties properties,
            RateLimiterConfigurationSource<HttpServletRequest> rateLimiterConfigurationSource,
            ResourceClassesSupplier resourceClassesSupplier,
            AnnotationProcessor<Class<?>> annotationProcessor) {
        return new WebRequestRateLimiter<>(
                properties, rateLimiterConfigurationSource, resourceClassesSupplier.get(), annotationProcessor);
    }

    @Bean
    public RequestToIdConverter<HttpServletRequest, String> requestToIdConverter() {
        return new RequestToUriConverter();
    }

    @Bean
    public RateFactory rateFactory() {
        return  new LimitWithinDurationFactory();
    }

    @Bean
    public RateCache<Object, Object> rateCache(@Autowired(required = false) CacheManager cacheManager) {
        RateCache<Object, Object> rateCache = null;
        if(cacheManager != null) {
            Cache cache = cacheManager.getCache(DEFAULT_CACHE_NAME);
            if(cache != null) {
                rateCache = new SpringRateCache<>(cache);
            }
        }
        if(rateCache == null) {
            rateCache = new MapRateCache<>();
        }
        return new RateCacheWithKeysSupplier<>(rateCache);
    }

    @Bean
    public RateRecordedListener rateRecordedListener() {
        return new RateExceededExceptionThrower();
    }

    @Bean
    public RateLimiterConfigurationSource<HttpServletRequest> rateLimiterConfigurationSource(
            RequestToIdConverter<HttpServletRequest, String> requestToUriConverter,
            RateCache<Object, Object> rateCache,
            RateFactory rateFactory,
            RateRecordedListener rateRecordedListener,
            RateLimiterFactory<Object> rateLimiterFactory,
            @Autowired(required = false) RateLimiterConfigurer<HttpServletRequest> rateLimiterConfigurer) {

        return new RateLimiterConfigurationSource<>(
                requestToUriConverter, rateCache, rateFactory, rateRecordedListener, rateLimiterFactory,
                rateLimiterConfigurer, new ClassIdProvider(), new MethodIdProvider());
    }

    @Bean
    public RateLimiterFactory<Object> rateLimiterProvider() {
        return new DefaultRateLimiterFactory<>();
    }

    @Bean
    public ResourceClassesSupplier resourceClassesSupplier(RateLimitProperties properties) {
        return new DefaultResourceClassesSupplier(
                new ClassesInPackageFinderSpring(), properties.getResourcePackages(),
                Controller.class, RestController.class);
    }

    @Bean
    public AnnotationProcessor<Class<?>> annotationProcessor() {
        return new ClassAnnotationProcessor();
    }

    @Bean
    @Experimental
    // TODO This will not work if the user overrides the default case where we use only one cache
    public RateRepository<Object, LimitWithinDurationDTO<Object>> rateRepository(RateCache<Object, Object> rateCache) {
        final PageSupplier<Object> pageSupplier;
        if(rateCache instanceof RateCacheWithKeysSupplier) {
            pageSupplier = ((RateCacheWithKeysSupplier)rateCache).getKeysSupplier();
        }else{
            pageSupplier = (offset, limit) -> Collections.emptyList();
        }
        return new LimitWithinDurationRepository<>(rateCache, pageSupplier);
    }
}
