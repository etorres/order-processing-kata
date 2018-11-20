package es.eriktorr.katas.orders.infrastructure.jms;

import es.eriktorr.katas.orders.domain.model.OrderPlacedEvent;
import es.eriktorr.katas.orders.infrastructure.database.OrdersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;

@Slf4j
public class OrderPlacedEventListener {

    private final OrdersRepository ordersRepository;

    public OrderPlacedEventListener(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    @JmsListener(destination = "${order.placed.event.queue.name}", containerFactory = "jmsListenerContainerFactory")
    public void process(OrderPlacedEvent orderPlacedEvent) {
        ordersRepository.save(orderPlacedEvent.getValue());
    }

}