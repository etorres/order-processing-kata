package es.eriktorr.katas.orders.infrastructure.jms;

import es.eriktorr.katas.orders.domain.model.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;

@Slf4j
public class CreateOrderEventListener {

    @JmsListener(destination = "${orders.queue.name}", containerFactory = "jmsListenerContainerFactory")
    public void process(OrderCreatedEvent orderCreatedEvent) {
        log.info("Event received: " + orderCreatedEvent);
    }

}