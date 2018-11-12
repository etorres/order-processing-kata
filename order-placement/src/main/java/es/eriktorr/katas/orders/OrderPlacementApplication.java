package es.eriktorr.katas.orders;

import es.eriktorr.katas.orders.configuration.JmsConfiguration;
import es.eriktorr.katas.orders.infrastructure.jms.CreateOrderEventListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@SpringBootApplication(scanBasePackageClasses = JmsConfiguration.class
)
public class OrderPlacementApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderPlacementApplication.class, args);
    }

    @Configuration
    @Profile("test")
    @ComponentScan(lazyInit = true)
    static class LocalConfig {
    }

    @Bean
    CreateOrderEventListener createOrderListener() {
        return new CreateOrderEventListener();
    }

}