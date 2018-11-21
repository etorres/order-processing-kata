package es.eriktorr.katas.orders.infrastructure.jms;

import es.eriktorr.katas.orders.domain.model.OrderCreatedEvent;
import es.eriktorr.katas.orders.domain.model.OrderPlacedEvent;
import es.eriktorr.katas.orders.domain.services.OrderPlacer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;

@Slf4j
public class OrderCreatedEventListener {

    private final OrderPlacer orderPlacer;

    public OrderCreatedEventListener(OrderPlacer orderPlacer) {
        this.orderPlacer = orderPlacer;
    }

    @JmsListener(destination = "${order.created.event.queue.name}", containerFactory = "jmsListenerContainerFactory")
    public void process(OrderCreatedEvent orderCreatedEvent) {
        orderPlacer.place(orderCreatedEvent.getValue())
                .doOnError(this::writeFailedLogMessage)
                .subscribe(this::writeSuccessfulLogMessage);
    }

    private void writeSuccessfulLogMessage(OrderPlacedEvent orderPlacedEvent) {
        log.info("Order successfully placed: " + orderPlacedEvent);
    }

    private void writeFailedLogMessage(Throwable error) {
        log.info("Failed to process event", error);
    }

}