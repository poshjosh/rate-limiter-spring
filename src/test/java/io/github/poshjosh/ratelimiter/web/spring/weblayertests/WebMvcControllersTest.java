package io.github.poshjosh.ratelimiter.web.spring.weblayertests;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@WebMvcTest
@ContextConfiguration
public @interface WebMvcControllersTest {

    @AliasFor(annotation = ContextConfiguration.class, attribute = "classes")
    Class<?>[] classes() default {  };

    @AliasFor(annotation = Import.class, attribute = "value")
    Class<?>[] imports() default { TestResourceLimiterConfiguration.class, RequestRateLimitingFilter.class };
}
