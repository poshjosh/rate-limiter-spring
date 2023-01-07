# rate limiter spring

Light-weight rate limiting library for spring controllers, based on 
[rate-limiter-web-core](https://github.com/poshjosh/rate-limiter-web-core).

Please first read the [rate-limiter-web-core documentation](https://github.com/poshjosh/rate-limiter-web-core).

### Usage

__1. Configure your spring application__

```java
package com.myapplicatioon;

import javax.servlet.*;

import ResourceLimitingFilter;
import ResourceLimiterConfiguration;

@SpringBootApplication(scanBasePackageClasses = { ResourceLimiterConfiguration.class,
        MySpringApplication.class }) public class MySpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(MySpringApplication.class, args);
    }

    @Component public static class MySpringApplicationFilter extends ResourceLimitingFilter {
        @Override protected void onLimitExceeded(HttpServletRequest request,
                HttpServletResponse response, FilterChain chain) {
            response.sendError(429, "Too many requests");
        }
    }
}
```

At this point your application is ready to enjoy the benefits of rate limiting

__2. Annotate classes and/or methods.__

```java
package com.myapplicatioon.web.rest;

import Rate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/my-resources") public class MyResource {

    // Only 25 calls per second
    @Rate(25) @GetMapping("/greet/{name}") public ResponseEntity<String> greet(
            @PathVariable String name) {
        return ResponseEntity.ok("Hello " + name);
    }
}
```

__3. (Optional) Add rate-limiter properties__

```yaml
rate-limiter:
  resource-packages: com.myapplicatioon.web.rest
  rate-limit-configs:
    task_queue: # Accept only 2 tasks per second 
      permits: 2
      duration: PT1S
    video_download: # Cap streaming of video to 5kb per second
      permits: 5000
      duration: PT1S
    com.myapplicatioon.web.rest.MyResource: # Limit requests to this resource to 10 per minute
      permits: 10
      duration: PT1M 
```

### Fine-grained configuration of rate limiting

Configure rate limiting as described in the [rate-limiter-web-core documentation](https://github.com/poshjosh/rate-limiter-web-core).

When you configure rate limiting using properties, you could:

- Rate limit a class from properties by using the class ID.

- Rate limit a method from properties by using the method ID.

```java
public class RateLimitPropertiesImpl implements RateLimitProperties {
  @Override
  public Map<String, Rates> getRateLimitConfigs() {
    
    Map<String, Rates> ratesMap = new HashMap<>();
    
    // Rate limit a class
    ratesMap.put(ElementId.of(MyResource.class), Rates.of(Rate.ofMinutes(10)));

    // Rate limit a method
    ratesMap.put(ElementId.of(MyResource.class.getMethod("greet", String.class)), Rates.of(Rate.ofMinutes(10)));
    
    return ratesMap;
  }
}
```

### Manually create and use a ResourceLimiter

Usually, you are provided with appropriate `ResourceLimiter`s based on the annotations 
and properties you specify. However, you could manually create and use `ResourceLimiters`.

```java
import io.github.poshjosh.ratelimiter.web.spring.ResourceLimiterRegistrySpring;

public class ResourceLimiterProvider {

    public ResourceLimiter createResourceLimiter() {
        return ResourceLimiterRegistrySpring.ofDefaults().createResourceLimiter();
    }
}
```
This way you use the `ResourceLimiter` as you see fit.

### Annotation Specifications

Please read the [annotation specs](https://github.com/poshjosh/rate-limiter-annotation/blob/main/docs/ANNOTATION_SPECS.md). It is concise.

Enjoy! :wink:
