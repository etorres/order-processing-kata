# Order Processing Kata

## Domain Events

`Order Created` >> `Order Placed`

## Build, test and report to SonarQube

```text
gradle test integrationTest jacocoMerge sonarqube
```

## Deploy in Docker

`gradle :order-receipt:build`

`gradle :order-receipt:jibDockerBuild`

`docker run -d --name order-receipt -p 8080:8080 order-receipt:1.0.0`

## Usage

```text
curl -X POST -H "Content-Type: application/json" -d '{"reference":"7158","createdAt":"2018-11-03T14:48:17.000000242"}' http://localhost:8080/stores/00-396-261/orders
```

## References

### Command Query Responsibility Segregation and Event Sourcing

* [Really Simple CQRS](https://kalele.io/blog-posts/really-simple-cqrs/).
* [How CQRS Works with Spring Tools](https://thenewstack.io/how-cqrs-works-with-spring-tools/).
* [1 Year of Event Sourcing and CQRS](https://hackernoon.com/1-year-of-event-sourcing-and-cqrs-fb9033ccd1c6).
* [The anatomy of Domain Event](https://blog.arkency.com/2016/05/the-anatomy-of-domain-event/).
* [6 Code Smells with your CQRS Events – and How to Avoid Them](http://danielwhittaker.me/2014/10/18/6-code-smells-cqrs-events-avoid/).

### Reactive Programming

* [Reactive systems using Reactor](https://musigma.blog/2016/11/21/reactor.html).

## Vendor Documentation

* [Web on Reactive Stack](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html).
* [API specification for the Reactor Core library](https://projectreactor.io/docs/core/release/api/index.html).