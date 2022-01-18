package com.looseboxes.ratelimiter.web.spring.weblayertests;

import com.looseboxes.ratelimiter.web.spring.RateLimitPropertiesSpring;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@EnableConfigurationProperties({ RateLimitPropertiesSpring.class })
public @interface WebMvcControllersTest {

    @AliasFor(annotation = ContextConfiguration.class, attribute = "classes")
    Class<?>[] contextConfiguration() default { WebLayerTestConfiguration.class, TestWebMvcConfigurer.class };

    @AliasFor(annotation = Import.class, attribute = "value")
    Class<?>[] controllers() default { };
}
