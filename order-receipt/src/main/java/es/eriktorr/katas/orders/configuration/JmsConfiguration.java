package es.eriktorr.katas.orders.configuration;

import es.eriktorr.katas.orders.domain.service.OrderReceiver;
import lombok.val;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import javax.jms.ConnectionFactory;

@Configuration
@EnableJms
public class JmsConfiguration {

    public static final String ORDER_QUEUE = "order-queue";

    @Bean
    JmsListenerContainerFactory<?> jmsListenerContainerFactory(
            ConnectionFactory connectionFactory,
            DefaultJmsListenerContainerFactoryConfigurer jmsListenerContainerFactoryConfigurer
    ) {
        val jmsListenerContainerFactory = new DefaultJmsListenerContainerFactory();
        jmsListenerContainerFactoryConfigurer.configure(jmsListenerContainerFactory, connectionFactory);
        return jmsListenerContainerFactory;
    }

    @Bean
    MessageConverter jacksonJmsMessageConverter() {
        val converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    @Bean
    OrderReceiver orderReceiver() {
        return new OrderReceiver();
    }

}