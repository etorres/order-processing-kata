package es.eriktorr.katas.orders.infrastructure.web;

import es.eriktorr.katas.orders.domain.model.Order;
import es.eriktorr.katas.orders.domain.model.OrderCreatedEvent;
import es.eriktorr.katas.orders.domain.model.OrderIdGenerator;
import es.eriktorr.katas.orders.domain.model.StoreId;
import es.eriktorr.katas.orders.domain.problem.Violation;
import es.eriktorr.katas.orders.domain.services.OrderReceiver;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.net.URI;
import java.util.function.Function;
import java.util.stream.Collectors;

import static es.eriktorr.katas.orders.domain.problem.Violation.fieldFrom;
import static java.util.Comparator.comparing;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

public class OrderHandler {

    private static final String STORE_ID_ATTRIBUTE = "storeId";

    @Value("${order.problem.type.base_url}")
    private String problemTypeBaseUrl;

    private final Validator validator;
    private final OrderReceiver orderReceiver;
    private final OrderIdGenerator orderIdGenerator;

    public OrderHandler(Validator validator, OrderIdGenerator orderIdGenerator, OrderReceiver orderReceiver) {
        this.validator = validator;
        this.orderReceiver = orderReceiver;
        this.orderIdGenerator = orderIdGenerator;
    }

    @NonNull
    public Mono<ServerResponse> createOrder(ServerRequest request) {
        val storeId = storeIdFrom(request);
        return request.bodyToMono(Order.class)
                .map(orderRequest -> orderFrom(storeId, orderRequest))
                .doOnNext(this::validate)
                .compose(orderReceiver::save)
                .flatMap(okResponse())
                .onErrorResume(ConstraintViolationException.class, error -> badRequestResponse(error, storeId))
                .onErrorResume(Throwable.class, error -> internalServerErrorResponse(error, storeId));
    }

    private Function<OrderCreatedEvent, Mono<? extends ServerResponse>> okResponse() {
        return order -> ServerResponse.created(pathTo(order)).build();
    }

    private Mono<ServerResponse> badRequestResponse(ConstraintViolationException exception, StoreId storeId) {
        return status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_PROBLEM_JSON_UTF8)
                .body(BodyInserters.fromObject(badRequest(exception, storeId)));
    }

    private Mono<ServerResponse> internalServerErrorResponse(Throwable error, StoreId storeId) {
        return status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_PROBLEM_JSON_UTF8)
                .body(BodyInserters.fromObject(internalServerError(error, storeId)));
    }

    private Problem internalServerError(Throwable error, StoreId storeId) {
        return Problem.builder()
                .withType(URI.create(problemTypeBaseUrl + "/order-failed"))
                .withTitle("Order Operation Failed")
                .withStatus(Status.INTERNAL_SERVER_ERROR)
                .withDetail("The operation cannot be completed")
                .with(STORE_ID_ATTRIBUTE, storeId.getValue())
                .with("errorMessage", error.getMessage())
                .build();
    }

    private Problem badRequest(ConstraintViolationException exception, StoreId storeId) {
        val violations = exception.getConstraintViolations().stream()
                .map(this::violationFrom)
                .sorted(comparing(Violation::getField).thenComparing(Violation::getMessage))
                .collect(Collectors.toList());
        return Problem.builder()
                .withType(URI.create(problemTypeBaseUrl + "/invalid-parameters"))
                .withTitle("Invalid Parameters")
                .withStatus(Status.BAD_REQUEST)
                .withDetail("The request violates one or several constraints")
                .with(STORE_ID_ATTRIBUTE, storeId.getValue())
                .with("violations", violations)
                .build();
    }

    private Violation violationFrom(ConstraintViolation constraintViolation) {
        return new Violation(fieldFrom(constraintViolation), constraintViolation.getMessage());
    }

    private StoreId storeIdFrom(ServerRequest request) {
        return new StoreId(request.pathVariable(STORE_ID_ATTRIBUTE));
    }

    private Order orderFrom(StoreId storeId, Order orderRequest) {
        return new Order(
                orderIdGenerator.nextOrderId(),
                storeId,
                orderRequest.getOrderReference(),
                orderRequest.getCreatedAt()
        );
    }

    private void validate(Order order) {
        val violations = validator.validate(order);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private URI pathTo(OrderCreatedEvent orderCreatedEvent) {
        val order = orderCreatedEvent.getValue();
        return URI.create("/stores/" + order.getStoreId() + "/orders/" + order.getOrderId());
    }

}