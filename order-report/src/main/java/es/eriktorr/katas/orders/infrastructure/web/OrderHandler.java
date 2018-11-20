package es.eriktorr.katas.orders.infrastructure.web;

import es.eriktorr.katas.orders.domain.model.Order;
import es.eriktorr.katas.orders.domain.model.OrderId;
import es.eriktorr.katas.orders.domain.model.StoreId;
import es.eriktorr.katas.orders.domain.services.OrderFinder;
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

import java.net.URI;
import java.util.function.Function;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

public class OrderHandler {

    private static final String STORE_ID_ATTRIBUTE = "storeId";
    private static final String ORDER_ID_ATTRIBUTE = "orderId";

    @Value("${order.problem.type.base_url}")
    private String helpBaseUrl;

    private final OrderFinder orderFinder;

    public OrderHandler(OrderFinder orderFinder) {
        this.orderFinder = orderFinder;
    }

    @NonNull
    public Mono<ServerResponse> getOrder(ServerRequest request) {
        val storeId = storeIdFrom(request);
        val orderId = orderIdFrom(request);
        return orderFinder.findOrderBy(storeId, orderId)
                .flatMap(okResponse())
                .switchIfEmpty(notFoundResponse(storeId, orderId))
                .onErrorResume(Throwable.class, error -> internalServerErrorResponse(error, storeId, orderId));
    }

    private Function<Order, Mono<ServerResponse>> okResponse() {
        return order -> ok().contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(order));
    }

    private Mono<ServerResponse> notFoundResponse(StoreId storeId, OrderId orderId) {
        return status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_PROBLEM_JSON_UTF8)
                .body(BodyInserters.fromObject(notFound(storeId, orderId)));
    }

    private Mono<ServerResponse> internalServerErrorResponse(Throwable error, StoreId storeId, OrderId orderId) {
        return status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_PROBLEM_JSON_UTF8)
                .body(BodyInserters.fromObject(internalServerError(error, storeId, orderId)));
    }

    private StoreId storeIdFrom(ServerRequest request) {
        return new StoreId(request.pathVariable(STORE_ID_ATTRIBUTE));
    }

    private OrderId orderIdFrom(ServerRequest request) {
        return new OrderId(request.pathVariable(ORDER_ID_ATTRIBUTE));
    }

    private Problem notFound(StoreId storeId, OrderId orderId) {
        return Problem.builder()
                .withType(URI.create(helpBaseUrl + "/order-not-found"))
                .withTitle("Order Not Found")
                .withStatus(Status.NOT_FOUND)
                .withDetail("The order was not found in the specified store")
                .with(STORE_ID_ATTRIBUTE, storeId.getValue())
                .with(ORDER_ID_ATTRIBUTE, orderId.getValue())
                .build();
    }

    private Problem internalServerError(Throwable error, StoreId storeId, OrderId orderId) {
        return Problem.builder()
                .withType(URI.create(helpBaseUrl + "/order-failed"))
                .withTitle("Order Operation Failed")
                .withStatus(Status.INTERNAL_SERVER_ERROR)
                .withDetail("The operation cannot be completed")
                .with(STORE_ID_ATTRIBUTE, storeId.getValue())
                .with(ORDER_ID_ATTRIBUTE, orderId.getValue())
                .with("errorMessage", error.getMessage())
                .build();
    }

}