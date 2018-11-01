package es.eriktorr.katas.orders.domain.service;

import es.eriktorr.katas.orders.domain.model.Order;
import es.eriktorr.katas.orders.domain.model.OrderIdGenerator;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import reactor.core.publisher.Mono;

import javax.validation.ValidationException;
import javax.validation.Validator;

public class OrderReceiver {

    private final JmsTemplate jmsTemplate;

    public OrderReceiver(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public Mono<Order> save(Order order) {


        // TODO
        System.err.println("\n\n >> HERE, order: " + order + "\n");
        // TODO


        throw new ValidationException("Blah, blah, blah!");

//        return Mono.defer(() -> {
//            jmsTemplate.convertAndSend(ORDER_QUEUE, order);
//            return Mono.just(order);
//        });
    }

}