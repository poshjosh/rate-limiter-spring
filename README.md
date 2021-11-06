# rate limiter spring

Light weight rate limiting library for spring rest controllers.

Based on [rate-limiter](https://github.com/poshjosh/rate-limiter)

### Usage

__1. Annotate your spring application class as shown:__

```java

@SpringBootApplication(scanBasePackageClasses = {
        com.looseboxes.ratelimiter.spring.web.RateLimiterWebMvcConfigurer.class
})
@EnableConfigurationProperties({
        com.looseboxes.ratelimiter.spring.util.RateLimitProperties.class
})
@ServletComponentScan // Required for scanning of components like @WebListener
public class MySpringApplication {

}
```

__2. Add some required properties__

```yaml
rate-limiter:
  disabled: false
  # If using annotations, you have to specify one package where all the controllers should be scanned for
  controller-package: com.myapplicatioon.web.rest
```

__3. Add an exception handler for RateLimitException.__ 

[Exception handling for rest with Spring](https://www.baeldung.com/exception-handling-for-rest-with-spring)

__4. Annotate controller or separate methods.__

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

    public MyResource() {
    }

    @RateLimit(limit = 25, duration = 1000)
    @GetMapping("/greet/{name}")
    public ResponseEntity<String> greet(@PathVariable String name) {
        ResponseEntity.ok("Hello " + name);
    }
}
```

### Direct usage

You could use a `RateLimiter` directly.

The library provides a `RateLimiter<HttpServletRequest>` based on the limits specified in the properties file, for example: 

__1. Add some properties__

```yaml
rate-limiter:
  disabled: false
  rate-limits:
    per-second:
      # (Optional) A java.util.function.Function<HttpServletRequest, Object> 
      request-to-id-converter-function: 
      count: 90
      duration: 1
      time-unit: SECONDS
    per-minute:
      # (Optional) A java.util.function.Function<HttpServletRequest, Object> 
      request-to-id-converter-function:
      count: 300
      duration: 1
      time-unit: MINUTES
```

__2. Create and use the RateLimiter manually__

```java
import com.looseboxes.ratelimiter.RateLimitExceededException;
import com.looseboxes.ratelimiter.RateLimiter;
import com.looseboxes.ratelimiter.RateLimiterImpl;
import com.looseboxes.ratelimiter.rates.LimitWithinDuration;
import com.looseboxes.ratelimiter.spring.util.RateLimitProperties;
import com.looseboxes.ratelimiter.spring.util.RateLimitPropertiesSpring;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class DirectUsage {

    private final RateLimiter<HttpServletRequest> rateLimiter;

    public DirectUsage(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    public void rateLimit(HttpServletRequest request) throws RateLimitExceededException {
        rateLimiter.record(request);
    }
}
```

# Build

```sh
mvn clean install
```
