package es.eriktorr.katas.orders.domain.service;

import es.eriktorr.katas.orders.domain.model.Order;
import es.eriktorr.katas.orders.domain.model.OrderIdGenerator;
import org.springframework.jms.core.JmsTemplate;
import reactor.core.publisher.Mono;

import javax.validation.ValidationException;

public class OrderReceiver {

    private final JmsTemplate jmsTemplate;

    public OrderReceiver(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public Mono<Order> save(Order order) {


        System.err.println("\n\n >> HERE: " + order + "\n");


        throw new ValidationException("Blah, blah, blah!");

//        return Mono.defer(() -> {
//            jmsTemplate.convertAndSend(ORDER_QUEUE, order);
//            return Mono.just(order);
//        });
    }

}