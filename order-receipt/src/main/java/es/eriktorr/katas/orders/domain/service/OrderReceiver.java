package es.eriktorr.katas.orders.domain.service;

import es.eriktorr.katas.orders.domain.model.Order;
import es.eriktorr.katas.orders.infrastructure.database.EventStoreRepository;
import org.springframework.jms.core.JmsTemplate;
import reactor.core.publisher.Mono;

import static es.eriktorr.katas.orders.configuration.JmsConfiguration.ORDER_QUEUE;

public class OrderReceiver {

    private final EventStoreRepository eventStoreRepository;
    private final JmsTemplate jmsTemplate;

    public OrderReceiver(EventStoreRepository eventStoreRepository, JmsTemplate jmsTemplate) {
        this.eventStoreRepository = eventStoreRepository;
        this.jmsTemplate = jmsTemplate;
    }

    public Mono<Order> save(Order order) {
        return eventStoreRepository.create(order)
                .map(it -> {
                    jmsTemplate.convertAndSend(ORDER_QUEUE, it);
                    return it;
                });
    }

}