package es.eriktorr.katas.orders.spring.configuration.jms;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

import javax.jms.Queue;

@Configuration
@EnableJms
class JmsConfiguration {

    static final String JMS_ORDERS_QUEUE = "ordersQueue";

    @Bean(name = JMS_ORDERS_QUEUE)
    Queue ordersQueue() {
        return new ActiveMQQueue(JMS_ORDERS_QUEUE);
    }

}