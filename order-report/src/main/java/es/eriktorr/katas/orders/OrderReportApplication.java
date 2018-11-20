package es.eriktorr.katas.orders;

import es.eriktorr.katas.orders.configuration.WebSecurityConfiguration;
import es.eriktorr.katas.orders.domain.services.OrderFinder;
import es.eriktorr.katas.orders.infrastructure.database.OrdersRepository;
import es.eriktorr.katas.orders.infrastructure.jms.OrderPlacedEventListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@SpringBootApplication(scanBasePackageClasses = WebSecurityConfiguration.class)
public class OrderReportApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderReportApplication.class, args);
    }

    @Configuration
    @Profile("test")
    @ComponentScan(lazyInit = true)
    static class LocalConfig {
    }

    @Bean
    OrderFinder orderFinder(OrdersRepository ordersRepository) {
        return new OrderFinder(ordersRepository);
    }

    @Bean
    OrderPlacedEventListener orderPlacedEventListener(OrdersRepository ordersRepository) {
        return new OrderPlacedEventListener(ordersRepository);
    }

}