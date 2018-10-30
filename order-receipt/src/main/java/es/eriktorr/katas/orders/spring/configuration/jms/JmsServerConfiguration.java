package es.eriktorr.katas.orders.spring.configuration.jms;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.remoting.JmsInvokerServiceExporter;

import javax.jms.ConnectionFactory;

import static es.eriktorr.katas.orders.spring.configuration.jms.JmsConfiguration.JMS_ORDERS_QUEUE;

@Configuration
class JmsServerConfiguration {

    // @Bean
    // public JmsInvokerServiceExporter exporter(CabBookingService implementation) {
    //     JmsInvokerServiceExporter exporter = new JmsInvokerServiceExporter();
    //     exporter.setServiceInterface(CabBookingService.class);
    //     exporter.setService(implementation);
    //     return exporter;
    // }

    // @Bean
    // SimpleMessageListenerContainer listener(ConnectionFactory factory, JmsInvokerServiceExporter exporter) {
    //     SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
    //     container.setConnectionFactory(factory);
    //     container.setDestinationName(JMS_ORDERS_QUEUE);
    //     container.setConcurrentConsumers(1);
    //     container.setMessageListener(exporter);
    //     return container;
    // }

}