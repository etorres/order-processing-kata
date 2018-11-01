package es.eriktorr.katas.orders.infrastructure.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.eriktorr.katas.orders.domain.model.Order;
import es.eriktorr.katas.orders.infrastructure.common.JsonMapper;

import java.io.IOException;

public class OrderJsonMapper extends JsonMapper<Order> {

    public OrderJsonMapper(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public String toJson(Order object) {
        if (object == null) return null;
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("failed to serialize object to JSON: " + object, e);
        }
    }

    @Override
    public Order fromJson(String payload) {
        if (payload == null) return null;
        try {
            return objectMapper.readValue(payload, Order.class);
        } catch (IOException e) {
            throw new IllegalStateException("failed to deserialize payload: " + payload, e);
        }
    }

}