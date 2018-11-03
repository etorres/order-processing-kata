package es.eriktorr.katas.orders.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
@EnableJms
public class JmsConfiguration {

    public static final String ORDER_QUEUE = "order-queue";

    @Bean
    JmsListenerContainerFactory<DefaultMessageListenerContainer> jmsListenerContainerFactory(
            ConnectionFactory connectionFactory,
            DefaultJmsListenerContainerFactoryConfigurer jmsListenerContainerFactoryConfigurer
    ) {
        val jmsListenerContainerFactory = new DefaultJmsListenerContainerFactory();
        jmsListenerContainerFactoryConfigurer.configure(jmsListenerContainerFactory, connectionFactory);
        return jmsListenerContainerFactory;
    }

    @Bean
    MessageConverter jacksonJmsMessageConverter() {
        val javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ISO_LOCAL_DATE));
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        val objectMapper = new ObjectMapper();
        objectMapper.registerModule(javaTimeModule);

        val messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setTargetType(MessageType.TEXT);
        messageConverter.setTypeIdPropertyName("_type");
        messageConverter.setObjectMapper(objectMapper);

        return messageConverter;
    }

}