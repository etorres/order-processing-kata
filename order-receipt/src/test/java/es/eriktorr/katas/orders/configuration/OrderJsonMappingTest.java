package es.eriktorr.katas.orders.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.eriktorr.katas.orders.domain.model.Order;
import es.eriktorr.katas.orders.domain.model.OrderId;
import es.eriktorr.katas.orders.domain.model.OrderReference;
import es.eriktorr.katas.orders.domain.model.StoreId;
import lombok.AllArgsConstructor;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

class OrderJsonMappingTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        val jacksonObjectMapperBuilder = new Jackson2ObjectMapperBuilder();
        new JacksonConfiguration().customJson().customize(jacksonObjectMapperBuilder);
        objectMapper = jacksonObjectMapperBuilder.build();
    }

    @TestFactory
    Stream<DynamicTest> mapOrderFromToJson() {
        return Arrays.stream(new SampleCase[] {
                new SampleCase(
                        "All fields",
                        "json/orders/order_all_fields.json",
                        new Order(
                                new OrderId("4472a477-931e-48b7-8bfb-95daa1ad0216"),
                                new StoreId("00-396-261"),
                                new OrderReference("7158"),
                                LocalDateTime.of(2018, 11, 3, 14, 48, 17, 242)
                        )
                ),
                new SampleCase(
                        "Order reference only",
                        "json/orders/order_reference_only.json",
                        new Order(new OrderId(""), new StoreId(""), new OrderReference("7158"), null)
                )
        }).map(sampleCase -> DynamicTest.dynamicTest("Sample case: " + sampleCase.name, () -> {
            val jsonPayload = readFileToJson(sampleCase.fileName);
            val order = objectMapper.readValue(jsonPayload, Order.class);

            assertThat(order).isEqualTo(sampleCase.order);
            assertThat(objectMapper.writeValueAsString(order)).isEqualTo(jsonPayload);
        }));
    }

    private String readFileToJson(String filename) throws Exception {
        val path = Paths.get(requireNonNull(getClass().getClassLoader().getResource(filename)).toURI());
        try (Stream<String> lines = Files.lines(path)) {
            return lines.collect(Collectors.joining("\n"));
        }
    }

    @AllArgsConstructor
    private static class SampleCase {
        private final String name;
        private final String fileName;
        private final Order order;
    }

}