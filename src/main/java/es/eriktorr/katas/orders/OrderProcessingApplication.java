package es.eriktorr.katas.orders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class OrderProcessingApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderProcessingApplication.class, args);
	}

    @Configuration
    @Profile("local")
    @ComponentScan(lazyInit = true)
    static class LocalConfig {
    }

}