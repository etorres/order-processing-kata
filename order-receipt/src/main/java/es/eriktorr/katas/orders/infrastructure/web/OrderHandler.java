package es.eriktorr.katas.orders.infrastructure.web;

import es.eriktorr.katas.orders.domain.common.Clock;
import es.eriktorr.katas.orders.domain.model.Order;
import es.eriktorr.katas.orders.domain.model.OrderIdGenerator;
import es.eriktorr.katas.orders.domain.model.StoreId;
import es.eriktorr.katas.orders.domain.service.OrderReceiver;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.net.URI;
import java.util.stream.Collectors;

public class OrderHandler {

    private final Validator validator;
    private final OrderReceiver orderReceiver;
    private final OrderIdGenerator orderIdGenerator;
    private final Clock clock;

    public OrderHandler(Validator validator, Clock clock, OrderIdGenerator orderIdGenerator, OrderReceiver orderReceiver) {
        this.validator = validator;
        this.orderReceiver = orderReceiver;
        this.orderIdGenerator = orderIdGenerator;
        this.clock = clock;
    }

    @NonNull
    public Mono<ServerResponse> createOrder(ServerRequest request) {
        return request.bodyToMono(Order.class)
                .map(orderRequest -> orderFrom(request.pathVariable("storeId"), orderRequest))
                .doOnNext(this::validate)
                .compose(orderReceiver::save)
                .flatMap(order -> ServerResponse.created(pathTo(order)).build())
                .onErrorResume(ConstraintViolationException.class, this::toBadRequest);
    }

    private Order orderFrom(String storeId, Order orderRequest) {
        return new Order(
                orderIdGenerator.nextOrderId(),
                new StoreId(storeId),
                orderRequest.getOrderReference(),
                clock.now()
        );
    }

    private void validate(Order order) {
        val violations = validator.validate(order);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private Mono<? extends ServerResponse> toBadRequest(ConstraintViolationException exception) {
        val errors = exception.getConstraintViolations().parallelStream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_UTF8)
                .syncBody(Problem.valueOf(Status.BAD_REQUEST, errors));
    }

    private URI pathTo(Order order) {
        return URI.create("/stores/" + order.getStoreId() + "/orders/" + order.getOrderId());
    }

}