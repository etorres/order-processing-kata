package es.eriktorr.katas.orders.infrastructure.jms;

import es.eriktorr.katas.orders.domain.common.Clock;
import es.eriktorr.katas.orders.domain.model.OrderCreatedEvent;
import es.eriktorr.katas.orders.domain.model.OrderPlacedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;

@Slf4j
public class OrderCreatedEventListener {

    private final OrderPlacedEventSender orderPlacedEventSender;
    private final Clock clock;

    public OrderCreatedEventListener(OrderPlacedEventSender orderPlacedEventSender, Clock clock) {
        this.orderPlacedEventSender = orderPlacedEventSender;
        this.clock = clock;
    }

    @JmsListener(destination = "${order.created.event.queue.name}", containerFactory = "jmsListenerContainerFactory")
    public void process(OrderCreatedEvent orderCreatedEvent) {
        orderPlacedEventSender.send(OrderPlacedEvent.build(
                Long.MAX_VALUE,
                clock.now(),
                orderCreatedEvent.getValue()
        ));
        log.info("Event received: " + orderCreatedEvent);
    }

}