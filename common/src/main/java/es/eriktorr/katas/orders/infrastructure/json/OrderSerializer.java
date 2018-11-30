package es.eriktorr.katas.orders.infrastructure.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import es.eriktorr.katas.orders.domain.common.SingleValue;
import es.eriktorr.katas.orders.domain.model.WithOrderInformation;

import java.io.IOException;

import static es.eriktorr.katas.orders.domain.common.StringUtils.trim;
import static es.eriktorr.katas.orders.infrastructure.json.OrderFields.*;

public class OrderSerializer<T extends WithOrderInformation> extends StdSerializer<T> {

    public OrderSerializer(Class<T> handleType) {
        super(handleType);
    }

    @Override
    public void serialize(T order, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
        jsonGenerator.writeStartObject();
        writeOptional(jsonGenerator, ORDER_ID_FIELD, order.getOrderId());
        writeOptional(jsonGenerator, ORDER_STORE_FIELD, order.getStoreId());
        writeOptional(jsonGenerator, ORDER_REFERENCE_FIELD, order.getOrderReference());
        writeOptional(jsonGenerator, ORDER_CREATED_AT_FIELD, order.getCreatedAt());
        jsonGenerator.writeEndObject();
    }

    private <V extends SingleValue> void writeOptional(JsonGenerator jsonGenerator, String field, V singleValue) throws IOException {
        if (singleValue == null) return;
        final String trimmedValue = trim(singleValue.getValue());
        if (!"".equals(trimmedValue)) {
            jsonGenerator.writeStringField(field, trimmedValue);
        }
    }

    private <V> void writeOptional(JsonGenerator jsonGenerator, String field, V object) throws IOException {
        if (object != null) {
            jsonGenerator.writeObjectField(field, object);
        }
    }

}