package es.eriktorr.katas.orders.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import es.eriktorr.katas.orders.infrastructure.json.OrderDeserializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.problem.ProblemModule;

@Configuration
class JacksonConfiguration {

    @Bean
    Jackson2ObjectMapperBuilderCustomizer customJson() {
        return builder -> {
            builder.serializationInclusion(JsonInclude.Include.NON_NULL);
            builder.serializationInclusion(JsonInclude.Include.NON_EMPTY);
            builder.modules(new Jdk8Module(), new ParameterNamesModule(JsonCreator.Mode.PROPERTIES), new ProblemModule());
            builder.deserializers(new OrderDeserializer());
        };
    }

}