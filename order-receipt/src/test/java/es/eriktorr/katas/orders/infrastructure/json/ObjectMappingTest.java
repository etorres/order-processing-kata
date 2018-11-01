package es.eriktorr.katas.orders.infrastructure.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.Value;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ObjectMappingTest {

    private static final String ITEM_ID = "4472a477-931e-48b7-8bfb-95daa1ad0216";

    @Test
    @DisplayName("Serialize immutable object")
    void serializeImmutableObject() throws IOException {
        val jsonPayload = "{\"uuid\":\"" + ITEM_ID + "\"}";
        val immutableItem = new ImmutableItem(UUID.fromString(ITEM_ID));

        val jacksonObjectMapperBuilder = new Jackson2ObjectMapperBuilder();
        customJson().customize(jacksonObjectMapperBuilder);
        val objectMapper = jacksonObjectMapperBuilder.build();

        assertThat(objectMapper.readValue(jsonPayload, ImmutableItem.class)).isEqualTo(immutableItem);
    }

    private Jackson2ObjectMapperBuilderCustomizer customJson() {
        return builder -> {
            builder.serializationInclusion(JsonInclude.Include.NON_NULL);
            builder.serializationInclusion(JsonInclude.Include.NON_EMPTY);
            builder.modules(new Jdk8Module(), new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
        };
    }

    @Value
    private static class ImmutableItem {
        private final UUID uuid;
    }

}