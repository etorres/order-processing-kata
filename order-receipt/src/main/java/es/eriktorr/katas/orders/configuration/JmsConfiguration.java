package es.eriktorr.katas.orders.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import javax.jms.ConnectionFactory;

@Configuration
@EnableJms
@Slf4j
public class JmsConfiguration {

    @Bean
    JmsListenerContainerFactory<DefaultMessageListenerContainer> jmsListenerContainerFactory(
            ConnectionFactory connectionFactory,
            DefaultJmsListenerContainerFactoryConfigurer jmsListenerContainerFactoryConfigurer
    ) {
        val jmsListenerContainerFactory = new DefaultJmsListenerContainerFactory();
        jmsListenerContainerFactory.setErrorHandler(throwable -> log.error("Execution of JMS message listener failed", throwable));
        jmsListenerContainerFactoryConfigurer.configure(jmsListenerContainerFactory, connectionFactory);
        return jmsListenerContainerFactory;
    }

    @Bean
    MessageConverter jacksonJmsMessageConverter() {
        val objectMapper = new ObjectMapper();
        objectMapper.registerModules(
                new Jdk8Module(),
                new JavaTimeModule(),
                new ParameterNamesModule(JsonCreator.Mode.PROPERTIES)
        );

        val messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setTargetType(MessageType.TEXT);
        messageConverter.setTypeIdPropertyName("_type");
        messageConverter.setObjectMapper(objectMapper);

        return messageConverter;
    }

}