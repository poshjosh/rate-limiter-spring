# rate limiter spring

Light-weight rate limiting library for spring controllers, based on 
[rate-limiter-web-core](https://github.com/poshjosh/rate-limiter-web-core).

Please first read the [rate-limiter-web-core documentation](https://github.com/poshjosh/rate-limiter-web-core).

### Usage

__1. Annotate your spring application class as shown:__

```java

import com.looseboxes.ratelimiter.web.spring.RateLimitPropertiesSpring;
import com.looseboxes.ratelimiter.web.spring.RateLimiterWebMvcConfigurer;

@SpringBootApplication(scanBasePackageClasses = {
        RateLimiterWebMvcConfigurer.class
})
@EnableConfigurationProperties({
        RateLimitPropertiesSpring.class
})
@ServletComponentScan // Required for scanning of components like @WebListener
public class MySpringApplication {

}
```

__2. Add some required properties__

```yaml
rate-limiter:
  # If using annotations, you have to specify the list packages where resources 
  # that may contain the rate-limit related annotations should be scanned for.
  resource-packages: com.myapplicatioon.web.rest
```

__3. Add an exception handler for RateExceededException.__ 

[Exception handling for rest with Spring](https://www.baeldung.com/exception-handling-for-rest-with-spring)

__4. Annotate controller or separate methods. (Optional)__

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

__5. Add some properties (Optional)__

You can configure rate limiting from the properties file. 

```yaml
rate-limiter:
  resource-packages: com.myapplicatioon.web.rest
  rate-limit-configs:
    com.myapplicatioon.web.rest.MyResource: # This is the group name
      limits:
        -
          limit: 25
          duration: 1
          timeout: SECONDS
```

By using the fully qualified class name as the group name we can configure rate limiting 
of specific resources from application configuration properties.

We could also narrow the specified properties to a specific method. For example, in this case,
by using `com.myapplicatioon.web.rest.MyResource.greet(java.lang.String)` as the group name.

__6. Configure rate limiting__

Configure rate limiting as described in the [rate-limiter-web-core documentation](https://github.com/poshjosh/rate-limiter-web-core). 

Enjoy! :wink:

