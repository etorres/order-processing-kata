package es.eriktorr.katas.orders.infrastructure.web;

import es.eriktorr.katas.orders.domain.model.OrderId;
import es.eriktorr.katas.orders.domain.model.StoreId;
import es.eriktorr.katas.orders.domain.services.OrderFinder;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public class OrderHandler {

    private final OrderFinder orderFinder;

    public OrderHandler(OrderFinder orderFinder) {
        this.orderFinder = orderFinder;
    }

    @NonNull
    public Mono<ServerResponse> getOrder(ServerRequest request) {
        return orderFinder.findBy(storeIdFrom(request), orderIdFrom(request))
                .flatMap(order -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .body(BodyInserters.fromObject(order)));
    }

    private StoreId storeIdFrom(ServerRequest request) {
        return new StoreId(request.pathVariable("storeId"));
    }

    private OrderId orderIdFrom(ServerRequest request) {
        return new OrderId(request.pathVariable("orderId"));
    }

}