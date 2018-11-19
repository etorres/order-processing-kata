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
import java.util.function.Predicate;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

public class OrderHandler {

    @Value("${order.help.base_url}")
    private String helpBaseUrl;

    private final OrderFinder orderFinder;

    public OrderHandler(OrderFinder orderFinder) {
        this.orderFinder = orderFinder;
    }

    @NonNull
    public Mono<ServerResponse> getOrder(ServerRequest request) {
        val storeId = storeIdFrom(request);
        val orderId = orderIdFrom(request);
        return orderFinder.findBy(storeId, orderId)
                .filter(isNotInvalid())
                .flatMap(okResponse())
                .switchIfEmpty(notFoundResponse(storeId, orderId));
    }

    private Function<Order, Mono<ServerResponse>> okResponse() {
        return order -> ok().contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(order));
    }

    private Mono<ServerResponse> notFoundResponse(StoreId storeId, OrderId orderId) {
        return status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(notFound(storeId, orderId)));
    }

    private Predicate<Order> isNotInvalid() {
        return ((Predicate<Order>) (Order.INVALID::equals)).negate();
    }

    private StoreId storeIdFrom(ServerRequest request) {
        return new StoreId(request.pathVariable("storeId"));
    }

    private OrderId orderIdFrom(ServerRequest request) {
        return new OrderId(request.pathVariable("orderId"));
    }

    private Problem notFound(StoreId storeId, OrderId orderId) {
        return Problem.builder()
                .withType(URI.create(helpBaseUrl + "/order-not-found"))
                .withTitle("Order Not Found")
                .withStatus(Status.NOT_FOUND)
                .withDetail("The order was not found in the specified store")
                .with("storeId", storeId.getValue())
                .with("orderId", orderId.getValue())
                .build();
    }

}