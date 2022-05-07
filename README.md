# rate limiter spring

Light-weight rate limiting library for spring controllers, based on 
[rate-limiter-web-core](https://github.com/poshjosh/rate-limiter-web-core).

Please first read the [rate-limiter-web-core documentation](https://github.com/poshjosh/rate-limiter-web-core).

### Usage

__1a. Extend `com.looseboxes.ratelimiter.web.spring.AbstractRateLimiterWebMvcConfigurer`__

This way a rate limiter will be created an automatically applied based on rate limiter related properties and annotations.

```java
import com.looseboxes.ratelimiter.RateLimiter;
import com.looseboxes.ratelimiter.web.spring.AbstractRateLimiterWebMvcConfigurer;
import com.looseboxes.ratelimiter.web.spring.RateLimiterWebMvcConfigurer;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class RateLimiterWebMvcConfigurer extends AbstractRateLimiterWebMvcConfigurer {

    public RateLimiterWebMvcConfigurer(RateLimiter<HttpServletRequest> rateLimiter) {
        super(rateLimiter);
    }
}
```

__1a. Alternatively, autowire the provided `RateLimiter<HttpServletRequest>` bean__

This way you use the `RateLimiter` as you see fit.

__2. Annotate your spring application class as shown:__

```java

import com.looseboxes.ratelimiter.web.spring.RateLimitPropertiesSpring;

@SpringBootApplication(scanBasePackageClasses = {RateLimiterWebMvcConfigurer.class})
@EnableConfigurationProperties({RateLimitPropertiesSpring.class})
@ServletComponentScan // Required for scanning of components like @WebListener
public class MySpringApplication {

}
```

__3. Add required rate-limiter properties__

```yaml
rate-limiter:
  # If using annotations, you have to specify the list packages where resources 
  # that may contain the rate-limit related annotations should be scanned for.
  resource-packages: com.myapplicatioon.web.rest
```

__4. Annotate classes and/or methods.__

```java
import com.looseboxes.ratelimiter.annotation.RateLimit;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RateLimit(limit = 100, duration = 1000)
@RestController
@RequestMapping("/my-resources")
public class MyResource {

    // Only 25 calls per second
    @RateLimit(limit = 25, duration = 1000)
    @GetMapping("/greet/{name}")
    public ResponseEntity<String> greet(@PathVariable String name) {
        return ResponseEntity.ok("Hello " + name);
    }
}
```

__5. Configure rate limiting__

Configure rate limiting as described in the [rate-limiter-web-core documentation](https://github.com/poshjosh/rate-limiter-web-core). 

In addition, you could use spring specific features, like `com.looseboxes.ratelimiter.web.spring.SpringRateCache`

__Notes:__

You can configure rate limiting from the properties file.

```yaml
rate-limiter:
  resource-packages: com.myapplicatioon.web.rest
  rate-limit-configs:
    com.myapplicatioon.web.rest.MyResource: # This is the group name
      limits:
        -
          limit: 25
          duration: PT1S
```

  - By using the fully qualified class name as the group name we can configure rate limiting
of specific resources from application configuration properties.

  - You could also narrow the specified properties to a specific method. For example, in this case,
by using `com.myapplicatioon.web.rest.MyResource.greet(java.lang.String)` as the group name.

Enjoy! :wink:

