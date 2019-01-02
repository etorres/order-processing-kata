# Order Processing Kata

## Domain Events

`Order Created` >> `Order Placed`

## Event Store

The event store is a log that can be implemented in an append-only table. For instance, PostgreSQL supports [declarative partitioning](https://www.postgresql.org/docs/current/ddl-partitioning.html), and [table inheritance](https://www.postgresql.org/docs/current/ddl-inherit.html) for partitioning in situations where declarative partitioning falls short.

## Duplicate event handling

At most once handling is built into the domain model.

If multiple messages have the same business semantics, then the consumers will reject duplicates themselves.

Therefore, any given consumer can use the semantics of the message to eliminate duplicates.

## Build

### Build and test

Please, use `docker-compose -f docker/docker-compose.yml up postgres adminer flyway` to spin up the database, then run the test like this:

```text
gradle test integrationTest
```

#### Regression tests with Cucumber

We decided to put regression tests in the same repository as `order-receipt` microservice. The reasoning behind this decision is that `order-receipt` contains the behaviour the client is interested in. The fact that `order-placement` and `order-report` are called in an specific order is an implementation detail from the point of view of the client.

Please, use `docker-compose -f docker/docker-compose.yml up postgres adminer flyway` to spin up the environment (database + microservices), then run the test like this:

```text
gradle order-receipt:regressionTest
```

### Build, test and report to SonarQube

```text
gradle test integrationTest jacocoMerge sonarqube
```

### Test Reports
Gradle writes XML test reports to build/test-results/test and HTML test reports to build/reports/tests/test.

## Deploy in Docker

`gradle :order-receipt:build`

`gradle :order-receipt:jibDockerBuild`

`docker run -d --name order-receipt -p 8080:8080 order-receipt:1.0.0`

`docker-compose -f docker/docker-compose.yml up`

`docker-compose -f docker/docker-compose.yml ps`

`docker inspect docker_order-processing-kata-network`

`docker exec -it -u root activemq /bin/ash`

`apk update && apk add curl`

## Usage

```text
curl -X POST -H "Content-Type: application/json" -d '{"reference":"7158","createdAt":"2018-11-03T14:48:17.000000242"}' http://localhost:8080/stores/00-396-261/orders
```

## References

### Project setup

* [Keeping environment variables local to a project directory on the terminal and with IntelliJ](https://medium.com/@tmaslen/keeping-environment-variables-local-to-a-project-directory-on-the-terminal-and-with-intellij-c928c2016599).

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
* [WebFlux Security](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-security.html#boot-features-security-webflux).
* [Spring integration with ActiveMQ](https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#jms).

## Examples

* [Docker+ActiveMQ](https://github.com/daggerok/spring-boot-rest-jms-activemq).

## Test Coverage

* [Merge multi-project test coverage: Gradle + Jacoco + Sonarqube](https://cristian.io/post/sonar-coverage/).

## Spring Boot Security

* [10 Excellent Ways to Secure Your Spring Boot Application](https://developer.okta.com/blog/2018/07/30/10-ways-to-secure-spring-boot).

## Problem JSON (RFC 7807)

* [Use Problem JSON [176]](https://opensource.zalando.com/restful-api-guidelines/index.html#176).
* [Open API schema definition of the Problem JSON object](https://opensource.zalando.com/problem/schema.yaml).

## Amazon MQ

* [Tutorial: Connecting a Java Application to Your Amazon MQ Broker](https://docs.aws.amazon.com/amazon-mq/latest/developer-guide/amazon-mq-connecting-application.html).

* [Connecting to Amazon MQ](https://docs.aws.amazon.com/amazon-mq/latest/developer-guide/connecting-to-amazon-mq.html).

* [Working Examples of Using Java Message Service (JMS) with ActiveMQ](https://docs.aws.amazon.com/amazon-mq/latest/developer-guide/amazon-mq-working-java-example.html).

## Table Partitioning in PostgreSQL

* [How to scale PostgreSQL 10 using table inheritance and declarative partitioning](https://blog.timescale.com/scaling-partitioning-data-postgresql-10-explained-cd48a712a9a1).

## JSON Support in PostgreSQL

* [Unleash the Power of Storing JSON in Postgres](https://blog.codeship.com/unleash-the-power-of-storing-json-in-postgres/).

## Locks in PostgreSQL

* [When Postgres blocks: 7 tips for dealing with locks](https://www.citusdata.com/blog/2018/02/22/seven-tips-for-dealing-with-postgres-locks/).