package es.eriktorr.katas.orders.configuration;

import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
public class WebSecurityConfiguration {

    @Value("${spring.security.user.name}")
    private String userName;

    @Value("${spring.security.user.password}")
    private String userPassword;

    private static final String ACTUATOR_ROLE = "ACTUATOR";

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http.csrf().disable().authorizeExchange()
                .matchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .matchers(EndpointRequest.to(InfoEndpoint.class, HealthEndpoint.class)).permitAll()
                .matchers(EndpointRequest.toAnyEndpoint()).hasAuthority(ACTUATOR_ROLE)
                .pathMatchers(HttpMethod.GET, "/", "/index.html").permitAll()
                .pathMatchers(HttpMethod.GET, "/stores/**").permitAll()
                .anyExchange().authenticated().and()
                .httpBasic().and()
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new SCryptPasswordEncoder();
    }

    @Bean
    MapReactiveUserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        val user = User.withUsername(userName)
                .passwordEncoder(passwordEncoder::encode)
                .password(userPassword)
                .authorities(ACTUATOR_ROLE)
                .build();
        return new MapReactiveUserDetailsService(user);
    }

}