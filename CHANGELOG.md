# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [ Unreleased ]

-

## [ [0.8.1](https://github.com/poshjosh/rate-limiter-spring/tree/v0.8.1) ] - 2024-08-16

### Added

- Direct registration of Rates using  method: `RateLimiterRegistry#register(String, Rates)`.
- Direct rate limiting using method: `RateLimiterRegistry#tryAcquire`.
- Method `RateLimiterRegistry#isWithinLimit(K key)`.
- Field `rate` to the `@Rate` annotation. It is the default. Example use: `@Rate("9/s")`.
- Fix for matching case-insensitive header names.

### Changed

- Decoupled from `javax.servlet` by replaced `javax.servlet.HttpServletRequest` with `RequestInfo`
- Return type of `RateLimitProperties#getRateLimitConfigs()` from `Map` to `List`.

## [ [0.8.0](https://github.com/poshjosh/rate-limiter-spring/tree/v0.8.0) ] - 2024-08-05

### Added

- Changes from [rate-limiter-web-core v0.8.0](https://github.com/poshjosh/rate-limiter-web-core/blob/v0.8.0/CHANGELOG.md)
- `.expression.Operator#IN`. Example use: `@Rate(permits=0, when="web.request.remote.address in io.github.ratelimiter.Bots.getBotIps()")`.

### Changed

- Expressions to require space around operators. For example:
  use: `sys.time.elapsed > PT9S` not: `sys.time.elapsed>PT9S`.
- Expressions. Simply format `sys.property = {name = value}` to `sys.propery[name] = value`.
- `ResourcePath<String>` to `ResourcePath`. The generic type is always a string.

### Moved

- Static factory methods from `.WebRateLimiterRegistrySpring` to `.WebRateLimiterRegistries`.

## [ [0.7.1](https://github.com/poshjosh/rate-limiter-spring/tree/v0.7.1) ] - 2024-01-26

### Added

- Improve performance, bump to v0.7.1

## [ [0.7.0](https://github.com/poshjosh/rate-limiter-spring/tree/v0.7.0) ] - 2024-01-21

### Added

- Added RateLimiterFactory to replace ResourceLimiter
- Added method RateConfig.getParentOptional()
- Added more performance tests

### Changed

- Revamp logic for matching rate conditions
- Renamed @Rate.name to @Rate.id
- Renamed @RateGroup.name to @RateGroup.id
- Renamed ElementId to RateId
- Renamed all ResourceLimiterXXX to RateLimiterXXX
- Renamed all ResourceLimitingXXX to RateLimitingXXX
- Renamed all ResourceLimiterConfigXXX to RateLimiterContextXXXX

### Removed

- Removed ResourceLimiter and UsageListener
- Streamlined the methods in RateLimiterRegistry
- Moved RateLimiterProperties and RateLimiterRegistry to project rate-limiter-annotation

## [ [0.6.0](https://github.com/poshjosh/rate-limiter-spring/tree/v0.6.0) ] - 2024-01-12

### Changed

- Moved `Matcher` and `ExpressionMatcher` from project rate-limiter-annotation to project rate-limiter.

## [ [0.5.1](https://github.com/poshjosh/rate-limiter-spring/tree/v0.5.1) ] - 2023-05-20

### Added

- Support composite condition expressions e.g of format `sys.time > X & sys.time < Y`
- Support expression: `web.request.ip`. An alias `web.request.remote.address`.
- Added `@Rate#condition`. An alias for `@Rate#when`.
- Added more performance tests

### Changed

- Renamed `Bandwidth#ALWAYS_AVAILABLE` to `Bandwidth#UNLIMITED`
- Renamed `sys.memory.` to `jvm.memory.`

## [ [0.5.0](https://github.com/poshjosh/rate-limiter-spring/tree/v0.5.0) ] - 2023-05-01

### Added

- Allow usage of 3rd party rate limiters

## [ [0.4.2](https://github.com/poshjosh/rate-limiter-spring/tree/v0.4.2) ] - 2023-02-24

### Added

- Use system epoch millis as Ticker time

## [ [0.4.1](https://github.com/poshjosh/rate-limiter-spring/tree/v0.4.1) ] - 2023-02-19

### Added

- Added expression handling for `sys.property` and `sys.environment`
- Added `UnmodifiableRegistries`
- Added methods `ResourceLimiterRegistry#register` taking either `Class` or `Method` argument. This enables the manual registration of resources for rate limting.
- Added documentation for adapting to spring boot

### Changed

- Modified `ResourceLimiterRegisry#getRateConfig` to `getLimiterConfig`
- Updated README. Note that resource packages/classes must be specified for automatic rate limiting to work.

### Removed

- Removed dependency on `javax.cache.Cache`
- Removed spring boot dependencies. 

## [ [0.4.0](https://github.com/poshjosh/rate-limiter-spring/tree/v0.4.0) ] - 2023-02-11

### Added

- Added `RateLimitProperties#getApplicationPath` to support application path.

### Changed

- Fixed handling of multiple (non-composed) limits on a single node.
- Fixed handling of multiple methods with same path but different http method.

## [ [0.3.4](https://github.com/poshjosh/rate-limiter-spring/tree/v0.3.4) ] - 2023-02-05

### Changed

- Permit expressions (for rate conditions) having `null` right-hand side
- Improved expression splitting
- Renamed `web.session.cookie` to `web.request.cookie`
- Renamed `web.session.user.role` to `web.request.user.role`
- Renamed `web.session.user.principal` to `web.request.user.principal`
- Renamed `Element` to `RateSource` and add `PropertyRateSource`
- Renamed `Matcher#matchOrNull` to `Matcher#match`
- Use String return type for `Matcher#match`

## [ [0.3.3](https://github.com/poshjosh/rate-limiter-spring/tree/v0.3.3) ] - 2023-02-04

### Added

- Added field `id` to `Bandwidths`
- Added support for multiple (non-composed) bandwidths

### Changed

- Renamed `Operator#DEFAULT` to `Operator#NONE`

## [ [0.3.2](https://github.com/poshjosh/rate-limiter-spring/tree/v0.3.2) ] - 2023-01-29

### Added

- Added `when` field to `@Rate` annotation. It is an alias for `@RateCondition`

### Removed

- Removed `ResourceLimiters.of`. Rather use: `ResourceLimiter.of`

## [ [0.3.1](https://github.com/poshjosh/rate-limiter-spring/tree/v0.3.1) ] - 2023-01-24

### Added

- Implement JVM thread rate condition expression, setup code coverage

## [ [0.3.0](https://github.com/poshjosh/rate-limiter-spring/tree/v0.3.0) ] - 2023-01-22

### Added

- Implement `@RateCondition` using expressions
- Add method `getResourceClasses` to `RateLimitProperties`
- Add a default operator i.e `Operator.DEFAULT`
- Add more tests
- Improve performance

### Changed

- Restrict `@RateGroup` annotation to `ElementType.ANNOTATION_TYPE`
- Rename `MatchedResourceLimiter` to `ResourceLimiters`
- Make `@RateGroup#name` or `@RateGroup#value` mandatory.
- Rename `Bandwidths.getMembers` to `Bandwidths.getBandwidths`

### Removed

- Remove annotation `@RateReqestIf`

## [ [0.2.0](https://github.com/poshjosh/rate-limiter-spring/tree/v0.2.0) ] - 2023-01-08

### Added

- Implement matching of requests via annotation `@RateRequestIf`
- Implement and use `UnmodifiableRegistry`
- Add alias `value` as alias for `permits` property in `@Rate`

### Changed

- Rename package `com.looseboxes` to `io.github.poshjosh`
- Update README
- Move `@Rate` and `@RateGroup` from package `ratelimiter.annotations` to `ratelimiter.annotation`

### Removed

- Remove methods `RateCache.cache` and `RateCache.listener`

## [ [0.1.0](https://github.com/poshjosh/rate-limiter-spring/tree/v0.1.0) ] - 2023-01-07

### Added

- Add `name` field to `@RateLimit` annotation
- Add builder like methods to `ResourceLimiter` for setting `RateCache` and `UsageListener`
- Support single `Rate` per config in properties. Was previously a list of `Rate`s

### Changed

- Update README.md
- Rename all `of()` to `ofDefaults()`
- Change `@RateLimit.limit` to `@RateLimit.permits`
- Change default `timeUnit` in `@RateLimit` to `TimeUnit.SECONDS`
- Rename `WebResourceLimiterConfig` to `ResourceLimiterConfig`
- Make `RateLimiterRegistry` implement Registries
- Rename method `AnnotationProcessor.ofRates` to `AnnotationProcessor.ofDefaults`
- Rename `PatternMatchingResourceLimiter` to `MatchedResourceLimiter`
- Rename `RateLimit` annotation to `Rate`
- Rename `RateLimitGroup` to `RateGroup`
- Change default `@Rate.duration` to 1

### Removed

- Remove annotation `@Nullable`
- Remove `Registries.factories()`
- Remove the value-related generic from `RateCache`. The value now has a fixed type of `Bandwidths`

## [ [0.0.9](https://github.com/poshjosh/rate-limiter-spring/tree/v0.0.9) ] - 2022-12-30

### Added

- Add more Bandwidth tests

## [ [0.0.8](https://github.com/poshjosh/rate-limiter-spring/tree/v0.0.8) ] - 2022-12-29

### Added

- Separate repository for annotations: [rate-limiter-annotation](https://github.com/poshjosh/rate-limiter-annotation)
- Change log
