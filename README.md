# rate limiter spring

Light-weight rate limiting library for spring controllers, based on 
[rate-limiter-web-core](https://github.com/poshjosh/rate-limiter-web-core).

We believe that rate limiting should be as simple as:

```java
@Rate(10) // 10 permits per second for all methods collectively
@Controller
@RequestMapping("/api")
class GreetingResource {

  // Only 2 calls per second to this path, for users in role GUEST
  @Rate(permits=2, when="web.session.user.role=GUEST")
  @GetMapping("/smile")
  String smile() {
    return ":)";
  }

  // Only 10 calls per minute to this path, when system available memory < 1GB 
  @Rate(permits=10, timeUnit=TimeUnit.MINUTES, when="sys.memory.available<1gb")
  @GetMapping("/greet")
  String greet(String name) {
    return "Hello " + name;
  }
}
```

Please first read the [rate-limiter-web-core documentation](https://github.com/poshjosh/rate-limiter-web-core).

To add a dependency on `rate-limiter-spring` using Maven, use the following:

```xml
        <dependency>
            <groupId>io.github.poshjosh</groupId>
            <artifactId>rate-limiter-spring</artifactId>
            <version>0.3.3</version> 
        </dependency>
```

### Usage

__1. Configure your spring application__

```java
package com.myapplicatioon;

import javax.servlet.*;

import ResourceLimitingFilter;
import ResourceLimiterConfiguration;

@SpringBootApplication(scanBasePackageClasses = {ResourceLimiterConfiguration.class, MyApp.class }) 
public class MyApp {
    public static void main(String[] args) {
        SpringApplication.run(MyApp.class, args);
    }

    @Component 
    public static class MyAppFilter extends ResourceLimitingFilter {
        @Override 
        protected void onLimitExceeded(
                HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
            response.sendError(429, "Too many requests");
        }
    }
}
```

At this point your application is ready to enjoy the benefits of rate limiting

__2. Annotate classes and/or methods.__

```java
package com.myapplicatioon.web.rest;

import io.github.poshjosh.ratelimiter.annotations.RateCondition;
import io.github.poshjosh.ratelimiter.util.Rate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
@RequestMapping("/my-resources") 
public class MyResource {

    // Only 25 calls per second for users in role GUEST
    @Rate(permits=25, when="web.session.user.role=GUEST")
    @GetMapping("/greet/{name}") 
    public ResponseEntity<String> greet(@PathVariable String name) {
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

### Expression Language

The expression language allows us to write expressive rate conditions, e.g:

`@RateCondition("web.session.user.role=GUEST")`

`@RateCondition("sys.memory.free<1GB")`

format          | example                                  | description
----------------|------------------------------------------|------------
LHS=RHS         | web.request.header=X-RateLimit-Limit     | true, when the X-RateLimit-Limit header exists
LHS={key=val}   | web.request.parameter={limited=true}     | true, when request parameter limited equals true
LHS=[A!B]       | web.session.user.role=[GUEST!RESTRICTED] | true, when the user role is either GUEST or RESTRICTED
LHS=[A&B]       | web.session.user.role=[GUEST&RESTRICTED] | true, when the user role is either GUEST and RESTRICTED
LHS={key=[A!B]} | web.request.header={name=[val_0!val_1]}  | true, when either val_0 or val_1 is set a header
LHS={key=[A&B]} | web.request.header={name=[val_0&val_1]}  | true, when both val_0 and val_1 are set as headers

__Note:__ `|` equals OR. `!` is used above for OR because markdown does not support `|` in tables

A rich set of conditions may be expressed as detailed in the
[web specification](https://github.com/poshjosh/rate-limiter-web-core/blob/master/docs/RATE-CONDITION-EXPRESSION-LANGUAGE.md).

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
