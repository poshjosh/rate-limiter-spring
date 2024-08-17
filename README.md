# rate limiter spring

Enterprise rate limiter for spring web apps, based on 
[rate-limiter-web-core](https://github.com/poshjosh/rate-limiter-web-core).

We believe that rate limiting should be as simple as:

```java
@Rate("10/s") // 10 permits per second for all methods in this class
@Controller
@RequestMapping("/api/v1")
public class GreetingResource {

    @Rate(permits=10, when="web.request.user.role = GUEST")
    @GetMapping("/smile")
    public String smile() {
        return ":)";
    }

    @Rate(permits=1, when="jvm.memory.available < 1gb")
    @GetMapping("/greet")
    public String greet(@RequestParam("who") String who) {
        return "Hello " + who;
    }
}
```

Please first read the [rate-limiter-web-core documentation](https://github.com/poshjosh/rate-limiter-web-core).

To add a dependency on `rate-limiter-spring` using Maven, use the following:

```xml
        <dependency>
            <groupId>io.github.poshjosh</groupId>
            <artifactId>rate-limiter-spring</artifactId>
            <version>0.8.0</version> 
        </dependency>
```

### Usage (Springframework)

Note: Spring boot usage is in the next section.

__1. Implement `RateLimitProperties`__

```java
@Component
public class RateLimitPropertiesImpl implements RateLimitProperties {

    // If not using annotations, return an empty list
    @Override 
    public List<String> getResourcePackages() {
        return Collections.singletonList("com.myapp.web.rest");
    }

    // If not using properties, return an empty map
    @Override 
    public List<Rates> getRateLimitConfigs() {
        // Accept only 2 tasks per second
        return Collections.singletonList(Rates.of("task_queue", Rate.ofSeconds(2)));
    }
}
```

__2. Extend `RateLimitingFilter`__

```java
@Component 
public class RateLimitingFilterImpl extends RateLimitingFilter {
    public RateLimitingFilterImpl(RateLimitProperties properties) {
        super(properties);
    }
    @Override 
    protected void onLimitExceeded(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain) throws IOException {
        response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(),
                HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase());
    }
}
```

At this point, your application is ready to enjoy the benefits of rate limiting.

__3. Annotate classes and/or methods.__

```java
package com.myapp.web.rest;

import io.github.poshjosh.ratelimiter.model.Rate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/my-resources") public class MyResource {

    // Only 25 calls per second for users in role GUEST
    @Rate(permits = 25, when = "web.request.user.role = GUEST") @GetMapping("/greet/{name}") public ResponseEntity<String> greet(
            @PathVariable String name) {
        return ResponseEntity.ok("Hello " + name);
    }
}
```

### Spring Boot

__1. Configure your spring application__

```java
@SpringBootApplication
@EnableConfigurationProperties(MyApp.MyRateLimitProperties.class)
public class MyApp {

    public static void main(String[] args) {
        SpringApplication.run(MyApp.class, args);
    }

    @ConfigurationProperties(prefix = "rate-limiter", ignoreUnknownFields = false)
    public class MyRateLimitProperties extends RateLimitPropertiesSpring { }
    
    @Component 
    public static class MyAppFilter extends RateLimitingFilter {
        public MyAppFilter(RateLimitProperties properties) {
            super(properties);
        }
        @Override 
        protected void onLimitExceeded(
                HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
            response.sendError(429, "Too many requests");
        }
    }
}
```

__2. Add required rate-limiter properties__

Specify either `resource-packages` or `resource-classes`

```yaml
rate-limiter:
  resource-packages: com.myapp.web.rest
  #resource-classes: com.myapp.web.rest.MyResource
```

At this point your application is ready to enjoy the benefits of rate limiting

__3. Annotate classes and/or methods.__

Annotate classes and/or methods as described previously.

__4. (Optional) Add more rate-limit properties__

```yaml
rate-limiter:
  resource-packages: com.myapp.web.rest
  rate-limit-configs:
    # Cap streaming of video to 5kb per second
    - id: video_download
      permits: 5000
      duration: PT1S
    # Limit requests to this resource to 10 per minute
    - id: com.myapp.web.rest.MyResource
      permits: 10
      duration: PT1M
    # Accept only 2 tasks per second
    - id: task_queue
      permits: 2
      duration: PT1S
```

### Fine-grained configuration of rate limiting

Configure rate limiting as described in the [rate-limiter-web-core documentation](https://github.com/poshjosh/rate-limiter-web-core).

When you configure rate limiting using properties, you could:

- Rate limit a class from properties by using the class ID.

- Rate limit a method from properties by using the method ID.

```java
public class RateLimitPropertiesImpl implements RateLimitProperties {
  @Override
  public List<Rates> getRateLimitConfigs() {
    
    List<Rates> ratesList = new ArrayList<>();
    
    // Rate limit a class
    String classId = RateId.of(MyResource.class);  
    ratesList.add(Rates.of(classId, Rate.ofMinutes(10)));

    // Rate limit a method
    String methodId = RateId.of(MyResource.class.getMethod("greet", String.class));
    ratesList.add(Rates.of(methodId, Rate.ofMinutes(10)));
    
    return ratesList;
  }
}
```

### Expression Language

The expression language allows us to write expressive rate conditions, e.g:

`@RateCondition("web.request.user.role = GUEST")`

`@RateCondition("jvm.memory.free < 1GB")`

| format                   | example                                            | description                                                       |  
|--------------------------|----------------------------------------------------|-------------------------------------------------------------------|
| LHS = RHS                | web.request.header[X-RateLimit-Limit] !=           | true, when the X-RateLimit-Limit header exists                    |  
| LHS[key] = val           | web.request.parameter[limited] = true              | true, when request parameter limited equals true                  |  
| LHS = [A &#9122; B]      | web.request.user.role = [GUEST &#9122; RESTRICTED] | true, when the user role is either GUEST or RESTRICTED            |
| LHS[key] = [A &#9122; B] | web.request.cookie[name] = [val_0 &#9122; val_1]   | true, when cookie named `name` is either val_0 or val_1           |  
| LHS[key] = [A & B]       | web.request.header[name] = [val_0 & val_1]         | true, when header named `name` has both val_0 and val_1 as values |  

A rich set of conditions may be expressed as detailed in the
[web specification](https://github.com/poshjosh/rate-limiter-web-core/blob/master/docs/RATE-CONDITION-EXPRESSION-LANGUAGE.md).

### Manually create and use a RateLimiter

Usually, you are provided with appropriate `RateLimiter`s based on the annotations 
and properties you specify. However, you could manually create and use `RateLimiters`.

```java
class MyResource {
    
    RateLimiter rateLimiter = RateLimiterRegistry.of(MyResource.class, "smile");
    
    @Rate(name = "smile", permits = 2)
    String smile() {
        return ":)";
    }
}
```
This way you use the `RateLimiter` as you see fit.

### Annotation Specifications

Please read the [annotation specs](https://github.com/poshjosh/rate-limiter-annotation/blob/main/docs/ANNOTATION_SPECS.md). It is concise.

Enjoy! :wink:
