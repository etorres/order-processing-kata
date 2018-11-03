package es.eriktorr.katas.orders.infrastructure.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import es.eriktorr.katas.orders.domain.common.SingleValue;
import es.eriktorr.katas.orders.domain.model.Order;

import java.io.IOException;

import static es.eriktorr.katas.orders.infrastructure.json.OrderFields.*;
import static org.springframework.util.StringUtils.trimWhitespace;

public class OrderSerializer extends StdSerializer<Order> {

    public OrderSerializer() {
        super(Order.class);
    }

    @Override
    public void serialize(Order order, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
        jsonGenerator.writeStartObject();
        writeOptional(jsonGenerator, ORDER_ID_FIELD, order.getOrderId());
        writeOptional(jsonGenerator, ORDER_STORE_FIELD, order.getStoreId());
        writeOptional(jsonGenerator, ORDER_REFERENCE_FIELD, order.getOrderReference());
        writeOptional(jsonGenerator, ORDER_CREATED_AT_FIELD, order.getCreatedAt());
        jsonGenerator.writeEndObject();
    }

    private <T extends SingleValue> void writeOptional(JsonGenerator jsonGenerator, String field, T singleValue) throws IOException {
        if (singleValue == null) return;
        final String trimmedValue = trimWhitespace(singleValue.getValue());
        if (!"".equals(trimmedValue)) {
            jsonGenerator.writeStringField(field, trimmedValue);
        }
    }

    private <T> void writeOptional(JsonGenerator jsonGenerator, String field, T object) throws IOException {
        if (object != null) {
            jsonGenerator.writeObjectField(field, object);
        }
    }

}