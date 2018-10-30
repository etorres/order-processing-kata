package es.eriktorr.katas.orders.spring.configuration.jms;

import lombok.val;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.remoting.JmsInvokerProxyFactoryBean;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;

import static es.eriktorr.katas.orders.spring.configuration.jms.JmsConfiguration.JMS_ORDERS_QUEUE;

@Configuration
public class JmsClientConfiguration {

    // @Bean
    // public FactoryBean invoker(ConnectionFactory factory, @Qualifier(JMS_ORDERS_QUEUE) Queue queue) {
    //     val factoryBean = new JmsInvokerProxyFactoryBean();
    //     factoryBean.setConnectionFactory(factory);
    //     factoryBean.setServiceInterface(CabBookingService.class);
    //     factoryBean.setQueue(queue);
    //     return factoryBean;
    // }

}