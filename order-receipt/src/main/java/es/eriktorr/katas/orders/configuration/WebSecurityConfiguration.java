package es.eriktorr.katas.orders.configuration;

import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
public class WebSecurityConfiguration {

    private static final String ACTUATOR_ROLE = "ACTUATOR";

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http.csrf().disable().authorizeExchange()
                .matchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .matchers(EndpointRequest.to(InfoEndpoint.class, HealthEndpoint.class)).permitAll()
                .matchers(EndpointRequest.toAnyEndpoint()).hasRole(ACTUATOR_ROLE)
                .pathMatchers("/", "/index.html").permitAll()
                .pathMatchers("/stores/**").permitAll()
                .anyExchange().authenticated().and()
                .build();
    }

}